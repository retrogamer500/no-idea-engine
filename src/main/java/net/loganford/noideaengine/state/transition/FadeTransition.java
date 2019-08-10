package net.loganford.noideaengine.state.transition;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.FrameBufferObject;
import net.loganford.noideaengine.graphics.Renderer;

public class FadeTransition<G extends Game> extends Transition<G> {
    private long duration;
    private long timer;

    public FadeTransition(long duration) {
        this.duration = duration;
    }

    @Override
    public void beginState(G game) {
        super.beginState(game);
        getPreviousState().renderState(game, game.getRenderer());
        getNextState().renderState(game, game.getRenderer());
        timer = 0;
    }

    @Override
    public void step(G game, float delta) {
        super.step(game, delta);
        timer+= delta;


        if(timer > duration) {
            endTransition(game);
        }
    }

    @Override
    public void render(G game, Renderer renderer) {
        super.render(game, renderer);

        getPreviousState().getFrameBufferObject().renderToScreen(game, getPreviousState(), renderer);

        float alpha = timer == 0 ? 0 : Math.min(((float)timer) / duration, 1);
        FrameBufferObject nextStateFrameBuffer = getNextState().getFrameBufferObject();
        nextStateFrameBuffer.getColor().set(1f, 1f, 1f, alpha);
        nextStateFrameBuffer.renderToScreen(game, getNextState(), renderer);
    }
}
