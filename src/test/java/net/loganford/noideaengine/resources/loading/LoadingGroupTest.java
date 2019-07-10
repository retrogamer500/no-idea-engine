package net.loganford.noideaengine.resources.loading;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.resources.RequireGroup;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;

public class LoadingGroupTest {
    @RequireGroup(1)
    @RequireGroup(2)
    class Scene1 extends Scene<Game> {
        @Override
        public void beginState(Game game) {
            super.beginState(game);
            setBackgroundColor(.8f, .8f, .8f, 1f);
        }

        @Override
        public void step(Game game, float delta) {
            if(game.getInput().keyPressed(Input.KEY_SPACE)) {
                game.setState(scene2);
            }
        }

        @Override
        public void render(Game game, Renderer renderer) {
            float tx = 0;
            for(Texture texture : game.getTextureManager().getValues()) {
                texture.getImage().render(renderer, tx, tx);
                tx += 128;
            }

            super.render(game, renderer);
        }
    }

    class Scene2 extends Scene<Game> {
        @Override
        public void beginState(Game game) {
            super.beginState(game);
            setBackgroundColor(.5f, .9f, .5f, 1f);
        }

        @Override
        public void render(Game game, Renderer renderer) {
            super.render(game, renderer);
            float tx = 0;
            for(Texture texture : game.getTextureManager().getValues()) {
                texture.getImage().render(renderer, tx, tx);
                tx += 128;
            }
        }

        @Override
        public void step(Game game, float delta) {
            if(game.getInput().keyPressed(Input.KEY_SPACE)) {
                game.setState(scene1);
            }
        }
    }

    Scene1 scene1;
    Scene2 scene2;

    @Test
    public void testLoadingGroup() {
        scene1 = new Scene1();
        scene2 = new Scene2();

        Game game = new Game(scene1);
        game.run();
    }
}
