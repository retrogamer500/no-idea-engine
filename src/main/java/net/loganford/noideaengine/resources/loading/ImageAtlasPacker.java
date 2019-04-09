package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBRPContext;
import org.lwjgl.stb.STBRPNode;
import org.lwjgl.stb.STBRPRect;
import org.lwjgl.stb.STBRectPack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j2
public class ImageAtlasPacker extends ResourceLoader {

    public static final int MIN_REQUIRED_TEXTURE_SIZE = 2048;
    public static final int MAX_TEXTURE_SIZE = 8192;
    public static final int IMAGES_PER_TEXTURE = 65536;

    private static int atlasNumber = 0;
    private int imagesLeftToInsert = 0;
    private List<Image> imagesToInsertInAtlases;

    public ImageAtlasPacker(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        if(game.getConfig().getResources().getImages() != null) {
            //Todo: when implementing loading groups, check all in group where texture id is null
            imagesLeftToInsert = game.getConfig().getResources().getImages().size();
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        if(imagesToInsertInAtlases == null) {
            imagesToInsertInAtlases = new ArrayList<>(game.getImageManager().getValues());
        }

        Texture atlasTexture = loadIntoAtlas(imagesToInsertInAtlases);

        String key = "_atlas_" + ImageAtlasPacker.atlasNumber;
        ImageAtlasPacker.atlasNumber++;
        log.info("Image atlas loaded. Name: " + key + " Width: " + atlasTexture.getWidth() + ". Height: " + atlasTexture.getHeight() + ".");
        game.getTextureManager().put(key, atlasTexture);

        imagesLeftToInsert = imagesToInsertInAtlases.size();
    }

    @Override
    public int getRemaining() {
        if(imagesToInsertInAtlases != null) {
            return imagesToInsertInAtlases.size();
        }
        return imagesLeftToInsert;
    }

    /**
     * Loads a list of images into an atlas, and initializes them for rendering
     * @param images
     */
    public Texture loadIntoAtlas(List<Image> images) {
        //Calculate size of texture atlas
        int textureSize = Math.min(MAX_TEXTURE_SIZE, GL33.glGetInteger(GL33.GL_MAX_TEXTURE_SIZE));
        if(textureSize < MIN_REQUIRED_TEXTURE_SIZE) {
            throw new GameEngineException("Graphics card does not support large enough texture size. Size: " + textureSize);
        }

        //Calculate packed rectangles
        STBRPContext context = STBRPContext.malloc();
        STBRPNode.Buffer nodeBuffer = STBRPNode.malloc(2 * textureSize);
        int imageNum = Math.min(IMAGES_PER_TEXTURE, images.size());
        STBRPRect.Buffer rectBuffer = STBRPRect.malloc(imageNum);

        for(int i = 0; i < imageNum; i++) {
            rectBuffer.get(i).id(i);
            rectBuffer.get(i).w((short)images.get(i).getWidth());
            rectBuffer.get(i).h((short)images.get(i).getHeight());
            rectBuffer.get(i).x((short)0);
            rectBuffer.get(i).y((short)0);
            rectBuffer.get(i).was_packed(false);
        }

        STBRectPack.stbrp_init_target(context , textureSize, textureSize, nodeBuffer);
        STBRectPack.stbrp_pack_rects(context, rectBuffer);

        //Create texture buffer
        ByteBuffer atlasBuffer = BufferUtils.createByteBuffer(textureSize * textureSize * 4);

        for(int i = 0; i < imageNum; i++) {
            STBRPRect packedRect = rectBuffer.get(i);

            if(packedRect.was_packed()) {
                //Copy image into texture atlas
                Image image = images.get(packedRect.id());
                int x1 = packedRect.x();
                int y1 = packedRect.y();
                int x2 = x1 + packedRect.w();
                int y2 = y1 + packedRect.h();

                for(int y = y1; y < y2; y++) {
                    ByteBuffer imageBuffer = image.getData();

                    //Copy full row from image into atlas
                    imageBuffer.clear().position(4 * (int)image.getWidth() * y).mark().limit(4 * (int)image.getWidth() * (y+1));
                    atlasBuffer.clear().position(4 * (y * textureSize + x1)).mark().limit(4 * (y * textureSize + x2));
                    atlasBuffer.put(imageBuffer);
                }
            }
        }

        //Create the atlas texture
        atlasBuffer.clear();
        Texture atlasTexture = new Texture(textureSize, textureSize, atlasBuffer);

        //Update the images to point to the atlas
        for(int i = 0; i < imageNum; i++) {
            STBRPRect packedRect = rectBuffer.get(i);
            if(packedRect.was_packed()) {
                Image image = images.get(packedRect.id());
                image.init(atlasTexture,
                        packedRect.w(),
                        packedRect.h(),
                        packedRect.x()/(float)atlasTexture.getWidth(),
                        packedRect.y()/(float)atlasTexture.getHeight(),
                        (packedRect.x() + packedRect.w())/(float)atlasTexture.getWidth(),
                        (packedRect.y() + packedRect.h())/(float)atlasTexture.getHeight());
            }
        }

        //Remove updated images from array
        Iterator<Image> iter = images.iterator();
        while(iter.hasNext()) {
            Image image = iter.next();
            if(image.getTexture() != null) {
                iter.remove();
            }
        }

        //Free memory
        context.free();
        nodeBuffer.free();
        rectBuffer.free();

        return atlasTexture;
    }
}
