package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.graphics.shader.ShaderUniform;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.AbstractCollisionComponent;
import net.loganford.noideaengine.state.entity.components.UnregisterComponent;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import net.loganford.noideaengine.utils.memory.UnsafeMemoryTracker;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * A tile layer. This class is a subclass of Entity, so that it can be moved, or so the depth can be changed. Rendering
 * a tile layer can be done in O(1) time, no matter how many tiles there are in the layer. The only constraint is that
 * the tile layer must be less than 65535 tiles (not pixels) in size. In practice, this can be lower if the GPU doesn't
 * support large textures. However, this engine guarantees a minimum width of 2048.
 */
@Log4j2
@UnregisterComponent(AbstractCollisionComponent.class)
public class TileLayer extends Entity<Game, Scene<Game>> implements UnsafeMemory {
    private static Vector2f V2F = new Vector2f();
    private static int MAX_SIZE = 65535;

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

    /**
     * Creates a tile layer. This is currently protected as tile layers would typically be created by the scene.
     * @param tileImage image to use for the tileset
     * @param width width of the tileset, in tiles
     * @param height height of the tileset, in tiles
     * @param tileWidth width of each tile, in pixels
     * @param tileHeight height of each tile, in pixels
     */
    protected TileLayer(Image tileImage, int width, int height, float tileWidth, float tileHeight) {
        this.tileImage = tileImage;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        if(width > GL33.glGetInteger(GL33.GL_MAX_TEXTURE_SIZE) || height > GL33.glGetInteger(GL33.GL_MAX_TEXTURE_SIZE)) {
            throw new GameEngineException("This tileset is too large for the current GPU.");
        }
        if(width > MAX_SIZE || height > MAX_SIZE) {
            throw new GameEngineException("Tileset is too large! Max size: " + MAX_SIZE);
        }

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

    /**
     * Basically converts a 2d coordinate into a single integer, while preserving the value of 0 to indicate that no
     * tile exists at a position
     * @param tileX x value to hash
     * @param tileY y value to hash
     * @return the hashed value
     */
    private int getTileLookupHash(int tileX, int tileY) {
        return ((tileX + 1) << 16) + (tileY + 1);
    }

    /**
     * Renders the tile layer.
     * @param game the current game
     * @param scene the current scene
     * @param renderer reference to the renderer for primate rendering and more advanced drawing functionality
     */
    @Override
    public void render(Game game, Scene scene, Renderer renderer) {
        if(dirty) {
            tileLookupBuffer.clear();
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, tileLookupTexture.getTextureId());
            GL33.glTexSubImage2D(GL33.GL_TEXTURE_2D, 0, 0, 0, width, height, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, tileLookupBuffer);
            dirty = false;
        }

        if(renderer.getShaderTile() != null) {
            renderer.pushShader(renderer.getShaderTile());
        }

        renderer.getShader().setUniform(ShaderUniform.TEX_DIFFUSE, tileImage.getTexture());
        renderer.getShader().setUniform(ShaderUniform.TEX_TILE_LOOKUP, tileLookupTexture);

        renderer.getShader().setUniform(ShaderUniform.TILE_UV0, V2F.set(tileImage.getU0(), tileImage.getV0()));
        renderer.getShader().setUniform(ShaderUniform.TILE_UV_SIZE, V2F.set(tileWidth/tileImage.getTexture().getWidth(), tileHeight/tileImage.getTexture().getHeight()));
        renderer.getShader().setUniform(ShaderUniform.TILE_SIZE, V2F.set(tileWidth, tileHeight));

        //Drawing the quad flushes the texture batch and resets the bound texture count
        renderer.drawQuad(getX(), getY(), width * tileWidth, height * tileHeight);

        if(renderer.getShaderTile() != null) {
            renderer.popShader();
        }
    }

    /**
     * Destroys the tile layer, freeing any graphics memory this tile layer uses.
     * @param game the current game
     * @param scene the current scene
     */
    @Override
    public void onDestroy(Game game, Scene<Game> scene) {
        freeMemory();
        super.onDestroy(game, scene);
    }

    /**
     * Frees the memory used by this tile layer.
     */
    @Override
    public void freeMemory() {
        UnsafeMemoryTracker.untrack(this);
        tileLookupTexture.freeMemory();
        MemoryUtil.memFree(tileLookupBuffer);
    }
}
