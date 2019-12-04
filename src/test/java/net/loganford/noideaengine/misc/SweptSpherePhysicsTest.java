package net.loganford.noideaengine.misc;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.components.CharacterControllerComponent;
import net.loganford.noideaengine.components.LightingComponent;
import net.loganford.noideaengine.components.camera.FirstPersonCameraComponent;
import net.loganford.noideaengine.components.physics.CharacterPhysicsComponent;
import net.loganford.noideaengine.components.physics.PhysicsComponent;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.CubeMap;
import net.loganford.noideaengine.graphics.Model;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.Ellipsoid;
import net.loganford.noideaengine.shape.UnitSphere;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.lighting.PointLight;
import net.loganford.noideaengine.systems.CharacterControllerSystem;
import net.loganford.noideaengine.systems.FreeMovementSystem;
import net.loganford.noideaengine.systems.camera.FirstPersonCameraSystem;
import net.loganford.noideaengine.systems.collision.SpacialPartitionCollisionSystem;
import net.loganford.noideaengine.systems.physics.CharacterPhysicsSystem;
import net.loganford.noideaengine.systems.physics.PhysicsSystem;
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
    @RegisterSystem(FirstPersonCameraSystem.class)
    @RegisterSystem(FreeMovementSystem.class)
    @RegisterSystem(PhysicsSystem.class)
    @RegisterSystem(CharacterPhysicsSystem.class)
    @RegisterSystem(CharacterControllerSystem.class)
    public class TestScene extends Scene {

        private CubeMap cubeMap;
        private PointLight light1 = new PointLight();
        private PointLight light2 = new PointLight();

        @Override
        public void beginState(Game game) {
            super.beginState(game);
            add(new Level());
            add(new Player(), 6.47f, 0f, 86.34f);
            cubeMap = game.getCubeMapManager().get("plains");

            light1.setRadius(256f);
            light1.setColor(new Vector3f(1f, 0f, 0f));
            light1.setPosition(new Vector3f(6.47f, 0f, 86.34f));

            light2.setRadius(32f);
            light2.setColor(new Vector3f(0f, 0f, 1f));
            light2.setPosition(new Vector3f(23.35507f, -8.139f, 126.54014f));
        }

        @Override
        public void step(Game game, float delta) {
            super.step(game, delta);
            getLightingSystem().processLight(light1);
            getLightingSystem().processLight(light2);
        }

        @Override
        public void render(Game game, Renderer renderer) {
            cubeMap.render(renderer);
            super.render(game, renderer);

        }
    }

    @RegisterComponent(FirstPersonCameraComponent.class)
    @RegisterComponent(value = CharacterPhysicsComponent.class, arguments = {
            @Argument(name = "solidEntity", classValue = SolidInterface.class),
            @Argument(name = "gravity", vectorValue = @Vector3fa(y = -45f)),
            @Argument(name = "friction", floatValue = 40f),
            @Argument(name = "maxVerticalSpeed", floatValue = 20f),
            @Argument(name = "maxHorizontalSpeed", floatValue = 20f),
            @Argument(name = "floorAngle", floatValue = 1.2f)
    })
    @RegisterComponent(value = CharacterControllerComponent.class, arguments = {
            @Argument(name = "acceleration", floatValue = 40f),
            @Argument(name = "jumpSpeed", floatValue = 50f)
    })
    @RegisterComponent(LightingComponent.class)
    public class Player extends Entity {
        private Model model;
        private PointLight light;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);
            model = game.getModelManager().get("unitSphere");
            setShape(new Ellipsoid(new Vector3f(0, 0, 0), new Vector3f(.8f, 2.5f, .8f)));

            light = new PointLight();
            light.setRadius(256f);
            light.setColor(new Vector3f(1f, 1f, 0f));
            light.setPosition(new Vector3f(6.47f, 0f, 86.34f));
            getComponent(LightingComponent.class).setLight(light);
        }

        @Override
        public void step(Game game, Scene scene, float delta) {
            super.step(game, scene, delta);
            light.getPosition().set(getPos());

            if(game.getInput().mousePressed(Input.MOUSE_1)) {
                setPos(6.47f, 0f, 86.34f);
                getComponent(CharacterPhysicsComponent.class).getVelocity().set(0, 0, 0);
            }
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);
        }
    }

    public interface SolidInterface {}

    public class Level extends Entity implements SolidInterface {
        private Model model;

        @Override
        public void onCreate(Game game, Scene scene) {
            super.onCreate(game, scene);

            model = game.getModelManager().get("cs_italy_fixed");
            setShape(model.getShape());
        }

        @Override
        public void render(Game game, Scene scene, Renderer renderer) {
            super.render(game, scene, renderer);
            model.render(renderer, getPosMatrix());
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
            model.render(renderer, getPosMatrix());
        }
    }

    @Test
    public void testSweptSpherePhysics() {
        Game game = new Game(new TestScene());
        game.run();
    }
}
