package net.loganford.noideaengine.shape;

import lombok.Getter;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Texture;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Bitmap extends Shape {
    private static HashMap<String, boolean[][]> bitmapCache = new HashMap<>();

    @Getter private Vector3f position = new Vector3f();
    @Getter private boolean[][] bitmap;
    private Image image;

    public Bitmap(Image image) {
        this.image = image;

        bitmap = bitmapCache.get(imageHash(image));
        if(bitmap == null) {
            bitmap = new boolean[(int) image.getWidth()][(int) image.getHeight()];
            Texture tex = image.getTexture();
            int x1 = (int) (tex.getWidth() * image.getU0());
            int x2 = (int) (tex.getWidth() * image.getU1());

            int y1 = (int) (tex.getHeight() * image.getV0());
            int y2 = (int) (tex.getHeight() * image.getV1());

            GL33.glBindTexture(GL33.GL_TEXTURE_2D, tex.getTextureId());
            ByteBuffer pixels = BufferUtils.createByteBuffer((int) (tex.getWidth() * tex.getHeight() * 4));
            GL33.glGetTexImage(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, pixels);
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);


            for (int j = 0; j < image.getHeight(); j++) {
                for (int i = 0; i < image.getWidth(); i++) {
                    int index = ((i + x1) + (j + y1) * tex.getHeight()) * 4;
                    bitmap[i][j] = (pixels.get(index + 3) & 0xFF) != 0;
                }
            }

            bitmapCache.put(imageHash(image), bitmap);
        }
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(this.position);
    }

    @Override
    public void setPosition(Vector3fc position) {
        this.position.set(position);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setPosition(position);
        cube.setWidth(image.getWidth());
        cube.setHeight(image.getHeight());
        cube.setDepth(0);
    }

    private String imageHash(Image image) {
        Texture tex = image.getTexture();
        return tex.getTextureId() + "|" + image.getU0() + "|" + image.getU1() + "|" + image.getV0() + "|" + image.getV1();
    }
}
