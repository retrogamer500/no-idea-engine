package net.loganford.noideaengine.misc;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.audio.Audio;
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
            sound = game.getAudioManager().get("test");
        }

        @Override
        public void step(Game game, float delta) {
            super.step(game, delta);

            if(game.getInput().keyPressed(Input.KEY_SPACE)) {
                sound.play(.1f);
            }

            if(game.getInput().keyPressed(Input.KEY_ENTER)) {
                game.getAudioSystem().stopAll();
            }
        }
    };

    Game game = new Game(scene);
    game.run();
    }
}