package net.loganford.noideaengine.resources.loading;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.config.json.LoadableConfig;
import net.loganford.noideaengine.config.json.Tag;
import net.loganford.noideaengine.resources.Resource;

public abstract class ResourceLoader {
    @Getter private Game game;

    public ResourceLoader(Game game) {
        this.game = game;
    }

    public abstract void init(Game game, LoadingContext ctx);
    public abstract void loadOne(Game game, LoadingContext ctx);
    public abstract int getRemaining();

    protected void populateResource(Resource resource, LoadableConfig config) {
        resource.setKey(config.getKey());
        resource.setLoadingGroup(config.getGroup());

        for(Tag tag : config.getTags()) {
            resource.getTags().put(tag.getKey(), tag.getValue());
        }
    }
}
