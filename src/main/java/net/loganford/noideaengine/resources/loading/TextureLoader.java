package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.config.json.TextureConfig;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.utils.file.DataSource;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class TextureLoader extends ResourceLoader {

    private List<TextureConfig> texturesToLoad;

    public TextureLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getTextureManager().unloadGroups(ctx);
        texturesToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getTextures() != null) {
            texturesToLoad.addAll(game.getConfig().getResources().getTextures()
                    .stream().filter(r -> ctx.getLoadingGroups().contains(r.getGroup())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        TextureConfig config = texturesToLoad.remove(0);
        Texture texture = load(config);
        populateResource(texture, config);
        log.info("Texture loaded. Name: " + config.getKey() + " Width: " + texture.getWidth() + ". Height: " + texture.getHeight() + ".");
        game.getTextureManager().put(config.getKey(), texture);
    }

    @Override
    public int getRemaining() {
        return texturesToLoad.size();
    }

    public Texture load(TextureConfig textureConfig) {
        DataSource location = textureConfig.getResourceMapper().get((textureConfig.getFilename()));

        ByteBuffer textureBuffer;
        Texture texture;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer components = stack.mallocInt(1);

            boolean flipImage = textureConfig.isFlipVertically();
            STBImage.stbi_set_flip_vertically_on_load(flipImage);

            textureBuffer = STBImage.stbi_load_from_memory(location.loadBytes(), widthBuffer, heightBuffer, components, 4);
            if(textureBuffer == null) {
                throw new GameEngineException("Could not load texture!");
            }
            textureBuffer.flip();

            int width = widthBuffer.get();
            int height = heightBuffer.get();

            texture = new Texture(width, height, textureBuffer);
        }

        return texture;
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
