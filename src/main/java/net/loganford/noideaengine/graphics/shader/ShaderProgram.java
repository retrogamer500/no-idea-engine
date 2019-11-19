package net.loganford.noideaengine.graphics.shader;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.graphics.CubeMap;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import java.nio.FloatBuffer;

@Log4j2
public class ShaderProgram extends Resource implements UnsafeMemory {

	public static final FloatBuffer FB16 = BufferUtils.createFloatBuffer(16);
	public static final Vector4f DEFAULT_COLOR = new Vector4f(1f, 1f, 1f, 1f);

	@Getter private int programId;
	@Getter private VertexShader vertexShader;
	@Getter private FragmentShader fragmentShader;

	private int[] cachedUniformLocations = new int[ShaderUniform.values().length];
	private int boundTextures = 0;

	public ShaderProgram(int programId, String key, VertexShader vertexShader, FragmentShader fragmentShader) {
		this.programId = programId;
		this.vertexShader = vertexShader;
		this.fragmentShader = fragmentShader;

		//Cache locations of shader uniforms
		log.debug("Processing shader uniforms for: " + key);

		for(int i = 0; i < cachedUniformLocations.length; i++) {
            ShaderUniform uniform = ShaderUniform.values()[i];
            String name = uniform.getUniformName();
            int location = GL33.glGetUniformLocation(programId, name);
            log.debug("Uniform found. Name: " + name + " Location: " + location);
            cachedUniformLocations[i] = location;
		}
	}

    public int getUniformLocation(String name) {
        return GL33.glGetUniformLocation(programId, name);
    }

    //setUniform shortcuts

	public void setUniform(ShaderUniform uniform, Vector2f vector) {
        setUniform(getLocation(uniform), vector);
    }

    public void setUniform(ShaderUniform uniform, Vector3f vector) {
        setUniform(getLocation(uniform), vector);
    }

    public void setUniform(ShaderUniform uniform, Vector4f vector) {
        setUniform(getLocation(uniform), vector);
    }

    public void setUniform(ShaderUniform uniform, Matrix4f matrix) {
        setUniform(getLocation(uniform), matrix);
    }

    public void setUniform(ShaderUniform uniform, Texture texture) {
        setUniform(getLocation(uniform), texture);
    }

    public void setUniform(ShaderUniform uniform, CubeMap cubeMap) {
        setUniform(getLocation(uniform), cubeMap);
    }

    //setUniforms

    public void setUniform(int location, Vector2f vector) {
        GL33.glUniform2f(location, vector.x, vector.y);
    }

    public void setUniform(int location, Vector3f vector) {
        GL33.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    public void setUniform(int location, Vector4f vector) {
        GL33.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    public void setUniform(int location, Matrix4f matrix) {
        FloatBuffer buffer = matrix.get(ShaderProgram.FB16);
        GL33.glUniformMatrix4fv(location, false, buffer);
    }

    public void setUniform(int location, Texture texture) {
        GL33.glActiveTexture(GL33.GL_TEXTURE0 + boundTextures);
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, texture.getTextureId());
        GL33.glUniform1i(location, boundTextures);
        boundTextures++;
    }

    public void setUniform(int location, CubeMap cubeMap) {
        GL33.glActiveTexture(GL33.GL_TEXTURE0 + boundTextures);
        GL33.glBindTexture(GL33.GL_TEXTURE_CUBE_MAP, cubeMap.getTextureId());
        GL33.glUniform1i(location, boundTextures);
        boundTextures++;
    }


    public void resetBoundTextures(Renderer renderer) {
        boundTextures = 0;
    }

    private int getLocation(ShaderUniform uniform) {
	    return cachedUniformLocations[uniform.ordinal()];
    }

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ShaderProgram) {
			return ((ShaderProgram) obj).programId == programId;
		}
		return super.equals(obj);
	}

    @Override
    public void freeMemory() {
	    GL33.glDeleteProgram(programId);
    }
}
