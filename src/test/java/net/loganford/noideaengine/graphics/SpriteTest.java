package net.loganford.noideaengine.graphics;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.MathUtils;
import org.joml.Vector4f;
import org.junit.Test;

public class SpriteTest {
    @Test
    public void testSprite() {
        Scene scene = new Scene() {
            Sprite sprite;
            Sprite sprite2;

            @Override
            public void beginState(Game game) {
                super.beginState(game);
                setBackgroundColor(.9f, .9f, .9f, .1f);

                sprite = game.getSpriteManager().get("test_sprite");
                sprite.setScale(8);
                sprite.setColor(new Vector4f(1f, 0f, 0f, 1f));
                sprite2 = game.getSpriteManager().get("test_sprite");
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);
                sprite.render(renderer, 128, 64);
                sprite2.render(renderer, 300, 300);
            }

            @Override
            public void step(Game game, long delta) {
                sprite.step(delta);
                sprite.setAngle(sprite.getAngle() + MathUtils.PI * delta/1000f);
                sprite2.step(delta);
            }
        };

        Game game = new Game(scene);
        game.run();
    }
}