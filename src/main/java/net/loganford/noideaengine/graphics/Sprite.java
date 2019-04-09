package net.loganford.noideaengine.graphics;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.resources.PrototypeResource;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Sprite extends PrototypeResource {
    @Getter private List<Frame> frames;
    @Getter @Setter private float animationSpeed = 1f;

    @Getter @Setter private int currentFrame = 0;
    @Getter private double frameTime = 0;

    @Getter @Setter private Vector4f color = new Vector4f(1f, 1f, 1f, 1f);
    @Getter @Setter private float angle = 0;
    @Getter @Setter private float scaleX = 1;
    @Getter @Setter private float scaleY = 1;
    @Getter @Setter private float offsetX;
    @Getter @Setter private float offsetY;

    /**
     * Creates a Sprite with a single frame based on a texture
     * @param image
     */
    public Sprite(Image image) {
        Frame frame = new Frame(image, Frame.DEFAULT_DURATION);
        List<Frame> frames = new ArrayList<>();
        frames.add(frame);
        this.frames = frames;
    }

    public Sprite(List<Frame> frames) {
        this.frames = frames;
    }

    public void step(float delta) {
        frameTime += (animationSpeed * delta);

        while(frameTime >= frames.get(currentFrame).getDuration()) {
            frameTime -= frames.get(currentFrame).getDuration();
            currentFrame++;

            if(currentFrame >= frames.size()) {
                currentFrame = 0;
            }
        }
    }

    public void setScale(float scale) {
        scaleX = scale;
        scaleY = scale;
    }

    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void render(Renderer renderer, float x, float y) {
        //Renders the current sub-image
        Frame frame = frames.get(currentFrame);
        frame.getImage().setAngle(angle);
        frame.getImage().setScaleX(scaleX);
        frame.getImage().setScaleY(scaleY);
        frame.getImage().setOffsetX(offsetX);
        frame.getImage().setOffsetY(offsetY);
        frame.getImage().setColor(color);
        frame.getImage().render(renderer, x, y);
    }
}
