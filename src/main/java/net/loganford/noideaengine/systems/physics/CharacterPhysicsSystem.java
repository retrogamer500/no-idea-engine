package net.loganford.noideaengine.systems.physics;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.components.AbstractPositionComponent;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.components.collision.AbstractCollisionComponent;
import net.loganford.noideaengine.components.physics.CharacterPhysicsComponent;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.systems.ProcessEntitySystem;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3f;

import java.util.List;

@RegisterComponent(CharacterPhysicsComponent.class)
@RegisterComponent(AbstractPositionComponent.class)
@RegisterComponent(AbstractCollisionComponent.class)
public class CharacterPhysicsSystem extends ProcessEntitySystem {
    private int characterPhysicsComponentIndex;
    private int abstractPositionComponentIndex;
    private int abstractCollisionIndex;

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
    private static Vector3f V3F_12 = new Vector3f();
    private static Vector3f V3F_13 = new Vector3f();

    public CharacterPhysicsSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

        characterPhysicsComponentIndex = getComponentLocation(CharacterPhysicsComponent.class);
        abstractPositionComponentIndex = getComponentLocation(AbstractPositionComponent.class);
        abstractCollisionIndex = getComponentLocation(AbstractCollisionComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        CharacterPhysicsComponent physicsComponent =
                (CharacterPhysicsComponent) components.get(characterPhysicsComponentIndex);
        AbstractPositionComponent abstractPositionComponent =
                (AbstractPositionComponent) components.get(abstractPositionComponentIndex);
        AbstractCollisionComponent abstractCollisionComponent =
                (AbstractCollisionComponent) components.get(abstractCollisionIndex);

        //Add gravity
        physicsComponent.getVelocity().add(V3F_4.set(physicsComponent.getGravity()).mul(delta / 1000f));

        //Separate velocity components
        Vector3f normalVelocity = V3F_7;
        Vector3f orthogonalVelocity = V3F_8;
        MathUtils.vectorComponents(physicsComponent.getVelocity(), physicsComponent.getGravity(), normalVelocity, orthogonalVelocity);

        //Limit max speed
        if(normalVelocity.length() > physicsComponent.getMaxVerticalSpeed()) {
            normalVelocity.normalize().mul(physicsComponent.getMaxVerticalSpeed());
        }

        //Limit max speed
        if(orthogonalVelocity.length() > physicsComponent.getMaxHorizontalSpeed()) {
            orthogonalVelocity.normalize().mul(physicsComponent.getMaxHorizontalSpeed());
        }

        //Check if we're on the ground
        physicsComponent.setOnGround(false);
        float distance = .01f;
        SweepResult result = entity.sweep(V3F_13.set(physicsComponent.getGravity()).normalize().mul(distance), physicsComponent.getSolidEntity());
        if(result.collides()) {
            float floorAngle = getFloorAngle(physicsComponent.getGravity(), result.getNormal());
            if(floorAngle <= physicsComponent.getFloorAngle()) {
                physicsComponent.setOnGround(true);
            }
        }

        //Handle orthogonal movement
        handleMovement(entity,
                physicsComponent,
                abstractPositionComponent,
                abstractCollisionComponent,
                orthogonalVelocity,
                delta,
                true);

        //Reproject deflected orthogonal velocity onto plane
        float orthogonalSpeed = orthogonalVelocity.length();
        if(orthogonalSpeed > MathUtils.EPSILON ) {
            MathUtils.vectorComponents(orthogonalVelocity.normalize(), physicsComponent.getGravity(), null, V3F_9);
            orthogonalVelocity.set(V3F_9);
            if(orthogonalVelocity.lengthSquared() != 0) {
                orthogonalVelocity.normalize().mul(orthogonalSpeed);
            }
        }

        //Handle normal movement
        handleMovement(entity,
                physicsComponent,
                abstractPositionComponent,
                abstractCollisionComponent,
                normalVelocity,
                delta,
                false);

        //Reproject deflected normal velocity onto gravity
        float normalSpeed = normalVelocity.length();
        if(normalSpeed > MathUtils.EPSILON) {
            MathUtils.vectorComponents(normalVelocity.normalize(), physicsComponent.getGravity(), V3F_10, null);
            normalVelocity.set(V3F_10);
            if(normalVelocity.lengthSquared() != 0) {
                normalVelocity.normalize().mul(normalSpeed);
            }
        }


        if(physicsComponent.isOnGround()) {
            //Keep player on ground if climbing down slopes (only when traveling downward)
            if(normalVelocity.dot(physicsComponent.getGravity()) > 0) {
                float downAmount = 1f * delta/1000f;
                SweepResult sweepResult = entity.sweep(V3F_10.set(physicsComponent.getGravity()).mul(downAmount), physicsComponent.getSolidEntity());
                if (sweepResult.collides()) {
                    entity.move(sweepResult);
                    normalVelocity.set(0);
                }
            }

            //Friction
            float speed = orthogonalVelocity.length();
            if(physicsComponent.isOnGround() &&  speed > 0) {
                speed = Math.max(0, speed - physicsComponent.getFriction() * delta / 1000f);
                if(speed > 0) {
                    orthogonalVelocity.normalize().mul(speed);
                }
                else {
                    orthogonalVelocity.set(0);
                }
            }
        }

        //Recombine velocity components
        physicsComponent.getVelocity().set(normalVelocity).add(orthogonalVelocity);

        //Drag
        float speed = physicsComponent.getVelocity().length();
        if(speed > 0 && physicsComponent.getDrag() != 0) {
            speed = Math.max(0, speed - physicsComponent.getDrag());
            physicsComponent.getVelocity().normalize().mul(speed);
        }
    }

    private void handleMovement(Entity entity, CharacterPhysicsComponent physicsComponent, AbstractPositionComponent abstractPositionComponent,
                                AbstractCollisionComponent abstractCollisionComponent, Vector3f velocity, float delta, boolean handlingMovement) {

        float timeMultiplier = delta / 1000f;
        float speedPerSecond = velocity.length(); //Speed of this object per second
        float remainingSpeed = speedPerSecond * timeMultiplier; //Number of units left to move this frame
        Vector3f nextDirection = V3F.set(velocity).normalize(); //Unit vector of direction

        if(velocity.lengthSquared() == 0) {
            return;
        }

        SweepResult result;
        for(int i = 0; i < 3 && remainingSpeed != 0; i++) {
            result = entity.sweep(V3F_2.set(nextDirection).mul(remainingSpeed), physicsComponent.getSolidEntity());

            if(remainingSpeed > MathUtils.EPSILON) {
                entity.move(result);
            }

            result.remainder(V3F_3);
            remainingSpeed = V3F_3.length();

            if(result.collides()) {
                //Handle special character movement
                boolean hitWall = false;
                Vector3f projNormGravity = MathUtils.vectorProjection(result.getNormal(), physicsComponent.getGravity(), V3F_5);
                float floorAngle = getFloorAngle(physicsComponent.getGravity(), result.getNormal());

                if (handlingMovement) {
                        if (floorAngle > physicsComponent.getFloorAngle()) {
                            result.getNormal().sub(projNormGravity).normalize();
                            hitWall = true;
                        }
                        else {
                            physicsComponent.setOnGround(true);
                        }
                    }
                else {
                    if (floorAngle <= physicsComponent.getFloorAngle()) {
                        result.getNormal().set(projNormGravity.normalize());
                        physicsComponent.setOnGround(true);
                    }
                    else {
                        hitWall = true;
                    }
                }


                if(!hitWall && handlingMovement) {
                    nextDirection.mul(remainingSpeed);
                    Vector3f planeNormal = V3F_11.set(result.getNormal());
                    Vector3f lineDirection = V3F_12.set(physicsComponent.getGravity()).mul(-1).normalize();

                    if (planeNormal.dot(lineDirection) != 0) {
                        float t = -planeNormal.dot(nextDirection) / planeNormal.dot(lineDirection);
                        nextDirection.add(lineDirection.mul(t + MathUtils.EPSILON));
                    }
                }
                else {
                    result.slide(nextDirection);

                    if (nextDirection.lengthSquared() == 0) {
                        //Sliding object has squarely hit an edge, set remaining speed to 0. This will break the loop.
                        remainingSpeed = 0;
                    } else {

                        if(!hitWall || handlingMovement) {
                            speedPerSecond *= nextDirection.length();
                            remainingSpeed *= nextDirection.length();
                        }
                    }
                }

                if(nextDirection.lengthSquared() == 0) {
                    //Sliding object has squarely hit an edge, set remaining speed to 0. This will break the loop.
                    remainingSpeed = 0;
                }
                else {
                    nextDirection.normalize();
                }
            }
        }

        //Update original velocity
        if(nextDirection.lengthSquared() == 0) {
            velocity.set(0);
        }
        else {
            velocity.set(nextDirection).mul(speedPerSecond);
        }
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }

    public float getFloorAngle(Vector3f gravity, Vector3f floorNormal) {
        float floorAngle = V3F_6.set(gravity).mul(-1).normalize().dot(floorNormal);
        return (float) Math.acos(floorAngle);
    }
}
