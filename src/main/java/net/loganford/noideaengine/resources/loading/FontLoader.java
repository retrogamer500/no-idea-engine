package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.config.json.FontConfig;
import net.loganford.noideaengine.graphics.Font;
import net.loganford.noideaengine.graphics.Texture;
import net.loganford.noideaengine.utils.file.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FontLoader extends ResourceLoader {
    public static final int BITMAP_W = 512;
    public static final int BITMAP_H = 512;

    private List<FontConfig> fontsToLoad;

    public FontLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        fontsToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getFonts() != null) {
            fontsToLoad.addAll(game.getConfig().getResources().getFonts());
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        FontConfig description = fontsToLoad.remove(0);
        Font font = load(description);
        log.info("Loaded font: " + description.getKey());
        game.getFontManager().put(description.getKey(), font);
    }

    @Override
    public int getRemaining() {
        return fontsToLoad.size();
    }

    public Font load(FontConfig description) {
        Font font;
        int fontTexture = GL33.glGenTextures();
        STBTTPackedchar.Buffer charData = STBTTPackedchar.malloc(128);
        STBTTPackContext packContext;

        packContext = STBTTPackContext.malloc();
        ResourceLocation location = getGame().getResourceLocationFactory().get(description.getFilename());
        ByteBuffer ttf = location.loadBytes();

        //Get the scale and font info
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTruetype.stbtt_InitFont(fontInfo, ttf);
        float scale = STBTruetype.stbtt_ScaleForPixelHeight(fontInfo, description.getSize());
        IntBuffer accentBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer decentBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer lineGapBuffer = BufferUtils.createIntBuffer(1);
        STBTruetype.stbtt_GetFontVMetrics(fontInfo, accentBuffer, decentBuffer, lineGapBuffer);
        float accent = accentBuffer.get() * scale;
        float decent = decentBuffer.get() * scale;
        float lineGap = lineGapBuffer.get() * scale;

        //Generate font bitmap
        ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        STBTruetype.stbtt_PackBegin(packContext, bitmap, BITMAP_W, BITMAP_H, 0, 1, MemoryUtil.NULL);
        charData.limit(127);
        charData.position(32);
        STBTruetype.stbtt_PackSetOversampling(packContext, 2, 2);
        STBTruetype.stbtt_PackFontRange(packContext, ttf, 0, accent - decent, 32, charData);
        charData.clear();
        STBTruetype.stbtt_PackEnd(packContext);

        /*Pack the bitmap as RGBA since STB generates it as a single channel alpha value and we need 4 channels to
        * support our shader architecture.*/
        ByteBuffer bitmapRgba = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H * 4);
        for(int i = 0; i < BITMAP_W * BITMAP_H; i++) {
            bitmapRgba.put((byte)255);
            bitmapRgba.put((byte)255);
            bitmapRgba.put((byte)255);
            bitmapRgba.put(bitmap.get(i));
        }
        bitmapRgba.flip();

        //Pack buffer into texture and create font
        GL33.glBindTexture(GL33.GL_TEXTURE_2D, fontTexture);
        GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, bitmapRgba);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
        GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);

        Texture texture = new Texture(BITMAP_W, BITMAP_H, fontTexture);

        font = new Font(texture, charData, description.getSize(), accent, decent, lineGap);

        if(packContext != null) {
            packContext.free();
        }

        return font;
    }


}
