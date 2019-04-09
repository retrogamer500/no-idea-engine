package net.loganford.noideaengine.resources.loading;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.config.json.SpriteConfig;
import net.loganford.noideaengine.graphics.Frame;
import net.loganford.noideaengine.graphics.Image;
import net.loganford.noideaengine.graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SpriteLoader extends ResourceLoader {

    private List<SpriteConfig> spritesToLoad;

    public SpriteLoader(Game game) {
        super(game);
    }

    @Override
    public void init(Game game, LoadingContext ctx) {
        spritesToLoad = new ArrayList<>();
        if(game.getConfig().getResources().getSprites() != null) {
            spritesToLoad.addAll(game.getConfig().getResources().getSprites());
        }
    }

    @Override
    public void loadOne(Game game, LoadingContext ctx) {
        SpriteConfig description = spritesToLoad.remove(0);
        Sprite sprite = load(game, description);
        game.getSpriteManager().put(description.getKey(), sprite);
        log.info("Loaded sprite: " + description.getKey());
    }

    @Override
    public int getRemaining() {
        return spritesToLoad.size();
    }

    public Sprite load(Game game, SpriteConfig description) {
        Image image = game.getImageManager().get(description.getImageKey());

        //Setup default values
        if(description.getFrameWidth() == null) {
            description.setFrameWidth(image.getWidth());
        }
        if(description.getFrameHeight() == null) {
            description.setFrameHeight(image.getHeight());
        }
        if(description.getPadding() == null) {
            description.setPadding(0);
        }
        if(description.getLength() == null) {
            description.setLength((int) Math.floor(image.getWidth() / description.getFrameWidth()));
        }
        if(description.getDuration() == null) {
            description.setDuration(Frame.DEFAULT_DURATION);
        }

        List<Frame> frames = new ArrayList<>();

        for(int i = 0; i < description.getLength(); i ++) {
            Image frameImage = image.getSubImage(description.getPadding() + (description.getFrameWidth() + description.getPadding())*i,
                    description.getPadding(),
                    description.getFrameWidth(),
                    description.getFrameHeight()
                    );
            Frame frame = new Frame(frameImage, description.getDuration());
            frames.add(frame);
        }

        Sprite sprite = new Sprite(frames);
        sprite.setOffsetX(description.getOffsetX());
        sprite.setOffsetY(description.getOffsetY());
        return sprite;
    }
}
