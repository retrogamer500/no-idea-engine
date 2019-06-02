package net.loganford.noideaengine.state;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.junit.Test;

public class TileLayerTest {
    @Test
    public void testTileLayer() {
        Game game = new Game(new Scene() {
            TileLayer tileLayer;

            @Override
            public void beginState(Game game) {
                super.beginState(game);

                tileLayer = addTileLayer(game.getImageManager().get("tiles"), 1024, 1024, 16, 16, 0f);
                for(int i = 0; i < 1024; i ++) {
                    for(int j = 0; j < 1024; j++) {
                        tileLayer.setTile(i, j, MathUtils.randRangeI(0, 16), MathUtils.randRangeI(0, 16));
                    }
                }

                getView().setX(-20f);
                getView().setY(-20f);
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);

                tileLayer.render(game, this, renderer);
            }
        });
        game.run();
    }
}