package net.loganford.noideaengine.state;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import org.joml.Matrix4f;

@Log4j2
public abstract class ScreenTransformation {
    @Getter @Setter protected Matrix4f projectionMatrix = new Matrix4f();
    @Getter @Setter protected Matrix4f viewMatrix = new Matrix4f();
    @Getter @Setter protected Game game;
    @Getter @Setter protected GameState gameState;

    public ScreenTransformation(Game game, GameState gameState) {
        this.game = game;
        this.gameState = gameState;
    }

    protected abstract void calculateViewProjection();

    public void onResize(int width, int height) {
        log.info("View/Camera being resized: " + width + " " + height);
    }
}
