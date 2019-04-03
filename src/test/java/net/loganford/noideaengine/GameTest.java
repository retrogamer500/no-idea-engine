package net.loganford.noideaengine;

import net.loganford.noideaengine.state.Scene;

public class GameTest {
    public static void main(String[] args) {
        Game game = new Game(new Scene());
        game.setFps(60, 60); //Sets the min and max FPS
        game.getWindow().setSize(1920, 1080);
        game.getWindow().setVsync(true);
        game.run();
    }
}