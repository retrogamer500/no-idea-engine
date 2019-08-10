package net.loganford.noideaengine.state.loading;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.resources.loading.LoadingContext;
import net.loganford.noideaengine.resources.loading.ResourceLoader;
import net.loganford.noideaengine.state.GameState;

import java.util.List;

public abstract class LoadingScreen<G extends Game> extends GameState<G> {
    @Getter private GameState nextState;
    @Getter @Setter private LoadingContext loadingContext;
    @Getter private List<ResourceLoader> resourceLoaders;
    @Getter private int totalItems = 0;
    @Getter private int itemsToLoad = 0;
    @Getter private float ratioLoaded = 0f;

    private boolean vsync;
    private int minFps;
    private int maxFps;

    public final void beginLoadingScreen(LoadingContext loadingContext, GameState nextState) {
        this.loadingContext = loadingContext;
        this.nextState = nextState;
    }

    @Override
    public void beginState(G game) {
        super.beginState(game);
        resourceLoaders = game.getResourceLoaders();

        for(ResourceLoader loader: resourceLoaders) {
            loader.init(game, loadingContext);
            totalItems += loader.getRemaining();
        }
        itemsToLoad = totalItems;

        minFps = game.getMinFps();
        maxFps = game.getMaxFps();
        game.setFps(1, 9999);
        vsync = game.getWindow().isVsync();
        game.getWindow().setVsync(false);
    }

    @Override
    public void step(G game, float delta) {
        super.step(game, delta);

        {
            //Get the first loader with a resource
            ResourceLoader loader = resourceLoaders.get(0);
            while (loader.getRemaining() == 0) {
                if (resourceLoaders.size() > 1) {
                    resourceLoaders.remove(0);
                    loader = resourceLoaders.get(0);
                } else {
                    doneLoading(game);
                    break;
                }
            }

            //Load a resource
            if (loader.getRemaining() > 0) {
                loader.loadOne(game, loadingContext);
            } else {
                if (resourceLoaders.size() > 1) {
                    resourceLoaders.remove(0);
                } else {
                    doneLoading(game);
                }
            }
        }

        //Refresh the percentage of items loaded
        itemsToLoad = 0;
        for(ResourceLoader loader: resourceLoaders) {
            itemsToLoad += loader.getRemaining();
        }

        ratioLoaded = 1f - (totalItems == 0 ? 0f : (itemsToLoad / (float)totalItems));
    }

    public final void doneLoading(Game game) {
        game.setState(nextState);
        game.getWindow().setVsync(vsync);
        game.setFps(minFps, maxFps);
    }
}
