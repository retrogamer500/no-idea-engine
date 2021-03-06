package net.loganford.noideaengine.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.components.CharacterControllerComponent;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.components.camera.AbstractCameraComponent;
import net.loganford.noideaengine.components.physics.CharacterPhysicsComponent;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(CharacterPhysicsComponent.class)
@RegisterComponent(CharacterControllerComponent.class)
@RegisterComponent(AbstractCameraComponent.class)
public class CharacterControllerSystem extends ProcessEntitySystem {
    private static Vector3f V3F = new Vector3f();

    private int physicsComponentIndex;
    private int characterControllerComponentIndex;
    private int abstractCameraComponentIndex;

    public CharacterControllerSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

        physicsComponentIndex = getComponentLocation(CharacterPhysicsComponent.class);
        characterControllerComponentIndex = getComponentLocation(CharacterControllerComponent.class);
        abstractCameraComponentIndex = getComponentLocation(AbstractCameraComponent.class);

        setPriority(11);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        CharacterPhysicsComponent physicsComponent = (CharacterPhysicsComponent) components.get(physicsComponentIndex);
        CharacterControllerComponent characterControllerComponent = (CharacterControllerComponent) components.get(characterControllerComponentIndex);
        AbstractCameraComponent abstractCameraComponent = (AbstractCameraComponent) components.get(abstractCameraComponentIndex);

        float acceleration;
        if(physicsComponent.isOnGround()) {
            acceleration = characterControllerComponent.getAcceleration() + physicsComponent.getFriction();
        }
        else {
            acceleration = characterControllerComponent.getAcceleration();
        }
        if(game.getInput().keyDown(characterControllerComponent.getUpKey())) {
            physicsComponent.getVelocity().add(V3F.set(1, 0, 0).rotateY(-abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(characterControllerComponent.getLeftKey())) {
            physicsComponent.getVelocity().add(V3F.set(1, 0, 0).rotateY((float)Math.PI/2f - abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(characterControllerComponent.getDownKey())) {
            physicsComponent.getVelocity().add(V3F.set(1, 0, 0).rotateY((float)Math.PI - abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(characterControllerComponent.getRightKey())) {
            physicsComponent.getVelocity().sub(V3F.set(1, 0, 0).rotateY((float)Math.PI/2f - abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(characterControllerComponent.getJumpKey())) {
            if(physicsComponent.isOnGround()) {
                physicsComponent.setOnGround(false);
                physicsComponent.getVelocity().add(V3F.set(physicsComponent.getGravity()).mul(-characterControllerComponent.getJumpSpeed()));
            }
        }
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }
}
