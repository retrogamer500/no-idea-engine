package net.loganford.noideaengine.state.transition;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.GameState;
import net.loganford.noideaengine.state.Scene;

public abstract class Transition extends GameState {

    @Getter private GameState previousState;
    @Getter private GameState nextState;

    /**
     * Called prior to beginState() to set the previous and next state, and begin the next state.
     * @param game
     * @param previousState
     * @param nextState
     */
    public final void beginTransition(Game game, GameState previousState, GameState nextState) {
        game.setState(null);
        this.previousState = previousState;
        this.nextState = nextState;
        this.nextState.beginState(game);
        this.nextState.postBeginState(game);
    }

    /**
     * Ends the transition. At the next game loop, the state will be set to the next state.
     * @param game
     */
    public final void endTransition(Game game) {
        game.setState(nextState);
    }
}
