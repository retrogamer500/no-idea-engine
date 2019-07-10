package net.loganford.noideaengine.resources.loading;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.collisionSystem.CollisionSystem2D;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LoadingGroupTest {
    private Scene scene1;
    private Scene scene2;

    @Test
    public void testLoadingGroup() {
        scene1 = new Scene() {
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

            @Override
            public List<Integer> getRequiredResourceGroups() {
                List<Integer> requiredResources = new ArrayList<>();
                requiredResources.add(0);
                requiredResources.add(1);
                return requiredResources;
            }
        };

        scene2 = new Scene() {
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

            @Override
            public List<Integer> getRequiredResourceGroups() {
                List<Integer> requiredResources = new ArrayList<>();
                requiredResources.add(0);
                return requiredResources;
            }
        };

        Game game = new Game(scene1);
        game.run();
    }
}
