package net.loganford.noideaengine.graphics;

import lombok.Getter;
import net.loganford.noideaengine.resources.Resource;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import net.loganford.noideaengine.utils.memory.UnsafeMemoryTracker;
import org.lwjgl.opengl.GL33;

import java.nio.ByteBuffer;

public class Texture extends Resource implements UnsafeMemory {

    @Getter private int textureId = -1;
    @Getter private int width;
    @Getter private int height;
    @Getter private Image image;

    /**
     * Creates a texture from an already initialized texture.
     *
     * @param width
     * @param height
     */
    public Texture(int width, int height, int textureId) {
        this.width = width;
        this.height = height;
        this.textureId = textureId;
        init(null, false);
    }

    /**
     * Creates a texture from RGBA data.
     *
     * @param width
     * @param height
     * @param data
     */
    public Texture(int width, int height, ByteBuffer data) {
        this.width = width;
        this.height = height;
        init(data, false);
    }

    public Texture(int width, int height, ByteBuffer data, boolean generateMipmaps) {
        this.width = width;
        this.height = height;
        init(data, generateMipmaps);
    }

    private void init(ByteBuffer data, boolean generateMipmaps) {
        if (textureId == -1) {
            textureId = GL33.glGenTextures();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, textureId);

            GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA8, width, height, 0, GL33.GL_RGBA,
                    GL33.GL_UNSIGNED_BYTE, data);

            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
            GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
            GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);

            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
        }

        image = new Image(this, width, height, 0, 0, 1, 1);
        UnsafeMemoryTracker.track(this);
    }

    public void freeMemory() {
        GL33.glDeleteTextures(textureId);
        UnsafeMemoryTracker.untrack(this);
    }
}
