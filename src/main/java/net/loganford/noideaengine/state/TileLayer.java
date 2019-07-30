package net.loganford.noideaengine.state;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.graphics.UnsafeMemory;
import net.loganford.noideaengine.graphics.shader.ShaderProgram;
import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.utils.UnsafeMemoryTracker;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class TileLayer extends Entity implements UnsafeMemory {
    private static Vector2f V2F = new Vector2f();

    /**A texture with all the tiles in it*/
    @Getter private Image tileImage;
    /**Width of the tile layer, in tiles*/
    @Getter private int width;
    /**Height of the tile layer, in tiles*/
    @Getter private int height;
    /**Width of each tile, in pixels*/
    @Getter private float tileWidth;
    /**Height of each tile, in pixels*/
    @Getter private float tileHeight;
    /**Texture where each 4 bytes is the position of a tile, recalculated every frame*/
    private Texture tileLookupTexture;
    /**Buffer which backs up the tile lookup texture*/
    private ByteBuffer tileLookupBuffer;
    /**Whether or not the tile layer has been modified in the current frame*/
    private boolean dirty = false;

    protected TileLayer(Image tileImage, int width, int height, float tileWidth, float tileHeight) {
        this.tileImage = tileImage;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        tileLookupBuffer = MemoryUtil.memCalloc(width * height * 4);
        tileLookupTexture = new Texture(width, height, tileLookupBuffer, false);

        UnsafeMemoryTracker.track(this);
    }

    /**
     * Adds a tile at a position. Note that these coordinates are NOT in pixels, but in units of tile size.
     * @param x x position to add the tile
     * @param y y position to add the tile
     * @param tileX x position in tile atlas
     * @param tileY y position in til atlas
     */
    public void setTile(int x, int y, int tileX, int tileY) {
        int index = (y * width + x) * 4;
        tileLookupBuffer.putInt(index, getTileLookupHash(tileX, tileY));
        dirty = true;
    }

    /**
     * Removes a tile at a position. Note that these coordinates are NOT in pixels, but in units of tile size.
     * @param x x position of tile to remove
     * @param y y position of tile to remove
     */
    public void removeTile(int x, int y) {
        int index = (y * width + x) * 4;
        tileLookupBuffer.putInt(index, 0);
        dirty = true;
    }

    private int getTileLookupHash(int tileX, int tileY) {
        return ((tileX + 1) << 16) + (tileY + 1);
    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {
        if(dirty) {
            tileLookupBuffer.clear();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, tileLookupTexture.getTextureId());
            GL33.glTexSubImage2D(GL33.GL_TEXTURE_2D, 0, 0, 0, width, height, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, tileLookupBuffer);
            dirty = false;
        }

        ShaderProgram oldShader = renderer.getShader();
        renderer.setShader(renderer.getShaderTile());

        renderer.getShader().setUniform(ShaderUniform.TEX_DIFFUSE, tileImage.getTexture());
        renderer.getShader().setUniform(ShaderUniform.TEX_TILE_LOOKUP, tileLookupTexture);

        renderer.getShader().setUniform(ShaderUniform.TILE_UV0, V2F.set(tileImage.getU0(), tileImage.getV0()));
        renderer.getShader().setUniform(ShaderUniform.TILE_UV_SIZE, V2F.set(tileWidth/tileImage.getTexture().getWidth(), tileHeight/tileImage.getTexture().getHeight()));
        renderer.getShader().setUniform(ShaderUniform.TILE_SIZE, V2F.set(tileWidth, tileHeight));

        renderer.drawQuad(0, 0, width * tileWidth, height * tileHeight);
        renderer.setShader(oldShader);
    }

    @Override
    public void onDestroy(Game game, Scene scene) {
        freeMemory();
        super.onDestroy(game, scene);
    }

    @Override
    public void freeMemory() {
        UnsafeMemoryTracker.untrack(this);
        tileLookupTexture.freeMemory();
        MemoryUtil.memFree(tileLookupBuffer);
    }
}
