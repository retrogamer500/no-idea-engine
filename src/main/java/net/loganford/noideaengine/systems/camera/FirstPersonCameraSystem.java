package net.loganford.noideaengine.systems.camera;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.components.AbstractPositionComponent;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.components.camera.FirstPersonCameraComponent;
import net.loganford.noideaengine.systems.ProcessEntitySystem;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(AbstractPositionComponent.class)
@RegisterComponent(FirstPersonCameraComponent.class)
public class FirstPersonCameraSystem extends ProcessEntitySystem {

    private int abstractPositionComponentIndex;
    private int firstPersonCameraComponentIndex;

    private static Vector3f V3F = new Vector3f();

    public FirstPersonCameraSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

        abstractPositionComponentIndex = getComponentLocation(AbstractPositionComponent.class);
        firstPersonCameraComponentIndex = getComponentLocation(FirstPersonCameraComponent.class);

        game.getWindow().setMouseCaptured(true);
        setPriority(10);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        AbstractPositionComponent abstractPositionComponent =
                (AbstractPositionComponent) components.get(abstractPositionComponentIndex);
        FirstPersonCameraComponent firstPersonCameraComponent =
                (FirstPersonCameraComponent) components.get(firstPersonCameraComponentIndex);

        //Adjust camera according to mouse movement
        firstPersonCameraComponent.setYaw(firstPersonCameraComponent.getYaw() +
                (game.getInput().getMouseDeltaX() * firstPersonCameraComponent.getSensitivity()));

        firstPersonCameraComponent.setPitch(firstPersonCameraComponent.getPitch() +
                (game.getInput().getMouseDeltaY() * firstPersonCameraComponent.getSensitivity()));

        firstPersonCameraComponent.setPitch(Math.min(Math.max(firstPersonCameraComponent.getPitch(), firstPersonCameraComponent.getMinPitch()), firstPersonCameraComponent.getMaxPitch()));


        //Set scene's camera
        scene.getCamera().setPosition(abstractPositionComponent.getPos());
        V3F.set(firstPersonCameraComponent.getDirection());
        V3F.add(scene.getCamera().getPosition());
        scene.getCamera().lookAt(V3F);
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }
}
