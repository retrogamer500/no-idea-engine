package net.loganford.noideaengine.state;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.transition.FadeTransition;
import org.junit.Test;

public class TransitionTest {
    private GameState scene1;
    private GameState scene2;

    @Test
    public void testTransition() {
        scene1 = new GameState() {
            @Override
            public void beginState(Game game) {
                super.beginState(game);
                getAlarms().add(2000, () -> game.setState(scene2));
                setBackgroundColor(0f, 1f, 1f, 1f);
            }
        };
        scene2 = new GameState() {
            @Override
            public void beginState(Game game) {
                super.beginState(game);
                getAlarms().add(2000, () -> game.setState(scene1));
                setBackgroundColor(1f, 0f, 1f, 1f);
            }
        };
        Game game = new Game(scene1);
        game.setTransition(new FadeTransition(200));
        //game.getAlarms().add(10000, () -> game.endGame());
        game.run();
    }
}