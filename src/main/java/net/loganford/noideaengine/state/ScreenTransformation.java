package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import org.joml.Matrix4fStack;

/**
 * A ScreenTransformation represents a transformation used in order to transform points from the world to points
 * on the screen. This is the superclass for both the View and the Camera class.
 */
@Log4j2
public abstract class ScreenTransformation {
    @Getter @Setter protected Matrix4fStack projectionMatrix = new Matrix4fStack(4);
    @Getter @Setter protected Matrix4fStack viewMatrix = new Matrix4fStack(4);
    @Getter @Setter protected Game game;
    @Getter @Setter protected GameState gameState;

    /**
     * Creates a ScreenTransformation for a certain game and state
     * @param game the game
     * @param gameState the state
     */
    public ScreenTransformation(Game game, GameState gameState) {
        this.game = game;
        this.gameState = gameState;
    }

    /**
     * This method is called once per frame. It is executed prior to stepping anything in the GameState.
     */
    protected void step() {}

    /**
     * This method is called every frame prior to rendering the scene.
     * @param gameState the current gameState
     */
    protected void beforeRender(GameState gameState) {}

    /**
     * This method is called by the engine when the window is resized.
     * @param width new width of the window
     * @param height new height of the window
     */
    public void onResize(int width, int height) {
        log.info("View/Camera being resized: " + width + " " + height);
    }
}
