package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.components.physics.PhysicsComponent;
import net.loganford.noideaengine.systems.physics.PhysicsSystem;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import net.loganford.noideaengine.utils.annotations.RegisterSystem;
import org.joml.Vector3f;
import org.junit.Test;

public class SweptRectanglePhysicsTest {


    public class TestWall extends Entity {
        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            setShape(new Rect(0, 0, 32, 32));
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);
            renderer.setColor(1, 0, 0, 1);
            renderer.drawRectangle(getX(), getY(), 32, 32);
        }
    }

    @RegisterComponent(PhysicsComponent.class)
    public class TestPlayer extends Entity {
        private Vector3f acceleration = new Vector3f();
        private float accelerationAmount = 64f; //Acceleration per second, must be higher than friction

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            setShape(new Rect(0, 0, 16, 16));
            getComponent(PhysicsComponent.class).setSolidEntity(TestWall.class);
            getComponent(PhysicsComponent.class).setDrag(32f);
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);
            renderer.setColor(0, 0, 1, 1);
            renderer.drawRectangle(getX(), getY(), 16, 16);
        }

        @Override
        public void step(Game game, Scene scene, float delta) {
            super.step(game, scene, delta);

            //Handle keyboard input
            acceleration.set(0, 0, 0);
            if(game.getInput().keyDown(Input.KEY_W)) {
                acceleration.y -= accelerationAmount;
            }
            if(game.getInput().keyDown(Input.KEY_S)) {
                acceleration.y += accelerationAmount;
            }
            if(game.getInput().keyDown(Input.KEY_A)) {
                acceleration.x -= accelerationAmount;
            }
            if(game.getInput().keyDown(Input.KEY_D)) {
                acceleration.x += accelerationAmount;
            }

            //Apply acceleration
            if(acceleration.lengthSquared() > 0) {
                acceleration.normalize().mul(accelerationAmount);
                getComponent(PhysicsComponent.class).getVelocity().add(acceleration);
            }
        }
    }

    @RegisterSystem(PhysicsSystem.class)
    public class TestScene extends Scene {

        @Override
        public void beginState(Game game) {
            super.beginState(game);
            add(new TestWall(), 32, 32);
            add(new TestPlayer(),  128, 128);
        }

        @Override
        public void step(Game game, float delta) {
            super.step(game, delta);

            if(game.getInput().mousePressed(Input.MOUSE_1)) {
                add(new TestWall(), getView().getMouseX(), getView().getMouseY());
            }

            //Entity e = nearest(Entity.class);
        }
    }

    @Test
    public void testSpacialPartitionCollisionSystem() {
        Game game = new Game(new TestScene());
        game.run();
    }

}