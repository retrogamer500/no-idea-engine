package net.loganford.noideaengine.shape;

import junit.framework.TestCase;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.state.Scene;
import org.junit.Test;

public class BitmapShapeTest extends TestCase {
    @Test
    public void testBitmapShape() {
        Scene scene = new Scene() {

            Image textureImage;

            @Override
            public void beginState(Game game) {
                super.beginState(game);

                Image image = game.getImageManager().get("4x4");
                Texture t = image.getTexture();
                textureImage = new Image(t, t.getWidth(), t.getHeight(), 0, 0, 1, 1);
                setBackgroundColor(1f, 1f, 1f, 1f);

                Bitmap shape = new Bitmap(image);
                shape.setPosition(0, 0);

                Bitmap shape2 = new Bitmap(image);
                shape2.setPosition(3, 3);

                System.out.println(shape2.collidesWith(shape));
                System.out.println("Test");
            }

            @Override
            public void render(Game game, Renderer renderer) {
                super.render(game, renderer);

                textureImage.render(renderer, 0, 0);
            }
        };

        Game game = new Game(scene);
        game.run();
    }

}