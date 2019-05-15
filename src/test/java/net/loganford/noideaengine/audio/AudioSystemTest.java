package net.loganford.noideaengine.audio;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;

@Log4j2
public class AudioSystemTest {
    @Test
        public void testAudioSystem() {
        Scene scene = new Scene() {
            private Audio sound;

            @Override
            public void beginState(Game game) {
                super.beginState(game);
                sound = game.getAudioManager().get("test.ogg");
            }

            @Override
            public void step(Game game, float delta) {
                super.step(game, delta);

                if(game.getInput().keyPressed(Input.KEY_SPACE)) {
                    log.info("Playing sound!");
                    sound.play(.1f);
                }
            }
        };

        Game game = new Game(scene);
        game.run();
    }
}