package net.loganford.noideaengine.systems.camera;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.components.AbstractPositionComponent;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.components.camera.ThirdPersonCameraComponent;
import net.loganford.noideaengine.systems.ProcessEntitySystem;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(AbstractPositionComponent.class)
@RegisterComponent(ThirdPersonCameraComponent.class)
public class ThirdPersonCameraSystem extends ProcessEntitySystem {

    private static Vector3f V3F = new Vector3f();

    private int abstractPositionComponentIndex;
    private int thirdPersonCameraComponentIndex;

    public ThirdPersonCameraSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

        abstractPositionComponentIndex = getComponentLocation(AbstractPositionComponent.class);
        thirdPersonCameraComponentIndex = getComponentLocation(ThirdPersonCameraComponent.class);

        game.getWindow().setMouseCaptured(true);
        setPriority(10);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        AbstractPositionComponent abstractPositionComponent =
                (AbstractPositionComponent) components.get(abstractPositionComponentIndex);
        ThirdPersonCameraComponent thirdPersonCameraComponent =
                (ThirdPersonCameraComponent) components.get(thirdPersonCameraComponentIndex);

        //Adjust camera according to mouse movement
        thirdPersonCameraComponent.setYaw(thirdPersonCameraComponent.getYaw() +
                (game.getInput().getMouseDeltaX() * thirdPersonCameraComponent.getSensitivity()));

        thirdPersonCameraComponent.setPitch(thirdPersonCameraComponent.getPitch() +
                (game.getInput().getMouseDeltaY() * thirdPersonCameraComponent.getSensitivity()));

        thirdPersonCameraComponent.setPitch(Math.min(Math.max(thirdPersonCameraComponent.getPitch(), thirdPersonCameraComponent.getMinPitch()), thirdPersonCameraComponent.getMaxPitch()));


        //Set scene's camera
        V3F.set(thirdPersonCameraComponent.getDirection()).mul(-thirdPersonCameraComponent.getDistance());

        if(thirdPersonCameraComponent.isLimitY()) {
            V3F.y = Math.max(thirdPersonCameraComponent.getLimitYAmount(), V3F.y);
        }

        V3F.add(abstractPositionComponent.getPos());
        scene.getCamera().setPosition(V3F);

        scene.getCamera().lookAt(abstractPositionComponent.getPos());
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }
}
