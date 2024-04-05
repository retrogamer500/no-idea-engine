package net.loganford.noideaengine.state;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Renderer;

public class DefaultUILayer extends UILayer {
    private Scene scene;

    public DefaultUILayer(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void beginUILayer(Game game, GameState gameState) {

    }

    @Override
    public void render(Game game, GameState gameState, Renderer renderer) {
        for(Entity entity : scene.getEntities()) {
            entity.renderUI(game, scene, this, renderer);
        }
    }

    @Override
    public void step(Game game, GameState scene, float delta) {

    }
}
