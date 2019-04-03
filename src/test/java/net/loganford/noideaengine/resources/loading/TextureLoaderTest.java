package net.loganford.noideaengine.resources.loading;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.GameState;
import org.junit.Test;

public class TextureLoaderTest {
    @Test
    public void testTextureLoader() {
        GameState gameState = new GameState() {
            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);

                game.getTextureManager().get("_atlas_0").getImage().render(renderer, 8, 8);
            }
        };

        Game game = new Game(gameState);
        game.run();
    }
}