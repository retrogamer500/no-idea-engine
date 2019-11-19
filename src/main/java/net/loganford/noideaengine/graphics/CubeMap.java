package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.lwjgl.opengl.GL33;

public class CubeMap extends Resource implements UnsafeMemory {

    private int textureId;

    public CubeMap(int textureId) {
        this.textureId = textureId;
    }

    public void render(Renderer renderer) {

        boolean popShader = false;
        if(renderer.getShader() == null) {
            renderer.pushShader(renderer.getSkyboxShader());
            popShader = true;
        }

        renderer.getTextureBatch().flush(renderer);

        boolean cullingBackface = renderer.isCullingBackface();
        renderer.setCullingBackface(false);
        GL33.glEnable(GL33.GL_DEPTH_TEST);
        GL33.glDepthMask(false);
        GL33.glDepthFunc(GL33.GL_LEQUAL);

        renderer.getShader().setUniform(ShaderUniform.PROJECTION, renderer.getCamera().getProjectionMatrix());
        renderer.getShader().setUniform(ShaderUniform.TEX_SKYBOX, this);

        //Bind vertex array
        GL33.glBindVertexArray(renderer.getModel2Cube().getMeshes().get(0).getVao().getId());

        //Render
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, renderer.getModel2Cube().getMeshes().get(0).getVertexCount());

        GL33.glDepthMask(true);
        GL33.glDepthFunc(GL33.GL_LESS);
        renderer.getShader().resetBoundTextures(renderer);
        renderer.setCullingBackface(cullingBackface);

        if(popShader) {
            renderer.popShader();
        }
    }

    public int getTextureId() {
        return textureId;
    }

    @Override
    public void freeMemory() {
        GL33.glDeleteTextures(textureId);
    }
}
