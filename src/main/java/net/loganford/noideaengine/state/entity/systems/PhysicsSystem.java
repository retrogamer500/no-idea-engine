package net.loganford.noideaengine.state.entity.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.AbstractCollisionComponent;
import net.loganford.noideaengine.state.entity.components.AbstractPositionComponent;
import net.loganford.noideaengine.state.entity.components.Component;
import net.loganford.noideaengine.state.entity.components.PhysicsComponent;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(PhysicsComponent.class)
@RegisterComponent(AbstractPositionComponent.class)
@RegisterComponent(AbstractCollisionComponent.class)
public class PhysicsSystem extends ProcessEntitySystem {
    private int physicsComponentIndex;
    private int abstractPositionComponentIndex;
    private int abstractCollisionIndex;

    private static int MODIFIER_NONE = 0;
    private static int MODIFIER_GRAVITY = 1;
    private static int MODIFIER_MOVEMENT = 2;

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();
    private static Vector3f V3F_4 = new Vector3f();
    private static Vector3f V3F_5 = new Vector3f();
    private static Vector3f V3F_6 = new Vector3f();
    private static Vector3f V3F_7 = new Vector3f();
    private static Vector3f V3F_8 = new Vector3f();
    private static Vector3f V3F_9 = new Vector3f();
    private static Vector3f V3F_10 = new Vector3f();
    private static Vector3f V3F_11 = new Vector3f();

    public PhysicsSystem(Game game, Scene scene, Argument[] args) {
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

        //Add gravity
        physicsComponent.getVelocity().add(V3F_6.set(physicsComponent.getGravity()).mul(delta / 1000f));

        //Limit max speed
        if(physicsComponent.getVelocity().length() > physicsComponent.getMaxSpeed()) {
            physicsComponent.getVelocity().normalize().mul(physicsComponent.getMaxSpeed());
        }

        if(physicsComponent.isCharacterController()) {
            handleMovement(entity,
                    physicsComponent,
                    abstractPositionComponent,
                    abstractCollisionComponent,
                    physicsComponent.getCharacterVelocity(),
                    delta,
                    MODIFIER_MOVEMENT);

            handleMovement(entity,
                    physicsComponent,
                    abstractPositionComponent,
                    abstractCollisionComponent,
                    physicsComponent.getVelocity(),
                    delta,
                    MODIFIER_GRAVITY);
        }
        else {
            handleMovement(entity,
                    physicsComponent,
                    abstractPositionComponent,
                    abstractCollisionComponent,
                    physicsComponent.getVelocity(),
                    delta,
                    MODIFIER_NONE);
        }

        //Drag
        float speed = physicsComponent.getVelocity().length();
        if(speed > 0 && physicsComponent.getDrag() != 0) {
            speed = Math.max(0, speed - physicsComponent.getDrag());
            physicsComponent.getVelocity().normalize().mul(speed);
        }
    }

    private void handleMovement(Entity entity, PhysicsComponent physicsComponent, AbstractPositionComponent abstractPositionComponent,
                                AbstractCollisionComponent abstractCollisionComponent, Vector3f velocity, float delta, int modifier) {
        if(velocity.lengthSquared() < MathUtils.EPSILON) {
            return;
        }

        float timeMultiplier = delta / 1000f;
        float speedPerSecond = velocity.length(); //Speed of this object per second
        float remainingSpeed = speedPerSecond * timeMultiplier; //Number of units left to move this frame
        Vector3f nextDirection = V3F.set(velocity).normalize(); //Unit vector of direction

        SweepResult result;
        for(int i = 0; i < 8; i++) {
            result = entity.sweep(V3F_2.set(nextDirection).mul(remainingSpeed), physicsComponent.getSolidEntity());
            entity.move(result);

            if(result.collides()) {

                //Handle special character movement
                if(modifier != 0) {
                    Vector3f projNormGravity = V3F_10.set(physicsComponent.getGravity()).mul(result.getNormal().dot(physicsComponent.getGravity()) / physicsComponent.getGravity().lengthSquared());
                    float floorAngle = V3F_10.set(physicsComponent.getGravity()).normalize().dot(result.getNormal());
                    System.out.println("test");

                    if (modifier == MODIFIER_GRAVITY) {
                        if (floorAngle < -(1 - physicsComponent.getFloorAngle())) {
                            result.getNormal().set(projNormGravity.normalize());
                        }
                    }
                    else if (modifier == MODIFIER_MOVEMENT) {
                        if (floorAngle > -(1 - physicsComponent.getFloorAngle())) {
                            result.getNormal().sub(projNormGravity).normalize();
                        }
                    }
                }

                result.remainder(V3F_3);
                remainingSpeed = V3F_3.length();

                //Process interactive entities
                if(result.getEntity() != null && ((Entity)result.getEntity()).getPhysicsComponent() != null &&
                physicsComponent.isInteractive() && ((Entity)result.getEntity()).getPhysicsComponent().isInteractive()) {
                    PhysicsComponent otherPhysicsComponent = ((Entity)result.getEntity()).getPhysicsComponent();
                    float ratio1 = physicsComponent.getMass() / (otherPhysicsComponent.getMass() + physicsComponent.getMass());
                    float ratio2 = otherPhysicsComponent.getMass() / (otherPhysicsComponent.getMass() + physicsComponent.getMass());
                    otherPhysicsComponent.getVelocity().add(V3F_9.set(result.getNormal()).mul(-1 * ratio2 * velocity.length()));
                    speedPerSecond *= ratio1;
                    remainingSpeed *= ratio1;
                }

                //Calculate bounce vector and determine whether to slide or bounce
                Vector3f bouncedVector = V3F_4.set(nextDirection);
                result.reflect(bouncedVector);
                Vector3f projected = V3F_5.set(bouncedVector).mul(speedPerSecond);
                float scalarProjection =  projected.dot(result.getNormal());

                if(scalarProjection * physicsComponent.getBounceVelocityMultiplier() - physicsComponent.getBounceVelocityDampener() <= 0) {
                    //Slide
                    result.slide(nextDirection);

                    if(nextDirection.lengthSquared() == 0) {
                        //Sliding object has squarely hit an edge, set remaining speed to 0. This will break the loop.
                        remainingSpeed = 0;
                    }
                    else {
                        //Calculate friction (if gravity exists)
                        float frictionAmount = 0;
                        if(physicsComponent.getGravity().lengthSquared() != 0) {
                            float gravityDotNormal = Math.max(0, -V3F_8.set(physicsComponent.getGravity()).normalize().dot(result.getNormal()));
                            if(gravityDotNormal >= physicsComponent.getRollFactor()) {
                                frictionAmount = timeMultiplier * gravityDotNormal * physicsComponent.getFrictionDampener();
                            }
                        }

                        speedPerSecond *= nextDirection.length();
                        speedPerSecond = Math.max(0, speedPerSecond - frictionAmount);

                        remainingSpeed *= nextDirection.length();
                        remainingSpeed = Math.max(0, remainingSpeed - .5f * frictionAmount * timeMultiplier); //Calculus, dudes

                        nextDirection.normalize();
                    }
                }
                else {
                    //Bounce
                    nextDirection.set(bouncedVector);
                    Vector3f dampenFactor = V3F_7.set(result.getNormal()).mul(-(scalarProjection * physicsComponent.getBounceVelocityMultiplier() + physicsComponent.getBounceVelocityDampener()));
                    nextDirection.mul(speedPerSecond).add(dampenFactor);
                    speedPerSecond = nextDirection.length();
                    nextDirection.normalize();


                }
            }
            else {
                remainingSpeed = 0;
            }

            if(remainingSpeed < MathUtils.EPSILON) {
                break;
            }
        }

        //Update original velocity
        if(nextDirection.lengthSquared() == 0) {
            velocity.set(0, 0, 0);
        }
        else {
            velocity.set(nextDirection).normalize().mul(speedPerSecond);
        }
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }


}
