package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Texture;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

@Log4j2
public class TextureLoader extends ResourceLoader {

    public TextureLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getTextureManager().unloadGroups(ctx);
        //Todo: implement
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        //Todo: implement
    }

    @Override
    public int getRemaining() {
        return 0;
    }

    public Texture load(int width, int height, TextureGenerator generator) {
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for(int i = 0; i < width; i++) {
            for(int j = 0; j < width; j++) {
                int pixel = generator.getPixel(i, j);
                buffer.put((byte) ((pixel >> 24) & 0xFF));
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) ((pixel) & 0xFF));
            }
        }

        buffer.flip();

        return new Texture(width, height, buffer);
    }
}
