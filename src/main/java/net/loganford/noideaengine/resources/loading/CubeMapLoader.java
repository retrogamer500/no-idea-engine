package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.config.json.CubeMapConfig;
import net.loganford.noideaengine.graphics.CubeMap;
import net.loganford.noideaengine.utils.file.DataSource;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class CubeMapLoader extends ResourceLoader {

    private List<CubeMapConfig> cubeMapsToLoad;

    public CubeMapLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        game.getCubeMapManager().unloadGroups(ctx);
        cubeMapsToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getCubeMaps() != null) {
            cubeMapsToLoad.addAll(game.getConfig().getResources().getCubeMaps()
                    .stream().filter(r -> ctx.getLoadingGroups().contains(r.getGroup())).collect(Collectors.toList()));
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        CubeMapConfig config = cubeMapsToLoad.remove(0);
        CubeMap cubeMap = load(config);
        populateResource(cubeMap, config);
        log.info("Cube map loaded: " + config.getKey() + "!");
        game.getCubeMapManager().put(config.getKey(), cubeMap);
    }

    @Override
    public int getRemaining() {
        return cubeMapsToLoad.size();
    }

    public CubeMap load(CubeMapConfig config) {
        String[] filenames = {
                config.getFrontFilename(),
                config.getBackFilename(),
                config.getUpFilename(),
                config.getDownFilename(),
                config.getRightFilename(),
                config.getLeftFilename(),
        };

        int textureId = GL33.glGenTextures();
        GL33.glBindTexture(GL33.GL_TEXTURE_CUBE_MAP, textureId);

        for(int i = 0; i < filenames.length; i++) {
            String filename = filenames[i];
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);
                IntBuffer components = stack.mallocInt(1);

                boolean flipImage = false;
                //noinspection ConstantConditions
                STBImage.stbi_set_flip_vertically_on_load(flipImage);
                DataSource location = config.getResourceMapper().get(filename);
                ByteBuffer textureBuffer = STBImage.stbi_load_from_memory(location.loadBytes(), widthBuffer, heightBuffer, components, 4);
                if(textureBuffer == null) {
                    throw new GameEngineException("Could not load texture!");
                }
                textureBuffer.flip();

                int width = widthBuffer.get();
                int height = heightBuffer.get();

                GL33.glTexImage2D(GL33.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL33.GL_RGBA8, width, height, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, textureBuffer);
            }

            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
            GL33.glTexParameteri(GL33.GL_TEXTURE_CUBE_MAP, GL33.GL_TEXTURE_WRAP_R, GL33.GL_CLAMP_TO_EDGE);

            //Probably don't need this
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
        }

        return new CubeMap(textureId);
    }
}
