package net.loganford.noideaengine.state.entity.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.*;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(PhysicsComponent.class)
@RegisterComponent(AbstractPositionComponent.class)
@RegisterComponent(AbstractCollisionComponent.class)
public class PhysicsSystem extends ProcessEntitySystem {
    private int physicsComponentIndex;
    private int abstractPositionComponentIndex;
    private int abstractCollisionIndex;

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();
    private static Vector3f V3F_4 = new Vector3f();
    private static Vector3f V3F_5 = new Vector3f();

    public PhysicsSystem(Game game, Scene scene, String[] args) {
        super(game, scene, args);

        physicsComponentIndex = getComponentLocation(PhysicsComponent.class);
        abstractPositionComponentIndex = getComponentLocation(AbstractPositionComponent.class);
        abstractCollisionIndex = getComponentLocation(AbstractCollisionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        PhysicsComponent physicsComponent =
                (PhysicsComponent) components.get(physicsComponentIndex);
        AbstractPositionComponent abstractPositionComponent =
                (AbstractPositionComponent) components.get(abstractPositionComponentIndex);
        AbstractCollisionComponent abstractCollisionComponent =
                (AbstractCollisionComponent) components.get(abstractCollisionIndex);

        //Limit max speed
        if(physicsComponent.getVelocity().length() > physicsComponent.getMaxSpeed()) {
            physicsComponent.getVelocity().normalize().mul(physicsComponent.getMaxSpeed());
        }

        handleMovement(entity, physicsComponent, abstractPositionComponent, abstractCollisionComponent, delta);

        //Friction
        if(physicsComponent.getGravity().lengthSquared() == 0) {
            float speed = physicsComponent.getVelocity().length();
            if(speed > 0) {
                speed = Math.max(0, speed - physicsComponent.getFriction());
                physicsComponent.getVelocity().normalize().mul(speed);
            }
        }
    }

    private void handleMovement(Entity entity, PhysicsComponent physicsComponent, AbstractPositionComponent abstractPositionComponent,
                                AbstractCollisionComponent abstractCollisionComponent, float delta) {
        if(physicsComponent.getVelocity().lengthSquared() == 0) {
            return;
        }

        float speedPerSecond = physicsComponent.getVelocity().length(); //Speed of this object per second
        float remainingSpeed = speedPerSecond * delta / 1000f; //Number of units left to move this frame
        Vector3f nextDirection = V3F.set(physicsComponent.getVelocity()).normalize(); //Unit vector of direction

        SweepResult result;
        for(int i = 0; i < 8; i++) {
            result = entity.sweep(V3F_2.set(nextDirection).mul(remainingSpeed), physicsComponent.getSolidEntity());
            entity.move(result);


            if(result.collides()) {
                result.remainder(V3F_3);
                remainingSpeed = V3F_3.length();

                //Calculate bounce vector and determine whether to slide or bounce
                Vector3f bouncedVector = V3F_4.set(nextDirection);
                result.reflect(bouncedVector);
                Vector3f projected = V3F_5.set(bouncedVector).mul(speedPerSecond);
                float scalarProjection =  projected.dot(result.getNormal());

                if(scalarProjection < physicsComponent.getBounceVelocity()) {
                    //Slide
                    result.slide(nextDirection);

                    if(nextDirection.lengthSquared() == 0) {
                        remainingSpeed = 0;
                    }
                    else {
                        speedPerSecond *= nextDirection.length();
                        remainingSpeed *= nextDirection.length();
                        nextDirection.normalize();
                    }
                }
                else {
                    //Bounce
                    speedPerSecond *= physicsComponent.getBounceVelocityMultiplier();
                    speedPerSecond -= physicsComponent.getBounceVelocityDampener();
                    speedPerSecond = Math.max(0, speedPerSecond);
                    nextDirection.set(bouncedVector);
                }
            }
            else {
                remainingSpeed = 0;
            }

            if(remainingSpeed == 0) {
                break;
            }
        }

        //Update original velocity
        if(nextDirection.lengthSquared() == 0) {
            physicsComponent.getVelocity().set(0, 0, 0);
        }
        else {
            physicsComponent.getVelocity().set(nextDirection).normalize().mul(speedPerSecond);
        }
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }


}
