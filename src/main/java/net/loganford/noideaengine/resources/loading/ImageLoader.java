package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.config.json.ImageConfig;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.utils.file.DataSource;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ImageLoader extends ResourceLoader {

    private List<ImageConfig> imagesToLoad;

    public ImageLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getImageManager().unloadGroups(ctx);
        imagesToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getImages() != null) {
            imagesToLoad.addAll(game.getConfig().getResources().getImages()
                    .stream().filter(r -> ctx.getLoadingGroups().contains(r.getGroup())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        ImageConfig config = imagesToLoad.remove(0);
        Image image = load(config);
        populateResource(image, config);
        log.info("Image loaded. Name: " + config.getKey() + " Width: " + image.getWidth() + ". Height: " + image.getHeight() + ".");
        game.getImageManager().put(config.getKey(), image);
    }

    @Override
    public int getRemaining() {
        return imagesToLoad.size();
    }

    public Image load(ImageConfig imageConfig) {
        DataSource location = imageConfig.getResourceMapper().get((imageConfig.getFilename()));

        ByteBuffer imageBuffer;
        Image image;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer components = stack.mallocInt(1);

            boolean flipImage = imageConfig.isFlipVertically();
            STBImage.stbi_set_flip_vertically_on_load(flipImage);

            imageBuffer = STBImage.stbi_load_from_memory(location.loadBytes(), widthBuffer, heightBuffer, components, 4);
            if(imageBuffer == null) {
                throw new GameEngineException("Could not load image!");
            }
            imageBuffer.flip();

            int width = widthBuffer.get();
            int height = heightBuffer.get();

            image = Image.createUninitializedImage(width, height, imageBuffer);
        }

        return image;
    }
}
