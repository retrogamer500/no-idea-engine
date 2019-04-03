package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a single frame in a sprite animation
 */
public class Frame {
    public static final float DEFAULT_DURATION = 17;

    @Getter private Image image;
    @Getter private float duration;

    @Getter @Setter private float offsetX = 0;
    @Getter @Setter private float offsetY = 0;

    public Frame(Image image, float duration) {
        this.image = image;
        this.duration = duration;
    }

    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
