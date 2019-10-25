package net.loganford.noideaengine.state.entity.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.Input;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.*;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(AbstractPositionComponent.class)
@RegisterComponent(FreeMovementComponent.class)
@RegisterComponent(AbstractCameraComponent.class)
public class FreeMovementSystem extends ProcessEntitySystem {
    private static Vector3f V3F = new Vector3f();

    private int abstractPositionComponentIndex;
    private int freeMovementComponentIndex;
    private int abstractCameraComponentIndex;

    public FreeMovementSystem(Game game, Scene scene, String[] args) {
        super(game, scene, args);

        abstractPositionComponentIndex = getComponentLocation(AbstractPositionComponent.class);
        freeMovementComponentIndex = getComponentLocation(FreeMovementComponent.class);
        abstractCameraComponentIndex = getComponentLocation(AbstractCameraComponent.class);

        setPriority(11);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        AbstractPositionComponent abstractPositionComponent = (AbstractPositionComponent) components.get(abstractPositionComponentIndex);
        FreeMovementComponent freeMovementComponent = (FreeMovementComponent) components.get(freeMovementComponentIndex);
        AbstractCameraComponent abstractCameraComponent = (AbstractCameraComponent) components.get(abstractCameraComponentIndex);

        if(game.getInput().keyDown(Input.KEY_W)) {
            freeMovementComponent.getVelocity().add(V3F.set(abstractCameraComponent.getDirection()).mul(freeMovementComponent.getAcceleration() * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_A)) {
            freeMovementComponent.getVelocity().add(V3F.set(1, 0, 0).rotateY((float)Math.PI/2f - abstractCameraComponent.getYaw()).mul(freeMovementComponent.getAcceleration() * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_S)) {
            freeMovementComponent.getVelocity().sub(V3F.set(abstractCameraComponent.getDirection()).mul(freeMovementComponent.getAcceleration() * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_D)) {
            freeMovementComponent.getVelocity().sub(V3F.set(1, 0, 0).rotateY((float)Math.PI/2f - abstractCameraComponent.getYaw()).mul(freeMovementComponent.getAcceleration() * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_SPACE)) {
            freeMovementComponent.getVelocity().add(V3F.set(0, 1, 0).mul(freeMovementComponent.getAcceleration() * delta/1000f));
        }
        if(game.getInput().keyDown(Input.KEY_LEFT_CONTROL)) {
            freeMovementComponent.getVelocity().sub(V3F.set(0, 1, 0).mul(freeMovementComponent.getAcceleration() * delta/1000f));
        }

        //Max speed
        float speed = freeMovementComponent.getVelocity().length();
        if(speed > freeMovementComponent.getMaxSpeed()) {
            freeMovementComponent.getVelocity().normalize().mul(freeMovementComponent.getMaxSpeed());
        }

        //Friction
        speed = freeMovementComponent.getVelocity().length();
        if(speed > 0) {
            speed = Math.max(0, speed - freeMovementComponent.getFriction() * delta/1000f);
            freeMovementComponent.getVelocity().normalize().mul(speed);
        }

        abstractPositionComponent.setPos(V3F.set(abstractPositionComponent.getPos()).add(freeMovementComponent.getVelocity()));
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }
}
