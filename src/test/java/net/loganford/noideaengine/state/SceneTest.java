package net.loganford.noideaengine.state;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.state.collisionSystem.SpacialPartitionBroadphase;
import net.loganford.noideaengine.state.entity.Entity2D;
import net.loganford.noideaengine.state.transition.FadeTransition;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.junit.Test;

public class SceneTest {

    @Test
    public void testScene() {
        TestScene testScene = new TestScene();
        testScene.setCollisionSystem2D(new SpacialPartitionBroadphase(32, 4096));

        Game game = new Game(testScene);
        game.setTransition(new FadeTransition(1000));
        game.run();
    }

    public class TestScene extends Scene<Game> {
        private PlayerEntity playerEntity;

        @Override
        public void beginState(Game game) {
            super.beginState(game);
            for(int i = 0; i < 25; i++) {
                TestEntity e = new TestEntity();
                e.setPos(MathUtils.randRangeF(16, 640 - 16), MathUtils.randRangeF(16, 480 - 16));
                add(e);
            }
            playerEntity = new PlayerEntity();
            add(playerEntity);
        }
    }

    public class TestEntity extends Entity2D<Game, TestScene> {
        @Override
        public void onCreate(Game game, TestScene scene) {
            super.onCreate(game, scene);
            setSprite(game.getSpriteManager().get("test_sprite"));
            createMaskFromSprite();
            getSprite().setAnimationSpeed(0f);
        }

        @Override
        public void step(Game game, TestScene scene, float delta) {
            super.step(game, scene, delta);
            getSprite().setCurrentFrame(1);
            if(collidesWith(TestEntity.class)) {
                getSprite().setCurrentFrame(1);
            }
            else {
                getSprite().setCurrentFrame(0);
            }

            if(collidesWith(PlayerEntity.class)) {
                destroy();
            }
        }
    }

    public class PlayerEntity extends Entity2D<Game, TestScene> {
        @Override
        public void onCreate(Game game, TestScene scene) {
            super.onCreate(game, scene);
            setSprite(game.getSpriteManager().get("test_sprite"));
            setDepth(-100);
            setPos(128, 64);
            createMaskFromSprite();
        }

        @Override
        public void step(Game game, TestScene scene, float delta) {
            super.step(game, scene, delta);
            if(game.getInput().keyDown(Input.KEY_W)) {
                setY(getY() - (120 * delta/1000f));
            }
            if(game.getInput().keyDown(Input.KEY_A)) {
                setX(getX() - (120 * delta/1000f));
            }
            if(game.getInput().keyDown(Input.KEY_S)) {
                setY(getY() + (120 * delta/1000f));
            }
            if(game.getInput().keyDown(Input.KEY_D)) {
                setX(getX() + (120 * delta/1000f));
            }

            if(game.getInput().keyDown(Input.KEY_SPACE)) {
                TestEntity entity = new TestEntity();
                entity.setPos(getX(), getY() + 32);
                scene.add(entity);
            }
        }
    }
}