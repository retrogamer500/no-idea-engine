package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.state.RoomEditorScene;
import org.junit.Test;

public class LevelEditorTest {
    @Test
    public void testLevelEditor() {
        Game game = new Game(new RoomEditorScene("upstairs_hallway_1"));
        game.run();
    }
}
