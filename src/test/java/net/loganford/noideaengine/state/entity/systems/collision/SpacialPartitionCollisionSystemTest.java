package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import org.junit.Test;

public class SpacialPartitionCollisionSystemTest {
    public class TestEntity extends Entity {
        private float size;

        public TestEntity(float size) {
            this.size = size;
        }

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            setShape(new Rect(0, 0, size, size));
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);
            renderer.setColor(1, 0, 0, 1);
            renderer.drawRectangle(getX(), getY(), size, size);
        }
    }

    public class TestScene extends Scene {
        private TestEntity t1;
        private TestEntity t2;

        @Override
        public void beginState(Game game) {
            super.beginState(game);
            t1 = new TestEntity(8);
            t1.setPos(48, 64 + .5f);
            add(t1);
            t2 = new TestEntity(32);
            t2.setPos(32, 32);
            add(t2);
        }

        @Override
        public void step(Game game, float delta) {
            SweepResult result = t1.sweep(.1f, -10.4f, TestEntity.class);
            System.out.println("test");
            super.step(game, delta);
        }
    }

    @Test
    public void testSpacialPartitionCollisionSystem() {
        Game game = new Game(new TestScene());
        game.run();
    }

}