package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Model;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.UnitSphere;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.components.CharacterControllerComponent;
import net.loganford.noideaengine.components.CharacterPhysicsComponent;
import net.loganford.noideaengine.components.PhysicsComponent;
import net.loganford.noideaengine.components.ThirdPersonCameraComponent;
import net.loganford.noideaengine.systems.*;
import net.loganford.noideaengine.systems.collision.SpacialPartitionCollisionSystem;
import net.loganford.noideaengine.utils.annotations.*;
import org.joml.Vector3f;
import org.junit.Test;


public class SweptSpherePhysicsTest {

    private static Vector3f V3F = new Vector3f();

    @UnregisterSystem(SpacialPartitionCollisionSystem.class)
    @RegisterSystem(value = SpacialPartitionCollisionSystem.class, arguments = {
            @Argument(name = "cellSize", intValue = 4),
            @Argument(name = "bucketCount", intValue = 1024)
    })
    @RegisterSystem(ThirdPersonCameraSystem.class)
    @RegisterSystem(FreeMovementSystem.class)
    @RegisterSystem(PhysicsSystem.class)
    @RegisterSystem(CharacterPhysicsSystem.class)
    @RegisterSystem(CharacterControllerSystem.class)
    public class TestScene extends Scene {

        @Override
        public void beginState(Game game) {
            super.beginState(game);
            add(new Level());
            add(new Player(), 0, 10, 0);

            /*for(int i = 0; i < 5; i++) {
                for(int j = 0; j < 5; j++) {
                    add(new UnitSphereEntity(), 4*i - 10, 10, 4*j - 10);
                }
            }*/
        }
    }

    @RegisterComponent(ThirdPersonCameraComponent.class)
    @RegisterComponent(value = CharacterPhysicsComponent.class, arguments = {
            @Argument(name = "solidEntity", classValue = SolidInterface.class),
            @Argument(name = "gravity", vectorValue = @Vector3fa(y = -10f)),
            @Argument(name = "friction", floatValue = 30f)
    })
    @RegisterComponent(value = CharacterControllerComponent.class, arguments = {
            @Argument(name = "acceleration", floatValue = 30f),
            @Argument(name = "jumpSpeed", floatValue = 30f)
    })
    public class Player extends Entity {
        private Model model;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            model = game.getModelManager().get("unitSphere");
            setShape(new UnitSphere());
        }

        @Override
        public void step(Game game, Scene scene, float delta) {
            super.step(game, scene, delta);

            if(game.getInput().mousePressed(Input.MOUSE_1)) {
                /*UnitSphereEntity entity = new UnitSphereEntity();
                scene.add(entity, getPos());

                AbstractCameraComponent cameraComponent = getComponent(AbstractCameraComponent.class);
                entity.getComponent(PhysicsComponent.class).getVelocity().set(V3F.set(cameraComponent.getDirection()).mul(6f));*/
                setPos(0, 10, 0);
                getComponent(CharacterPhysicsComponent.class).getVelocity().set(0, 0, 0);
            }
            if(game.getInput().mousePressed(Input.MOUSE_2)) {
                //scene.with(UnitSphereEntity.class, Entity::destroy);
            }
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);
            renderer.pushShader(renderer.getShaderForwardOpaque());
            model.render(renderer, getPosMatrix());
            renderer.popShader();
        }
    }

    public interface SolidInterface {}

    public class Level extends Entity implements SolidInterface {
        private Model model;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);

            model = game.getModelManager().get("level2");
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

    @RegisterComponent(value = PhysicsComponent.class, arguments = {
            @Argument(name = "solidEntity", classValue = SolidInterface.class),
            @Argument(name = "gravity", vectorValue = @Vector3fa(y = -10f)),
            @Argument(name = "interactive", booleanValue = true)
    })
    public class UnitSphereEntity extends Entity implements SolidInterface {
        private Model model;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
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
        //game.setFps(30, 30);
        game.run();
    }
}
