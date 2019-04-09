package net.loganford.noideaengine.resources.loading;

import lombok.Getter;
import net.loganford.noideaengine.Game;

public abstract class ResourceLoader {
    @Getter private Game game;

    public ResourceLoader(Game game) {
        this.game = game;
    }

    public abstract void init(Game game, LoadingContext ctx);
    public abstract void loadOne(Game game, LoadingContext ctx);
    public abstract int getRemaining();
}
