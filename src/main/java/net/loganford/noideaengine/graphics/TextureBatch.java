package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.utils.UnsafeMemoryTracker;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class TextureBatch implements UnsafeMemory {

    private static Vector3f v3 = new Vector3f();
    private static Matrix4f identity = new Matrix4f();
    public static final int MAX_QUADS = 512;
    public static final int MAX_VERTICES = 4 * MAX_QUADS; //512 quads
    public static final int INDEX_SIZE = 6 * MAX_QUADS;

    private int quads = 0;

    private VertexArrayObject vao;
    private VertexArrayObject.VertexBufferObject positionVbo;
    private VertexArrayObject.VertexBufferObject normalVbo;
    private VertexArrayObject.VertexBufferObject uvVbo;
    private VertexArrayObject.VertexBufferObject colorVbo;

    private int indicesVbo;

    private Texture currentTexture;

    @SuppressWarnings("PointlessArithmeticExpression")
    public TextureBatch() {
        vao = new VertexArrayObject();
        positionVbo = vao.addVertexBufferObject(3, MAX_VERTICES, GL33.GL_STREAM_DRAW);
        normalVbo = vao.addVertexBufferObject(3, MAX_VERTICES, GL33.GL_STREAM_DRAW);
        uvVbo = vao.addVertexBufferObject(2, MAX_VERTICES, GL33.GL_STREAM_DRAW);
        colorVbo = vao.addVertexBufferObject(4, MAX_VERTICES, GL33.GL_STREAM_DRAW);
        vao.buffer();

        //Create index arrays
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer indices = stack.mallocInt(INDEX_SIZE);
            int index = 0;
            for (int i = 0; i < MAX_QUADS; i++) {
                indices.put(index + 0);
                indices.put(index + 2);
                indices.put(index + 1);

                indices.put(index + 1);
                indices.put(index + 2);
                indices.put(index + 3);

                index += 4;
            }
            indices.flip();
            GL33.glBindVertexArray(vao.getId());
            indicesVbo = GL33.glGenBuffers();
            GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, indicesVbo);
            GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, indices, GL33.GL_STATIC_DRAW);
            GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        UnsafeMemoryTracker.track(this);
    }

    public void put(Renderer renderer, Image image, float x, float y, float width, float height, float u0, float v0, float u1, float v1) {
        //Flush if buffer is full
        if(quads >= MAX_QUADS) {
            flush(renderer);
        }

        //flush if texture has changed
        Texture texture = image.getTexture();
        if(currentTexture == null) {
            currentTexture = texture;
        }
        else {
            if(texture.getTextureId() != currentTexture.getTextureId()) {
                flush(renderer);
                currentTexture = texture;
            }
        }

        put(renderer, image, v3.set(0, 0, 0), x, y, u0, v0);
        put(renderer, image, v3.set(width, 0, 0), x, y, u1, v0);
        put(renderer, image, v3.set(0, height, 0), x, y, u0, v1);
        put(renderer, image, v3.set(width, height, 0), x, y, u1, v1);

        quads++;
    }

    private void put(Renderer renderer, Image image, Vector3f pos, float x, float y, float u, float v) {
        pos.add(-image.getOffsetX(), -image.getOffsetY(), 0f);
        pos.rotateZ(-image.getAngle());
        pos.mul(image.getScaleX(), image.getScaleY(), 1f);
        pos.add(x, y, 0);

        positionVbo.put(pos);

        normalVbo.put(0);
        normalVbo.put(0);
        normalVbo.put(1);

        uvVbo.put(u);
        uvVbo.put(v);

        colorVbo.put(image.getColor());
    }

    public void flush(Renderer renderer) {
        if(quads != 0) {
            boolean cullingBackface = renderer.isCullingBackface();
            renderer.setCullingBackface(false);
            vao.flipAndUpdate(0);

            //Populate uniforms
            ShaderProgram shader = renderer.getShader();

            shader.setUniform(ShaderUniform.COLOR, ShaderProgram.DEFAULT_COLOR);

            shader.setUniform(ShaderUniform.MODEL, identity);
            shader.setUniform(ShaderUniform.VIEW, renderer.getView().getViewMatrix());
            shader.setUniform(ShaderUniform.PROJECTION, renderer.getView().getProjectionMatrix());
            shader.setUniform(ShaderUniform.TEX_DIFFUSE, currentTexture);

            shader.setUniform(ShaderUniform.LIGHT_COLOR, renderer.getLightColor());
            shader.setUniform(ShaderUniform.LIGHT_DIRECTION, renderer.getLightDirection());
            shader.setUniform(ShaderUniform.AMBIENT_LIGHT_COLOR, renderer.getAmbientLightColor());

            //Bind vertex array
            GL33.glBindVertexArray(vao.getId());
            //Bind indices
            GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, indicesVbo);

            //Render
            GL33.glDrawElements(GL33.GL_TRIANGLES, quads * 6, GL33.GL_UNSIGNED_INT, 0);

            shader.resetBoundTextures(renderer);
            quads = 0;
            vao.clear();
            currentTexture = null;
            renderer.setCullingBackface(cullingBackface);
        }
    }

    @Override
    public void freeMemory() {
        vao.freeMemory();
        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL33.glDeleteBuffers(indicesVbo);
        UnsafeMemoryTracker.untrack(this);
}
}
