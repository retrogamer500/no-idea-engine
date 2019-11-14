package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.shape.AbstractCompoundShape;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.state.AbstractViewProjection;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import net.loganford.noideaengine.utils.memory.UnsafeMemoryTracker;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Mesh implements UnsafeMemory {

	@Getter private int vertexCount = -1;
	@Getter private VertexArrayObject vao;

	@Getter @Setter private Material material;

	@Getter @Setter private List<Face> faces;
	@Getter @Setter private List<Vector3f> points;
	@Getter @Setter private List<Vector3f> normals;
	@Getter @Setter private List<Vector2f> uvCoordinates;

	private Matrix4f modelMatrix;

	public Mesh() {
		faces = new ArrayList<>();
		points = new ArrayList<>();
		normals = new ArrayList<>();
		uvCoordinates = new ArrayList<>();

        modelMatrix = new Matrix4f();
	}

	public void init(boolean freeMemory) {
		vertexCount = getFaces().size() * 3;

		vao = new VertexArrayObject();
		VertexArrayObject.VertexBufferObject positionVbo = vao.addVertexBufferObject(3, vertexCount);
		VertexArrayObject.VertexBufferObject normalVbo = vao.addVertexBufferObject(3, vertexCount);
		VertexArrayObject.VertexBufferObject uvVbo = vao.addVertexBufferObject(2, vertexCount);
		VertexArrayObject.VertexBufferObject colorVbo = vao.addVertexBufferObject(4, vertexCount);

		for (Face face : getFaces()) {
			for (Vector3f vector : face.getPositions()) {
				positionVbo.put(vector);

				//Populate color
				colorVbo.put(1f);
				colorVbo.put(1f);
				colorVbo.put(1f);
				colorVbo.put(1f);
			}
			for (Vector3f vector : face.getNormals()) {
				normalVbo.put(vector);
			}
			for (Vector2f vector : face.getUvs()) {
				uvVbo.put(vector);
			}
		}

		vao.flipAndBuffer();

		// Since the data has been transferred to the GPU, remove from RAM if requested
		if (freeMemory) {
			faces = null;
			points = null;
			normals = null;
			uvCoordinates = null;
		}

		UnsafeMemoryTracker.track(this);
	}

	public void render(Renderer renderer, float x, float y) {
		modelMatrix = modelMatrix.identity().translate(x, y, 0);
		GL33.glDisable(GL33.GL_DEPTH_TEST);
		render(renderer, renderer.getView(), modelMatrix);
	}

	public void render(Renderer renderer, float x, float y, float z) {
		modelMatrix = modelMatrix.identity().translate(x, y, z);
		GL33.glEnable(GL33.GL_DEPTH_TEST);
		render(renderer, renderer.getCamera(), modelMatrix);
	}

	public void render(Renderer renderer, Matrix4fc matrix) {
		modelMatrix.set(matrix);
		GL33.glEnable(GL33.GL_DEPTH_TEST);
		render(renderer, renderer.getCamera(), modelMatrix);
	}

	private void render(Renderer renderer, AbstractViewProjection viewProjection, Matrix4f modelMatrix) {
		renderer.getTextureBatch().flush(renderer);

		//Populate uniforms
		ShaderProgram shader = renderer.getShader();
		shader.setUniform(ShaderUniform.COLOR, ShaderProgram.DEFAULT_COLOR);
		shader.setUniform(ShaderUniform.MODEL, modelMatrix);
		shader.setUniform(ShaderUniform.VIEW, viewProjection.getViewMatrix());
		shader.setUniform(ShaderUniform.PROJECTION, viewProjection.getProjectionMatrix());

		if(material != null) {
			shader.setUniform(ShaderUniform.TEX_DIFFUSE, material.getDiffuse().getTexture());
		}

        shader.setUniform(ShaderUniform.LIGHT_COLOR, renderer.getLightColor());
        shader.setUniform(ShaderUniform.LIGHT_DIRECTION, renderer.getLightDirection());
        shader.setUniform(ShaderUniform.AMBIENT_LIGHT_COLOR, renderer.getAmbientLightColor());

		//Bind vertex array
		GL33.glBindVertexArray(getVao().getId());

		//Render
		GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, vertexCount);
		shader.resetBoundTextures(renderer);
	}

	@Override
	public void freeMemory() {
		vao.freeMemory();
		UnsafeMemoryTracker.untrack(this);
	}

    public MeshShape getShape() {
        return new MeshShape(this);
    }

	public static class MeshShape extends AbstractCompoundShape {
        private Mesh mesh;

        public MeshShape(Mesh mesh) {
            this.mesh = mesh;
        }


		@SuppressWarnings("unchecked")
		@Override
		public Iterator<Shape> iterator() {
			return (Iterator) mesh.faces.iterator();
		}
	}
}
