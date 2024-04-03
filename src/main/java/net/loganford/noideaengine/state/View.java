package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * A view represents a 2D projection and it is used for rendering 2D graphics.
 */
public class View extends AbstractViewProjection {
    private static Vector4f V4 = new Vector4f();
    private static Matrix4f M4 = new Matrix4f();

    /**The x position of the view in the world.*/
    @Getter @Setter private float x;
    /**The y position of the view in the world.*/
    @Getter @Setter private float y;
    /**The width of the view in the world. Should only be modified if the state which this camera belongs to has stretch=true.*/
    @Getter @Setter private int width;
    /**The height of the view in the world. Should only be modified if the state which this camera belongs to has stretch=true.*/
    @Getter @Setter private int height;
    /**The zoom of the view. Higher values are more zoomed-in.*/
    @Getter @Setter private float zoom = 1f;
    /**The angle of the camera, in radians.*/
    @Getter @Setter private float angle = 0f;
    /**Whether to maintain aspect ratio when game state is set to stretch**/
    @Getter @Setter private boolean maintainAspectRatio = true;

    /**
     * Creates a view
     * @param game the current game
     * @param gameState the current gameState
     * @param width width of the view in world space
     * @param height height of the view in world space
     */
    public View(Game game, GameState gameState, int width, int height) {
        super(game, gameState);

        this.width = width;
        this.height = height;
    }

    /**
     * Called prior to rendering anything in the state
     * @param gameState the current gameState
     */
    @Override
    protected void beforeRender(GameState gameState) {
        float difference = 0;
        if(!gameState.isStretch()) {
            difference = game.getWindow().getHeight() - height * gameState.getScale();
        }

        projectionMatrix.identity().ortho(0, width, height, 0, -100f, 100f);
        viewMatrix.identity().translate(width/2f, height/2f, 0f).scale(zoom, zoom, 1f).rotateZ(-angle).translate(-width/2f, -height/2f, 0f).translate(-x, -y - difference, 0);
    }

    /**
     * Called when both the window is resized, and the game state is configured to not stretch the view
     * @param width new width of the window
     * @param height new height of the window
     */
    @Override
    public void onResize(int width, int height) {
        super.onResize(width, height);

        this.width = width;
        this.height = height;
    }

    /**
     * Moves the view so that a certain point is at the center of the screen.
     * @param x x position in world space
     * @param y y position in world space
     */
    public void lookAt(float x, float y) {
        this.x = x - (float)width/(2*zoom);
        this.y = y - (float)height/(2*zoom);
    }

    /**
     * Moves the view so that this point is at the top-left of the window. May not play nice with changing the view's angle.
     * @param x x position in world space
     * @param y y position in world space
     */
    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x position of the mouse in the world.
     * @return the x position of the mouse in the world
     */
    public float getMouseX() {
        V4.set(getGame().getInput().getMouseX(), getGame().getInput().getMouseY(), 0f, 1f);
        V4.add(-width/2f, -height/2f, 0f, 0f)
                .rotateZ(angle).mul(1/zoom)
                .add(width/2f, height/2f, 0f, 0f)
                .mul(1f/getGameState().getScale())
                .add(x, y, 0, 0);
        return V4.x;
    }

    /**
     * Gets the y position of the mouse in the world.
     * @return the y position of the mouse in the world
     */
    public float getMouseY() {
        V4.set(getGame().getInput().getMouseX(), getGame().getInput().getMouseY(), 0f, 1f);
        V4.add(-width/2f, -height/2f, 0f, 0f)
                .rotateZ(angle).mul(1/zoom)
                .add(width/2f, height/2f, 0f, 0f)
                .mul(1f/getGameState().getScale())
                .add(x, y, 0, 0);
        return V4.y;
    }
}
