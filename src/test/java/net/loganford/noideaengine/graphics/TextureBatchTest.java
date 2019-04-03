package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;


public class TextureBatchTest {
    @Test
    public void testTextureBatch() {
        Game game = new Game(new Scene() {
            TextureBatch batch;
            Image image;

            @Override
            public void beginState(Game game) {
                super.beginState(game);
                image = game.getImageManager().get("green_ball");
                batch = new TextureBatch();
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);

                //image.render(renderer, 0, 0);
                for(int i = 0; i < 10; i++) {

                    float x = 64 * i;
                    float y = 32;
                    batch.put(renderer, image, x, y, 64, 64, image.getU0(), image.getV0(), image.getU1(), image.getV1());
                }

                batch.flush(renderer);
            }
        });
        game.run();
    }

}