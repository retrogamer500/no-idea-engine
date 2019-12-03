package net.loganford.noideaengine.state.loading;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;

public class BasicLoadingScreen<G extends Game> extends LoadingScreen {

    @Getter @Setter private int loadBarHeight = 16;
    @Getter @Setter private int loadBarPadding = 16;
    @Getter @Setter private boolean centerVertically = false;

    @Override
    public void beginState(Game game) {
        super.beginState(game);
        setBackgroundColor(0f, 0f, 0f, 1f);
    }

    @Override
    public void render(Game game, Renderer renderer) {
        super.render(game, renderer);

        float verticalOffset = getView().getHeight() - loadBarHeight - 2 * loadBarPadding;
        if(centerVertically) {
            verticalOffset = (getView().getHeight() + loadBarHeight)/2f;
        }

        renderer.setColor(1f, 1f, 1f, 1f);
        renderer.drawRectangleOutline(loadBarPadding, verticalOffset, getView().getWidth() - loadBarPadding * 2, loadBarHeight);
        renderer.drawRectangle(loadBarPadding, verticalOffset, getRatioLoaded() * (getView().getWidth() - loadBarPadding * 2), loadBarHeight);
    }
}
