package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.resources.PrototypeResource;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

public class Image extends PrototypeResource {
    @Getter private float u0 = 0f;
    @Getter private float v0 = 0f;
    @Getter private float u1 = 1f;
    @Getter private float v1 = 1f;

    @Getter private float width;
    @Getter private float height;

    @Getter @Setter private Texture texture;
    @Getter private ByteBuffer data;

    @Getter @Setter private Vector4f color = new Vector4f(1f, 1f, 1f, 1f);
    @Getter @Setter private float angle = 0;
    @Getter @Setter private float scaleX = 1;
    @Getter @Setter private float scaleY = 1;

    private boolean initialized = false;

    /**
     * Creates and initializes an image.
     * @param texture
     * @param width
     * @param height
     * @param u0
     * @param v0
     * @param u1
     * @param v1
     */
    public Image(Texture texture, float width, float height, float u0, float v0, float u1, float v1) {
        init(texture, width, height, u0, v0, u1, v1);
    }

    /**
     * No-op constructor. This image needs to be initialized with more data before it can be used.
     */
    private Image() {}

    /**
     * Creates an uninitialized image for use in later storing the image into a texture atlas for rendering. This is a
     * static method rather than a constructor for clarity. This image must be initialized prior to drawing.
     * @param width
     * @param height
     * @param data
     * @return
     */
    public static Image createUninitializedImage(float width, float height, ByteBuffer data) {
        Image image = new Image();
        image.width = width;
        image.height = height;
        image.data = data;
        return image;
    }

    /**
     * Initializes this image to use a certain texture, and certain UV coordinates. This has no effect if the image is
     * already initialized.
     *
     * @param texture
     * @param u0
     * @param v0
     * @param u1
     * @param v1
     */
    public void init(Texture texture, float width, float height, float u0, float v0, float u1, float v1) {
        if(!initialized) {
            this.texture = texture;
            this.u0 = u0;
            this.v0 = v0;
            this.u1 = u1;
            this.v1 = v1;
            this.width = width;
            this.height = height;
            data = null;
            initialized = true;
        }
    }

    /**
     * Creates a new image from a part of this image.
     * @param x
     * @param y
     * @param width
     * @param height
     * @return an initialized image
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    public Image getSubImage(float x, float y, float width, float height) {
        if(x < 0 || y < 0 || width > this.width || height > this.height) {
            throw new GameEngineException("Tried to get subimage out of bounds of existing image");
        }

        Image image = new Image(texture, width, height, this.u0 + x/texture.getWidth(), this.v0 + y/texture.getHeight(),
                this.u0 + (x + width)/texture.getWidth(), this.v0 + (y + height)/texture.getHeight());

        return image;
    }

    public void render(Renderer renderer, float x, float y) {
        render(renderer, x, y, width, height, u0, v0, u1, v1);
    }

    public void render(Renderer renderer, float x, float y, float width, float height, float u0, float v0, float u1, float v1) {
        renderer.getTextureBatch().put(renderer, this, x, y, width, height, u0, v0, u1, v1);
    }
}
