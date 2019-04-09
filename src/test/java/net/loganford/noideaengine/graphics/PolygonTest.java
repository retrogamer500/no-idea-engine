package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;
import org.lwjgl.opengl.GL33;

public class PolygonTest {
    @Test
    public void testPolygon() {
        Scene scene = new Scene() {
            @Override
            public void beginState(Game game) {
                super.beginState(game);

                getView().setPos(-32, -64);
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);

                renderer.clear(.3f, .3f, .3f);
                GL33.glClearColor(0f, 0f, 0f, 1f);


                renderer.drawCircleOutline(64f, 64f, 32f);
                renderer.drawRectangleOutline(128f, 64f, 64f, 64f);

                renderer.drawCircle(64f, 128f, 32f);
                renderer.drawRectangle(128f, 128f, 64f, 64f);

                renderer.drawLine(0, 0, 64f, 64f, 2f);
                renderer.drawLine(16, 16, 32f, 256f, 2f);
                renderer.drawLine(258f, 258f, 300f, 16f, 32f);
                renderer.drawLine(258f + 20f, 258f, 300f + 20f, 16f, 1f);
            }

            @Override
            public void step(Game game, float delta) {
                super.step(game, delta);
            }
        };

        scene.setScale(2f);
        scene.setStretch(false);

        Game game = new Game(scene);
        game.run();
    }
}