package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.MovementBehavior;
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

    public class TestPlayer extends Entity {
        private Vector3f velocity = new Vector3f();
        private Vector3f acceleration = new Vector3f();

        private float friction = 32f; //Acceleration per second
        private float accelerationAmount = friction + 32f; //Acceleration per second, must be higher than friction
        private float maxSpeed = 500;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            setShape(new Rect(0, 0, 16, 16));
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
            if(acceleration.length() > 0) {
                acceleration.normalize().mul(accelerationAmount);
                velocity.add(acceleration);
            }

            //Limit max speed
            if(velocity.length() > maxSpeed) {
                velocity.normalize().mul(maxSpeed);
            }

            //Move entity, sliding smoothly off walls
            move(velocity, delta, MovementBehavior.SLIDE, TestWall.class);

            //Apply friction
            float speed = velocity.length();
            if(speed > 0) {
                speed = Math.max(0, speed - friction);
                velocity.normalize().mul(speed);
            }
        }
    }

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
        }
    }

    @Test
    public void testSpacialPartitionCollisionSystem() {
        Game game = new Game(new TestScene());
        game.run();
    }

}