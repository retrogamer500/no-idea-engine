package net.loganford.noideaengine.state.transition;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.GameState;

/***
 * A transition that instantly ends
 */
public class InstantTransition extends Transition {

    @Override
    public void beginState(Game game) {
        super.beginState(game);
        endTransition(game);
    }
}
