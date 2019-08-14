package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.utils.UnsafeMemoryTracker;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Polygon implements UnsafeMemory {

    private List<Vector2f> points;
    private int vertexCount;
    private Matrix4f modelMatrix;
    @Getter private Vector4f color = new Vector4f(1f, 1f, 1f, 1f);
    @Getter @Setter private float scaleX = 1f;
    @Getter @Setter private float scaleY = 1f;
    @Getter @Setter private float angle = 0f;
    @Getter private VertexArrayObject vao;

    Polygon(Vector2f[] points) {
        modelMatrix = new Matrix4f();
        this.points = Arrays.asList(points);
        init();
    }

    public interface PolygonGenerator {
        Vector2f generate(int pointNum);
    }

    Polygon(int pointCount, PolygonGenerator generator) {
        points = new ArrayList<>();
        modelMatrix = new Matrix4f();
        for(int i = 0; i < pointCount; i++) {
            points.add(generator.generate(i));
        }
        init();
    }

    private void init() {
        vertexCount = points.size();

        vao = new VertexArrayObject();
        VertexArrayObject.VertexBufferObject positionVbo = vao.addVertexBufferObject(3, vertexCount);
        VertexArrayObject.VertexBufferObject normalVbo = vao.addVertexBufferObject(3, vertexCount);
        VertexArrayObject.VertexBufferObject uvVbo = vao.addVertexBufferObject(2, vertexCount);
        VertexArrayObject.VertexBufferObject colorVbo = vao.addVertexBufferObject(4, vertexCount);

        for(Vector2f point : points) {
            positionVbo.put(point.x);
            positionVbo.put(point.y);
            positionVbo.put(0);

            normalVbo.put(0);
            normalVbo.put(0);
            normalVbo.put(-1);

            uvVbo.put(point.x);
            uvVbo.put(point.y);

            colorVbo.put(1f);
            colorVbo.put(1f);
            colorVbo.put(1f);
            colorVbo.put(1f);
        }

        vao.flipAndBuffer();
        UnsafeMemoryTracker.track(this);
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    /**
     * Renders a filled polygon. Will only work properly for convex polygons.
     * @param renderer
     * @param x
     * @param y
     */
    public void render(Renderer renderer, float x, float y) {
        renderer.getTextureBatch().flush(renderer);

        renderer.pushShader(renderer.getShaderSolid());
        GL33.glDisable(GL33.GL_CULL_FACE);
        GL33.glDisable(GL33.GL_DEPTH_TEST);

        modelMatrix.identity().translate(x, y, 0).rotateZ(angle).scale(scaleX, scaleY, 1f);

        //Populate uniforms
        ShaderProgram shader = renderer.getShader();
        shader.setUniform(ShaderUniform.COLOR, color);
        shader.setUniform(ShaderUniform.MODEL, modelMatrix);
        shader.setUniform(ShaderUniform.VIEW, renderer.getView().getViewMatrix());
        shader.setUniform(ShaderUniform.PROJECTION, renderer.getView().getProjectionMatrix());

        //Bind vertex array
        GL33.glBindVertexArray(getVao().getId());

        //Render
        GL33.glDrawArrays(GL33.GL_TRIANGLE_FAN, 0, vertexCount);
        GL33.glEnable(GL33.GL_CULL_FACE);
        shader.resetBoundTextures(renderer);
        renderer.popShader();
    }

    public void renderOutline(Renderer renderer, float x, float y) {
        renderer.pushShader(renderer.getShaderSolid());
        GL33.glDisable(GL33.GL_CULL_FACE);
        GL33.glDisable(GL33.GL_DEPTH_TEST);

        modelMatrix.identity().translate(x, y, 0).rotateZ(angle).scale(scaleX, scaleY, 1f);

        //Populate uniforms
        ShaderProgram shader = renderer.getShader();
        shader.setUniform(ShaderUniform.COLOR, color);
        shader.setUniform(ShaderUniform.MODEL, modelMatrix);
        shader.setUniform(ShaderUniform.VIEW, renderer.getView().getViewMatrix());
        shader.setUniform(ShaderUniform.PROJECTION, renderer.getView().getProjectionMatrix());

        //Bind vertex array
        GL33.glBindVertexArray(getVao().getId());

        //Render
        GL33.glDrawArrays(GL33.GL_LINE_LOOP, 0, vertexCount);
        GL33.glEnable(GL33.GL_CULL_FACE);
        shader.resetBoundTextures(renderer);
        renderer.popShader();
    }

    @Override
    public void freeMemory() {
        vao.freeMemory();
        UnsafeMemoryTracker.untrack(this);
    }
}
