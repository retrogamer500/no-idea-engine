package net.loganford.noideaengine;

import net.loganford.noideaengine.state.Scene;
import org.junit.Test;

public class WindowTest {
    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void testWindow() {
        Game game = new Game(new Scene());
        game.getWindow().setSize(300, 300);
        game.getAlarms().add(2000, () -> game.getWindow().setSize(600, 600));
        game.getAlarms().add(4000, () -> {game.getWindow().setSize(1920, 1080); game.getWindow().setVsync(true); game.getWindow().setFullscreen(true);});
        game.getAlarms().add(10000, () -> game.endGame());
        game.run();
    }
}