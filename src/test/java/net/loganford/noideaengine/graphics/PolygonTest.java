package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;

public class PolygonTest {
    @Test
    public void testPolygon() {
        Game game = new Game(new Scene() {
            @Override
            public void beginState(Game game) {
                super.beginState(game);

                getView().setPos(-32, -64);
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);

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
            public void step(Game game, long delta) {
                super.step(game, delta);

                System.out.println(getView().getMouseX() + " " + getView().getMouseY());
            }
        });
        game.run();
    }
}