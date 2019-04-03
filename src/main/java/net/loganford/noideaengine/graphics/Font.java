package net.loganford.noideaengine.graphics;

import lombok.Getter;
import net.loganford.noideaengine.resources.PrototypeResource;
import net.loganford.noideaengine.resources.loading.FontLoader;
import net.loganford.noideaengine.utils.UnsafeMemoryTracker;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Font extends PrototypeResource implements UnsafeMemory {
    private static final FloatBuffer xb = MemoryUtil.memAllocFloat(1);
    private static final FloatBuffer yb = MemoryUtil.memAllocFloat(1);
    private static final STBTTAlignedQuad q = STBTTAlignedQuad.malloc();


    @Getter private Texture texture;
    @Getter private float size;
    @Getter private float accent;
    @Getter private float decent;
    @Getter private float lineGap;
    @Getter private Vector4f color = new Vector4f(0f, 0f, 0f, 1f);
    private STBTTPackedchar.Buffer charData;

    public Font(Texture texture, STBTTPackedchar.Buffer charData, float size, float accent, float decent, float lineGap) {
        this.texture = texture;
        this.charData = charData;
        this.size = size;
        this.accent = accent;
        this.decent = decent;
        this.lineGap = lineGap;
        UnsafeMemoryTracker.track(this);
    }

    public void print(Renderer renderer, float x, float y, String text) {
        xb.put(0, 0);
        yb.put(0, 0);

        charData.position(0);
        texture.getImage().setColor(color);

        GL33.glDisable(GL33.GL_DEPTH_TEST);

        for(int i = 0; i < text.length(); i++) {
            STBTruetype.stbtt_GetPackedQuad(charData, FontLoader.BITMAP_W, FontLoader.BITMAP_H, text.charAt(i), xb, yb, q, false);

            float width = q.x1() - q.x0();
            float height = q.y1() - q.y0();
            texture.getImage().render(renderer, x + q.x0(), y + q.y0() + accent, width, height, q.s0(), q.t0(), q.s1(), q.t1());
        }
    }

    protected void setColor(Vector4f color) {
        this.color = color;
    }

    @Override
    public void freeMemory() {
        charData.free();
        UnsafeMemoryTracker.untrack(this);
    }

    @Override
    public PrototypeResource clone() throws CloneNotSupportedException {
        Font font = (Font)super.clone();
        font.setColor(new Vector4f(color.x, color.y, color.z, color.w));
        return font;
    }
}
