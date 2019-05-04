package net.loganford.noideaengine.state;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;

public abstract class UILayer<G extends Game, S extends GameState> {
    @Getter private boolean destroyed = false;

    public abstract void beginUILayer(G game, S gameState);

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

    public abstract void render(G game, S gameState, Renderer renderer);

    public abstract void step(G game, S scene, float delta);
}
