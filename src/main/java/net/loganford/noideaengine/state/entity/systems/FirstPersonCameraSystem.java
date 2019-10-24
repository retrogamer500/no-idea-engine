package net.loganford.noideaengine.state.entity.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.Component;
import net.loganford.noideaengine.state.entity.components.FirstPersonCameraComponent;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(FirstPersonCameraComponent.class)
public class FirstPersonCameraSystem extends ProcessEntitySystem {
    private int firstPersonCameraComponentIndex;
    private static Vector3f V3F = new Vector3f();

    public FirstPersonCameraSystem(Game game, Scene scene, String[] args) {
        super(game, scene, args);
        firstPersonCameraComponentIndex = getComponentLocation(FirstPersonCameraComponent.class);
        game.getWindow().setMouseCaptured(true);
        setPriority(10);
    }

    @Override
    void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        FirstPersonCameraComponent firstPersonCameraComponent =
                (FirstPersonCameraComponent) components.get(firstPersonCameraComponentIndex);

        //Adjust camera according to mouse movement
        firstPersonCameraComponent.setYaw(firstPersonCameraComponent.getYaw() +
                (game.getInput().getMouseDeltaX() * firstPersonCameraComponent.getSensitivity()));

        firstPersonCameraComponent.setPitch(firstPersonCameraComponent.getPitch() +
                (game.getInput().getMouseDeltaY() * firstPersonCameraComponent.getSensitivity()));

        firstPersonCameraComponent.setPitch(Math.min(Math.max(firstPersonCameraComponent.getPitch(), (float)-Math.PI/2f * .99f), (float)Math.PI/2f * .99f));


        //Set scene's camera
        scene.getCamera().setPosition(entity.getPos());
        V3F.set(firstPersonCameraComponent.getDirection());
        V3F.add(scene.getCamera().getPosition());
        scene.getCamera().lookAt(V3F);
    }

    @Override
    void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }
}
