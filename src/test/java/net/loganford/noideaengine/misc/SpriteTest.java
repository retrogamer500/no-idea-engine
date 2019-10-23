package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Sprite;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector4f;
import org.junit.Test;

public class SpriteTest {
    @Test
    public void testSprite() {
        Scene scene = new Scene() {
            Sprite sprite;
            Sprite sprite2;
            Sprite sprite3;

            @Override
            public void beginState(Game game) {
                super.beginState(game);
                setBackgroundColor(.9f, .9f, .9f, .1f);

                sprite = game.getSpriteManager().get("test_sprite");
                sprite.setScale(8);
                sprite.setColor(new Vector4f(1f, 0f, 0f, 1f));
                sprite2 = game.getSpriteManager().get("test_sprite");

                sprite3 = new Sprite(game.getImageManager().get("sprite_sheet"));
                sprite3.setOffsetX(32);
                sprite3.setOffsetY(8);
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);
                sprite.render(renderer, 128, 64);
                sprite2.render(renderer, 300, 300);
                sprite3.render(renderer, 256, 256);
            }

            @Override
            public void step(Game game, float delta) {
                sprite.step(delta);
                sprite.setAngle(sprite.getAngle() + MathUtils.PI * delta/1000f);
                sprite2.step(delta);

                sprite3.step(delta);
                sprite3.setAngle(sprite3.getAngle() + MathUtils.PI * delta/1000f);

            }
        };

        Game game = new Game(scene);
        game.run();
    }
}