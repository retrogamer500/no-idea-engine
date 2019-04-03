package net.loganford.noideaengine.state;

import net.loganford.noideaengine.Game;
import org.junit.Test;

public class StateTest {
    @Test
    public void testState() {
        Game game = new Game(new Scene());
        game.run();
    }
}
