package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.GameState;
import net.loganford.noideaengine.state.transition.FadeTransition;
import net.loganford.noideaengine.state.transition.InstantTransition;
import org.junit.Test;

public class TransitionTest {
    private GameState scene1;
    private GameState scene2;

    @Test
    public void testTransition() {
        scene1 = new GameState() {
            @Override
            public void beginState(Game game) {
                //setStretch(true);
                super.beginState(game);
                getView().setWidth(320);
                getView().setHeight(240);
                getView().setZoom(2f);
                setScale(2f);

                getAlarms().add(2000, () -> game.setState(scene2));
                setBackgroundColor(0f, 1f, 1f, 1f);
            }
        };
        scene2 = new GameState() {
            @Override
            public void beginState(Game game) {
                //setStretch(true);
                super.beginState(game);
                getView().setWidth(320);
                getView().setHeight(240);
                setScale(2f);

                getAlarms().add(2000, () -> game.setState(scene1));
                setBackgroundColor(1f, 0f, 1f, 1f);
            }

            @Override
            public void render(Game game, Renderer renderer) {
                renderer.setColor(.5f, .5f, 0f, 1f);
                renderer.drawRectangle(32, 32, 220, 150);
                super.render(game, renderer);
            }
        };
        Game game = new Game(scene1);
        game.setTransition(new FadeTransition(200));
        game.run();
    }
}