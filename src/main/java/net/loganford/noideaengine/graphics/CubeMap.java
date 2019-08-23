package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.utils.memory.UnsafeMemory;

@Deprecated
public class CubeMap implements UnsafeMemory {
    @Override
    public void freeMemory() {

    }

    /*private Texture[] textures = new Texture[6];
    private int textureId = -1;
    private Model skyboxModel;

    public CubeMap(Texture top, Texture north, Texture east, Texture south, Texture west, Texture bottom) {
        textures[0] = east;
        textures[1] = west;
        textures[2] = top;
        textures[3] = bottom;
        textures[4] = north;
        textures[5] = south;
    }

    public void init(Game game) {
        if(textureId == -1) {
            skyboxModel = null;//game.getModelManager().get("default/2cube.obj");
            textureId = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_CUBE_MAP, textureId);
            for(int i = 0; i < textures.length; i++) {
                Texture texture = textures[i];
                GL33.glTexImage2D(GL33.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL33.GL_RGBA8, texture.getWidth(), texture.getHeight(), 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, texture.getByteBuffer());
            }

            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_R, GL33.GL_CLAMP_TO_EDGE);

            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
        }
    }

    public void render(Game game, GameState gameState, ScreenTransformation viewProjection, Renderer renderer) {

        GL33.glEnable(GL33.GL_DEPTH_TEST);
        GL33.glDepthMask(false);
        GL33.glCullFace(GL33.GL_FRONT); //Cull opposite side since we are "inside" the skybox
        GL33.glDepthFunc(GL33.GL_LEQUAL);

        ShaderProgram oldShader = renderer.getShader();
        ShaderProgram shader = game.getShaderManager().get("default/skybox.shader");
        renderer.setShader(shader);
        shader.setProjectionMatrix(viewProjection.getProjectionMatrix());
        shader.setShaderUniform(shader.getUniformLocation("skybox"), this);
        shader.resetBoundTextures();

        //Bind vertex array
        GL33.glBindVertexArray(skyboxModel.getMeshes().get(0).getVao().getId());

        //Render
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, skyboxModel.getMeshes().get(0).getVertexCount());

        GL33.glCullFace(GL33.GL_BACK);
        renderer.setShader(oldShader);
        GL33.glDepthMask(true);
        GL33.glDepthFunc(GL33.GL_LESS);
    }

    public int getTextureId() {
        return textureId;
    }

    @Override
    public void freeMemory() {
        //Todo: do we need to free memory?
    }
    */
}
