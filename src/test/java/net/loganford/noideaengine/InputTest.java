package net.loganford.noideaengine;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;

@Log4j2
public class InputTest {
    @Test
    public void testInput() {
        Game game = new Game(new Scene() {

            @Override
            public void step(Game game, long delta) {
                super.step(game, delta);

                if(game.getInput().mousePressed(Input.MOUSE_1)) {
                    log.info("Mouse pressed!");
                }

                if(game.getInput().mouseDown(Input.MOUSE_1)) {
                    log.info("Mouse down!");
                }

                if(game.getInput().mouseReleased(Input.MOUSE_1)) {
                    log.info("Mouse released!");
                }
            }
        });
        game.run();
    }
}