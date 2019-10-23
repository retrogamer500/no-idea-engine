package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Font;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;

public class FontTest {
    @Test
    public void testFont() {
        Scene scene = new Scene() {
            Font font;
            Font font2;

            @Override
            public void beginState(Game game) {
                super.beginState(game);
                setBackgroundColor(.9f, .9f, .9f, .1f);
                font = game.getFontManager().get("roboto");
                font2 = game.getFontManager().get("roboto");
                font2.setScale(2f);

                font2.getColor().set(1f, 0f, 0f, 1f);
            }

            @Override
            public void render(Game game, Renderer renderer) {
                font.print(renderer, 8, 8, "The quick brown fox jumped over the lazy dog");
                font2.print(renderer, 8, 40, "The quick brown fox jumped over the lazy dog");
            }
        };

        Game game = new Game(scene);
        game.run();
    }
}