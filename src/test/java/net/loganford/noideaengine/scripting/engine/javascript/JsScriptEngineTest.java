package net.loganford.noideaengine.scripting.engine.javascript;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import org.junit.Test;


public class JsScriptEngineTest {
    @Test
    public void testScripts() {
        Scene scene = new Scene<Game>() {

            @Override
            public void beginState(Game game) {
                super.beginState(game);

                for(int i = 0; i < 100; i++) {
                    add(game.getEntityManager().get("test").newInstance());
                }
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);
            }

            @Override
            public void step(Game game, float delta) {
                super.step(game, delta);
            }
        };

        Game game = new Game(scene);
        game.run();
    }

    @Scriptable
    public static class ScriptedEntity extends Entity {
        @Scriptable
        public ScriptedEntity() {

        }

        @Scriptable
        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
        }

        @Scriptable
        @Override
        public void step(Game game, Scene scene, float delta) {
            super.step(game, scene, delta);
        }
    }
}