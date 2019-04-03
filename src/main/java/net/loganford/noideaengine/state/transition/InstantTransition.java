package net.loganford.noideaengine.state.transition;

import net.loganford.noideaengine.Game;

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
