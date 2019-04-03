package net.loganford.noideaengine.resources.loading;

import net.loganford.noideaengine.Game;

public interface ResourceLoader {
    void init(Game game, LoadingContext ctx);
    void loadOne(Game game, LoadingContext ctx);
    int getRemaining();
}
