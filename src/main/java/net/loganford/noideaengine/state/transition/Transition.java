package net.loganford.noideaengine.state.transition;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.GameState;

public abstract class Transition<G extends Game> extends GameState<G> {

    @Getter private GameState<G> previousState;
    @Getter private GameState<G> nextState;

    /**
     * Called prior to beginState() to set the previous and next state, and begin the next state.
     * @param game
     * @param previousState
     * @param nextState
     */
    public final void beginTransition(G game, GameState<G> previousState, GameState<G> nextState) {
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

    @Override
    public void endState(G game) {
        super.endState(game);
        getPreviousState().endState(game);
    }
}
