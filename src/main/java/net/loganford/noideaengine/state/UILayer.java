package net.loganford.noideaengine.state;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;

public abstract class UILayer {
    @Getter private boolean destroyed = false;

    public abstract void beginUILayer(Game game, GameState gameState);

    public boolean renderBelow() {
        return true;
    }

    public boolean stepBelow() {
        return true;
    }

    public boolean inputBelow() {
        return true;
    }

    public void destroy() {
        destroyed = true;
    }

    public abstract void render(Game game, GameState gameState, Renderer renderer);

    public abstract void step(Game game, GameState scene, float delta);
}
