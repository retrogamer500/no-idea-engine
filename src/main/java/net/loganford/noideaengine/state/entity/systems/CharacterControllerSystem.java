package net.loganford.noideaengine.state.entity.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.*;
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

        float acceleration = characterControllerComponent.getAcceleration() + physicsComponent.getFriction();
        if(game.getInput().keyDown(Input.KEY_W)) {
            physicsComponent.getVelocity().add(V3F.set(1, 0, 0).rotateY(-abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_A)) {
            physicsComponent.getVelocity().add(V3F.set(1, 0, 0).rotateY((float)Math.PI/2f - abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_S)) {
            physicsComponent.getVelocity().add(V3F.set(1, 0, 0).rotateY((float)Math.PI - abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_D)) {
            physicsComponent.getVelocity().sub(V3F.set(1, 0, 0).rotateY((float)Math.PI/2f - abstractCameraComponent.getYaw()).mul(acceleration * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_SPACE)) {
            /*if(physicsComponent.isOnGround()) {
                physicsComponent.getVelocity().add(V3F.set(physicsComponent.getGravity()).mul(-characterControllerComponent.getJumpSpeed()));
            }*/
        }
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }
}
