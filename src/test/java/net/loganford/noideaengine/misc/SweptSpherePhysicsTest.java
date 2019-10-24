package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Model;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.UnitSphere;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.MovementBehavior;
import net.loganford.noideaengine.state.entity.components.FirstPersonCameraComponent;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import net.loganford.noideaengine.state.entity.systems.FirstPersonCameraSystem;
import net.loganford.noideaengine.state.entity.systems.RegisterSystem;
import net.loganford.noideaengine.state.entity.systems.UnregisterSystem;
import net.loganford.noideaengine.state.entity.systems.collision.SpacialPartitionCollisionSystem;
import org.joml.Vector3f;
import org.junit.Test;


public class SweptSpherePhysicsTest {

    @UnregisterSystem(SpacialPartitionCollisionSystem.class)
    @RegisterSystem(value = SpacialPartitionCollisionSystem.class, arguments = {"4", "1024"})
    @RegisterSystem(FirstPersonCameraSystem.class)
    public class TestScene extends Scene {

        @Override
        public void beginState(Game game) {
            super.beginState(game);
            add(new Level());
            add(new Player(), 0, 10, 10);

            for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 5; j++) {
                    add(new UnitSphereEntity(), 4*i - 10, 10, 4*j - 10);
                }
            }

            //add(new UnitSphereEntity(), -5.7f, 6, -6);


            getCamera().setPosition(0, 10, 10);
            getCamera().lookAt(-3, 0, 0);
        }
    }

    @RegisterComponent(FirstPersonCameraComponent.class)
    public class Player extends Entity {

    }

    public class Level extends Entity {
        private Model model;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);

            model = game.getModelManager().get("level");
            setShape(model.getShape());
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);

            renderer.pushShader(renderer.getShaderForwardOpaque());
            model.render(renderer, getPosMatrix());
            renderer.popShader();
        }
    }

    public class UnitSphereEntity extends Entity {
        private Model model;
        private Vector3f velocity = new Vector3f(0, -2f, 0);

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            setShape(new UnitSphere());

            model = game.getModelManager().get("unitSphere");
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);

            renderer.pushShader(renderer.getShaderForwardOpaque());
            model.render(renderer, getPosMatrix());
            renderer.popShader();
        }

        @Override
        public void step(Game game, Scene scene, float delta) {
            super.step(game, scene, delta);

            move(velocity, delta, MovementBehavior.BOUNCE, Level.class);
        }
    }

    @Test
    public void testSweptSpherePhysics() {
        Game game = new Game(new TestScene());
        game.run();
    }
}
