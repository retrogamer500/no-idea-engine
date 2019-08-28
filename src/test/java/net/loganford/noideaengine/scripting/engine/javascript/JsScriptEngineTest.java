package net.loganford.noideaengine.scripting.engine.javascript;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.scripting.Function;
import net.loganford.noideaengine.scripting.Script;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import org.junit.Test;

import java.lang.reflect.Constructor;


public class JsScriptEngineTest {
    @Test
    public void testScripts() {
        Scene scene = new Scene<Game>() {

            @Override
            public void beginState(Game game) {
                super.beginState(game);

                Script script = game.getScriptManager().get("test");
                script.execute(); //Need to execute script in order to obtain contents
                Function getClassFunction = script.getFunction("getClass");
                Class<?> clazz = getClassFunction.evalObject(Class.class);

                try {
                    Constructor constructor = clazz.getConstructor();
                    for(int i = 0; i < 1000; i++) {
                        Object instance = constructor.newInstance();
                        if (instance instanceof Entity) {
                            add((Entity) instance);
                        }
                    }
                }
                catch(Exception e) {
                    throw new GameEngineException(e);
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
}