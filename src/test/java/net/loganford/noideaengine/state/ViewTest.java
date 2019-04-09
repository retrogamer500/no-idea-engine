package net.loganford.noideaengine.state;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Sprite;
import org.junit.Test;

public class ViewTest {
    @Test
    public void testCamera2D() {
        GameState gameState = new GameState() {
            Sprite sprite;

            @Override
            public void beginState(Game game) {
                sprite = game.getSpriteManager().get("test_sprite");
                setStretch(true);
            }

            @Override
            public void render(Game game, Renderer renderer) {
                sprite.render(renderer, 300, 300);
            }

            @Override
            public void step(Game game, float delta) {
                sprite.step(delta);
                getView().setX(getView().getX() + (30 * (delta/1000f)));
            }
        };

        Game game = new Game(gameState);
        game.run();
    }
}