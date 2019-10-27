package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Model;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.UnitSphere;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.FirstPersonCameraComponent;
import net.loganford.noideaengine.state.entity.components.FreeMovementComponent;
import net.loganford.noideaengine.state.entity.components.PhysicsComponent;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import net.loganford.noideaengine.state.entity.systems.*;
import net.loganford.noideaengine.state.entity.systems.collision.SpacialPartitionCollisionSystem;
import org.joml.Vector3f;
import org.junit.Test;


public class SweptSpherePhysicsTest {

    private static Vector3f V3F = new Vector3f();

    @UnregisterSystem(SpacialPartitionCollisionSystem.class)
    @RegisterSystem(value = SpacialPartitionCollisionSystem.class, arguments = {"4", "1024"})
    //@RegisterSystem(NaiveCollisionSystem.class)
    @RegisterSystem(FirstPersonCameraSystem.class)
    @RegisterSystem(FreeMovementSystem.class)
    @RegisterSystem(PhysicsSystem.class)
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
    @RegisterComponent(FreeMovementComponent.class)
    public class Player extends Entity {

        @Override
        public void step(Game game, Scene scene, float delta) {
            super.step(game, scene, delta);

            if(game.getInput().mousePressed(Input.MOUSE_1)) {
                UnitSphereEntity entity = new UnitSphereEntity();
                scene.add(entity, getPos());

                FirstPersonCameraComponent cameraComponent = (FirstPersonCameraComponent) getComponent(FirstPersonCameraComponent.class);
                ((PhysicsComponent)entity.getComponent(PhysicsComponent.class)).getVelocity().set(V3F.set(cameraComponent.getDirection()).mul(6f));
            }
            if(game.getInput().mousePressed(Input.MOUSE_2)) {
                scene.with(UnitSphereEntity.class, Entity::destroy);
            }
        }
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

    @RegisterComponent(PhysicsComponent.class)
    public class UnitSphereEntity extends Entity {
        private Model model;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            ((PhysicsComponent)getComponent(PhysicsComponent.class)).setSolidEntity(Level.class);
            //((PhysicsComponent)getComponent(PhysicsComponent.class)).getVelocity().set(0, -2f, 0);
            ((PhysicsComponent)getComponent(PhysicsComponent.class)).getGravity().set(0, -10f, 0);
            model = game.getModelManager().get("unitSphere");
            setShape(new UnitSphere());
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);
            renderer.pushShader(renderer.getShaderForwardOpaque());
            model.render(renderer, getPosMatrix());
            renderer.popShader();
        }
    }

    @Test
    public void testSweptSpherePhysics() {
        Game game = new Game(new TestScene());
        game.run();
    }
}
