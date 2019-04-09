package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.state.GameState;
import net.loganford.noideaengine.utils.UnsafeMemoryTracker;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;

@Log4j2
public class FrameBufferObject implements UnsafeMemory {

    @Getter int width;
    @Getter private int height;

    @Getter private int framebufferId;
    @Getter private int renderbufferId;

    @Getter private Image[] colorAttachments;
    @Getter private boolean depthAttachment;
    @Getter private Vector4f color = new Vector4f(1f, 1f, 1f, 1f);

    private Matrix4f modelMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();

    public FrameBufferObject(Game game, int width, int height, int attachmentNum, boolean depthAttachment) {
        this.width = width;
        this.height = height;
        this.colorAttachments = new Image[attachmentNum];
        this.depthAttachment = depthAttachment;

        init(game);
    }

    private void init(Game game) {
        log.info("Creating FBO...");

        // Generate framebuffer
        framebufferId = GL33.glGenFramebuffers();
        GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, framebufferId);

        // Generate color attachments
        for (int i = 0; i < colorAttachments.length; i++) {
            // Create texture
            int textureId = GL33.glGenTextures();
            log.info("texture ID: " + textureId);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);
            //GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGB32F, width, height, 0, GL33.GL_RGBA, GL33.GL_FLOAT, 0);
            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, width, height, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, 0);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
            GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
            // Attach texture
            GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0 + i, GL33.GL_TEXTURE_2D, textureId, 0);
            // Create texture for rendering this FBO
            Texture texture = new Texture(width, height, textureId);
            Image image = new Image(texture, width, height, 0, 0, 1, 1);
            colorAttachments[i] = image;
        }


        // Generate depth attachment
        if (depthAttachment) {
            // Generate renderState buffer
            renderbufferId = GL33.glGenRenderbuffers();
            GL33.glBindRenderbuffer(GL33.GL_RENDERBUFFER, renderbufferId);
            GL33.glRenderbufferStorage(GL33.GL_RENDERBUFFER, GL33.GL_DEPTH_COMPONENT, width, height);

            // Attach depth
            GL33.glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER,
                    renderbufferId);
        }


        if (!statusCheck()) {
            throw new GameEngineException("Error generating framebuffer!");
        }

        log.info("FBO " + framebufferId + " created");

        // Unbind framebuffer because we don't want the creating to hijack
        // following draw calls
        GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, 0);

        UnsafeMemoryTracker.track(this);
    }

    public boolean statusCheck() {
        int status = GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER);

        if (status != GL33.GL_FRAMEBUFFER_COMPLETE) {
            switch (status) {
                case GL33.GL_FRAMEBUFFER_UNDEFINED:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_UNDEFINED");
                    break;
                case GL33.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
                    break;
                case GL33.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
                    break;
                case GL33.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
                    break;
                case GL33.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
                    break;
                case GL33.GL_FRAMEBUFFER_UNSUPPORTED:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_UNSUPPORTED");
                    break;
                case GL33.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
                    break;
                case GL33.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS:
                    log.fatal("Error while generating FBO: GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS");
                    break;
            }

            log.fatal("Framebuffer error code: " + status);
            return false;
        }

        return true;
    }

    public void use() {
        GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, framebufferId);
        Renderer.errorCheck();
    }

    public static void useDefault() {
        GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, 0);
    }

    public void renderToScreen(Game game, GameState gameState, Renderer renderer) {
        renderer.getTextureBatch().flush(renderer);

        GL33.glDisable(GL33.GL_DEPTH_TEST);

        projectionMatrix.identity().ortho(0, game.getWindow().getWidth(), game.getWindow().getHeight(), 0, -100f, 100f);
        viewMatrix.identity();
        modelMatrix.identity().scale(game.getWindow().getWidth(), game.getWindow().getHeight(), 1);


        //Populate uniforms
        ShaderProgram shader = renderer.getShader();
        shader.setUniform(ShaderUniform.MODEL, modelMatrix);
        shader.setUniform(ShaderUniform.VIEW, viewMatrix);
        shader.setUniform(ShaderUniform.PROJECTION, projectionMatrix);
        shader.setUniform(ShaderUniform.TEX_DIFFUSE, colorAttachments[0].getTexture());
        shader.setUniform(ShaderUniform.COLOR, color);

        //Bind vertex array
        GL33.glBindVertexArray(renderer.getModelQuadFlipped().getMeshes().get(0).getVao().getId());

        //Render
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 6);
        shader.resetBoundTextures(renderer);
    }

    public Image getImage() {
        return colorAttachments[0];
    }

    public void render(Renderer renderer, float x, float y) {
        colorAttachments[0].render(renderer, x, y);
    }

    @Override
    public void freeMemory() {
        for (int i = 0; i < colorAttachments.length; i++) {
            colorAttachments[i].getTexture().freeMemory();
        }

        if (depthAttachment) {
            GL33.glDeleteRenderbuffers(renderbufferId);
        }

        GL33.glDeleteFramebuffers(framebufferId);
        log.info("FBO " + framebufferId + " destroyed");

        UnsafeMemoryTracker.untrack(this);
    }
}
