package net.loganford.noideaengine.entity;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.components.collision.AbstractCollisionComponent;
import net.loganford.noideaengine.components.collision.BasicCollisionComponent;
import net.loganford.noideaengine.components.physics.AbstractPhysicsComponent;
import net.loganford.noideaengine.components.physics.PhysicsComponent;
import net.loganford.noideaengine.graphics.Frame;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Sprite;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.components.*;
import net.loganford.noideaengine.entity.signals.*;
import net.loganford.noideaengine.systems.EntitySystem;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.InheritComponents;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import net.loganford.noideaengine.utils.annotations.UnregisterComponent;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

@RegisterComponent(BasicPositionComponent.class)
@RegisterComponent(BasicCollisionComponent.class)
public class Entity {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();
    private static Vector3f V3F_4 = new Vector3f();
    private static Vector3f V3F_5 = new Vector3f();

    @Getter @Setter private Scene scene;
    @Getter @Setter private Game game;
    @Getter @Setter private boolean persistent;
    @Getter private boolean destroyed = false;
    @Scriptable @Getter private float depth = 0;
    @Getter @Setter boolean depthChanged = false;

    @Getter private AlarmSystem alarms;
    private Map<Class, Component> components;
    private Map<Class, Component> unmodifiableComponents;
    @Getter private Set<EntitySystem> systems;

    //Entity'Scene sprite, when set gets drawn in the render method
    @Getter(onMethod = @__({@Scriptable})) @Setter(onMethod = @__({@Scriptable})) private Sprite sprite;

    //Signals
    @Getter private ComponentAddedSignal componentAddedSignal = new ComponentAddedSignal();
    @Getter private ComponentRemovedSignal componentRemovedSignal = new ComponentRemovedSignal();
    @Getter private DepthChangedSignal depthChangedSignal = new DepthChangedSignal();
    @Getter private DestructionSignal destructionSignal = new DestructionSignal();
    @Getter private BeforeMotionSignal beforeMotionSignal = new BeforeMotionSignal();
    @Getter private AfterMotionSignal afterMotionSignal = new AfterMotionSignal();

    //Components that are frequently accessed-- want to avoid a hashmap access
    @Getter private AbstractPositionComponent positionComponent;
    @Getter private AbstractCollisionComponent collisionComponent;
    @Getter private AbstractPhysicsComponent physicsComponent;

    /**
     * Creates the entity at position (0,0,0), but does not add it to the scene.
     */
    public Entity() {
        alarms = new AlarmSystem();
        components = new HashMap<>();
        unmodifiableComponents = Collections.unmodifiableMap(components);
        systems = new HashSet<>();
        loadAndInitializeComponents();
    }

    /**
     * This method is called at the beginning of the step, after the entity has been placed in the scene.
     * @param game the current game
     * @param scene the current scene
     */
    public void onCreate(Game game, Scene scene) {
        scene.getEntitySystemEngine().processNewEntityComponents(this);
    }

    /**
     * Call this method to destroy this entity. Entities are removed from the scene at the end of the game loop.
     */
    @Scriptable
    public final void destroy() {
        destroyed = true;
        onDestroy(game, scene);
        destructionSignal.dispatch(this);
    }

    /**
     * This method is called prior to step.
     * @param game the current game
     * @param scene the current scene
     * @param delta time since the previous frame, in milliseconds
     */
    public void beforeStep(Game game, Scene scene, float delta) {}

    /**
     * This method is called every step of the game loop. Delta time is passed through here. Do not call any draw
     * methods within this or any of the other step methods.
     * @param game the current game
     * @param scene the current scene
     * @param delta time since the previous frame, in milliseconds
     */
    public void step(Game game, Scene scene, float delta) {
        alarms.step(delta);

        if(sprite != null) {
            sprite.step(delta);
        }
    }

    /**
     * This method is called after step.
     * @param game the current game
     * @param scene the current scene
     * @param delta time since the previous frame, in milliseconds
     */
    public void afterStep(Game game, Scene scene, float delta) {}

    /**
     * This method is called once per step. Render the entity here. Do not change the state of the entity-- do that in
     * the step method.
     * @param game the current game
     * @param scene the current scene
     * @param renderer reference to the renderer for primate rendering and more advanced drawing functionality
     */
    public void render(Game game, Scene scene, Renderer renderer) {
        if(sprite != null) {
            sprite.render(renderer, getX(), getY());
        }
    }

    /**
     * This method is called when the entity is destroyed. You may place custom logic here.
     * @param game the current game
     * @param scene the current scene
     */
    public void onDestroy(Game game, Scene scene) {}

    /**
     * Called after onCreate when the scene begins.
     * @param game the current game
     * @param scene the current scene
     */
    public void beginScene(Game game, Scene scene) {}

    /**
     * Called when the scene ends.
     * @param game the current game
     * @param scene the current scene
     */
    public void endScene(Game game, Scene scene) {}

    /**
     * Sets the depth of the entity. Entities are drawn from the entity with the highest depth to the lowest. The entity
     * with the highest depth will appear below other entities. The step method will also be called in this order.
     * @param depth the desired depth
     */
    @Scriptable
    public void setDepth(float depth) {
        if(depth != this.depth) {
            depthChanged = true;
            depthChangedSignal.dispatch(this);
        }
        this.depth = depth;
    }

    /**
     * Gets the nearest entity to this entity.
     * @param clazz class of entity to find
     * @return the nearest entity
     */
    @Scriptable
    public <C> C nearest(Class<C> clazz) {
        return getScene().nearest(clazz, getX(), getY());
    }

    /**
     * Gets the furthest entity to this entity.
     * @param clazz class of entity to find
     * @return the furthest entity
     */
    @Scriptable
    public <C> C furthest(Class<C> clazz) {
        return getScene().furthest(clazz, getX(), getY());
    }

    /**
     * Gets the N closest entities to this entity, sorted nearest to furthest.
     * @param clazz class of entity to find
     * @param count the number of entities to find
     * @return a list of nearby entities
     */
    @Scriptable
    public <C> List<EntityDistancePair<C>> nearest(Class<C> clazz, int count) {
        return getScene().nearest(clazz, getX(), getY(), count);
    }

    /**
     * Gets the distance between this entity and another entity.
     * @param other entity to find the distance to
     * @return the distance between the specified entity and this one
     */
    @Scriptable
    public float distance(Entity other) {
        return MathUtils.distance(getX(), getY(), getZ(), other.getX(), other.getY(), other.getZ());
    }

    /**
     * Gets the distance^2 between this entity and another entity.
     * @param other entity to find the distance to
     * @return the square of the distance between the specified entity and this one
     */
    @Scriptable
    public float distanceSqr(Entity other) {
        return MathUtils.distanceSqr(getX(), getY(), getZ(), other.getX(), other.getY(), other.getZ());
    }

    /**
     * Adds a component to the entity. A call to this may add this entity to any of the scene'Scene systems.
     * @param component the component to add to the entity
     */
    public void addComponent(Component component) {
        Class clazz = component.getClass();
        while(clazz != null) {
            components.put(clazz, component);
            clazz = clazz.getSuperclass();
        }

        if(scene != null) {
            //Tell the entity system engine that components have been added
            scene.getEntitySystemEngine().processNewEntityComponents(this);
        }

        componentAddedSignal.dispatch(this);

        //We maintain a cache of important component types to avoid lookups every step for every entity
        if(component instanceof AbstractPositionComponent) {
            positionComponent = (AbstractPositionComponent)component;
        }
        else if(component instanceof AbstractCollisionComponent) {
            collisionComponent = (AbstractCollisionComponent) component;
        }
        else if(component instanceof AbstractPhysicsComponent) {
            physicsComponent = (PhysicsComponent) component;
        }
    }

    /**
     * Removes a component from the entity. This may remove the entity from the systems which it is registered to.
     * @param clazz the component class which to remove.
     */
    public void removeComponent(Class<? extends Component> clazz) {
        Component component = getComponent(clazz);
        boolean removed = false;

        for(Iterator<Map.Entry<Class, Component>> it = components.entrySet().iterator(); it.hasNext();) {
            Map.Entry<Class, Component> entry = it.next();
            if(entry.getValue() == component) {
                it.remove();
                removed = true;
            }
        }

        if(removed && scene != null) {
            componentRemovedSignal.dispatch(this);

            //Update cache
            if(component instanceof AbstractPositionComponent) {
                positionComponent = null;
            }
            else if(component instanceof AbstractCollisionComponent) {
                collisionComponent = null;
            }
            else if(component instanceof AbstractPhysicsComponent) {
                physicsComponent = null;
            }
        }
    }

    /**
     * Returns a map of components assigned to this entity. This map cannot be modified.
     * @return the component map
     */
    public Map<Class, Component> getComponents() {
        return unmodifiableComponents;
    }

    /**
     * Returns a specific component.
     * @param clazz class of component
     * @return a component, or null if  it doesn't belong to this entity.
     */
    @SuppressWarnings("unchecked")
    public <C extends Component> C getComponent(Class<C> clazz) {
        return (C) components.get(clazz);
    }

    /**
     * Returns the x position of the entity.
     * @return the x position
     */
    @Scriptable
    public float getX() {
        return positionComponent.getX();
    }

    /**
     * Sets the x position of the entity.
     * @param x the x position
     */
    @Scriptable
    public void setX(float x) {
        positionComponent.setX(x);
    }

    /**
     * Returns the y position of the entity.
     * @return the y position.
     */
    public float getY() {
        return positionComponent.getY();
    }

    /**
     * Sets the y position of the entity.
     * @param y the y position
     */
    @Scriptable
    public void setY(float y) {
        positionComponent.setY(y);
    }

    /**
     * Returns the z position of the entity.
     * @return the z position
     */
    @Scriptable
    public float getZ() {
        return positionComponent.getZ();
    }

    /**
     * Sets the z position of the entity.
     * @param z the z position
     */
    @Scriptable
    public void setZ(float z) {
        positionComponent.setZ(z);
    }

    /**
     * Returns a vector representing the entity in space.
     * @return the vector
     */
    public Vector3fc getPos() {
        return positionComponent.getPos();
    }

    /**
     * Returns a matrix representing this entity in space.
     * @return the matrix
     */
    public Matrix4f getPosMatrix() {
        return positionComponent.getMatrix();
    }

    /**
     * Moves the entity around in space.
     * @param v a vector of the desired position
     */
    public void setPos(Vector3fc v) {
        positionComponent.setPos(v);
    }

    /**
     * Sets the position of the entity. This method is slightly faster then setting both x and y independently.
     * @param x the x position
     * @param y the y position
     */
    @Scriptable
    public void setPos(float x, float y) {
        positionComponent.setPos(x, y);
    }

    /**
     * Sets the position of the entity in 3D space in a single call.
     * @param x the x position
     * @param y the y position
     * @param z the z position
     */
    @Scriptable
    public void setPos(float x, float y, float z) {
        positionComponent.setPos(x, y, z);
    }

    /**
     * Moves the entity by a relative amount.
     * @param x - x amount
     * @param y - y amount
     */
    @Scriptable
    public void move(float x, float y) {
        positionComponent.move(x, y);
    }

    /**
     * Moves the entity by a relative amount.
     * @param x - x amount
     * @param y - y amount
     * @param z - z amount
     */
    @Scriptable
    public void move(float x, float y, float z) {
        positionComponent.move(x, y, z);
    }

    /**
     * Moves the entity by a relative amount.
     * @param v amount to move the entity
     */
    public void move(Vector3fc v) {
        move(v.x(), v.y(), v.z());
    }

    /**
     * Sets the position to the result of a sweep test
     * @param result the sweep test result
     */
    @Scriptable
    public void move(SweepResult result) {
        move(V3F.set(result.getVelocity()).mul(Math.max(0, result.getDistance() - 0.01f)));
    }

    /**
     * Retrieves the current collision mask of the entity.
     * @return the current collision mask
     */
    @Scriptable
    public Shape getShape() {
        return collisionComponent.getShape();
    }

    /**
     * Sets the collision mask of this entity.
     * @param shape the desired collision mask
     */
    @Scriptable
    public void setShape(Shape shape) {
        collisionComponent.setShape(shape);
    }

    /**
     * Gets the x offset of the collision mask.
     * @return the x offset
     */
    @Scriptable
    public float getShapeOffsetX() {
        return collisionComponent.getShapeOffsetX();
    }

    /**
     * Sets the x offset of the collision mask.
     * @param shapeOffsetX the x offset
     */
    @Scriptable
    public void setShapeOffsetX(float shapeOffsetX) {
        collisionComponent.setShapeOffsetX(shapeOffsetX);
    }

    /**
     * Gets the y offset of the collision mask.
     * @return the y offset
     */
    @Scriptable
    public float getShapeOffsetY() {
        return collisionComponent.getShapeOffsetY();
    }

    /**
     * Sets the y offset of the collision mask.
     * @param shapeOffsetY the y offset
     */
    @Scriptable
    public void setShapeOffsetY(float shapeOffsetY) {
        collisionComponent.setShapeOffsetY(shapeOffsetY);
    }

    /**
     * Gets the z offset of the collision mask.
     * @return the z offset
     */
    @Scriptable
    public float getShapeOffsetZ() {
        return collisionComponent.getShapeOffsetZ();
    }

    /**
     * Sets the z offset of the collision mask.
     * @param shapeOffsetZ the z offset
     */
    @Scriptable
    public void setShapeOffsetZ(float shapeOffsetZ) {
        collisionComponent.setShapeOffsetZ(shapeOffsetZ);
    }

    /**
     * Creates a rectangular collision mask based off of the currently assigned sprite.
     */
    @Scriptable
    public void createMaskFromSprite() {
        Frame firstFrame = sprite.getFrames().get(0);
        Rect rect = new Rect(getX(), getY(), sprite.getWidth(), sprite.getHeight());
        setShapeOffsetX(sprite.getOffsetX());
        setShapeOffsetY(sprite.getOffsetY());
        setShape(rect);
    }

    /**
     * Checks for collisions with entities.
     * @param clazz class of entities to search for collisions against
     * @return whether this entity collides with another of the specific class
     */
    @Scriptable
    public boolean collidesWith(Class<?> clazz) {
        return getScene().getCollisionSystem().collidesWith(getShape(), clazz);
    }

    /**
     * Checks for collisions with entities.
     * @param clazz class of entities to search for collisions against
     * @return one entity of the specific class which collides with this entity, or null if none exists
     */
    @Scriptable
    public <C> C getCollision(Class<C> clazz) {
        return getScene().getCollisionSystem().getCollision(getShape(), clazz);
    }

    /**
     * Checks for collisions with entities at a hypothetical location.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @return one entity of the specific class which collides with this entity, or null if none exists
     */
    @Scriptable
    public <C> C getCollisionAt(Class<C> clazz, float x, float y) {
        return getCollisionAt(clazz, x, y, 0);
    }

    /**
     * Checks for collisions with entities at a hypothetical location.
     * @param clazz class of entities to search for collisions against
     * @param v position to check for collisions
     * @return one entity of the specific class which collides with this entity, or null if none exists
     */
    public <C> C getCollisionAt(Class<C> clazz, Vector3fc v) {
        return getCollisionAt(clazz, v.x(), v.y(), v.z());
    }

    /**
     * Checks for collisions with entities at a hypothetical location.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @param z z position to check for collisions
     * @return one entity of the specific class which collides with this entity, or null if none exists
     */
    @Scriptable
    public <C> C getCollisionAt(Class<C> clazz, float x, float y, float z) {
        getShape().setPosition(x - getShapeOffsetX(), y - getShapeOffsetY(), z - getShapeOffsetZ());
        C entity = getScene().getCollisionSystem().getCollision(getShape(), clazz);
        getShape().setPosition(this.getX() - getShapeOffsetX(), this.getY() - getShapeOffsetY(), this.getZ() - getShapeOffsetZ());
        return entity;
    }

    /**
     * Checks for collisions with entities.
     * @param clazz class of entities to search for collisions against
     * @return a list of all entities which collide with the entity
     */
    @Scriptable
    public <C> List<C> getCollisions(Class<C> clazz) {
        return getScene().getCollisionSystem().getCollisions(getShape(), clazz);
    }

    /**
     * Checks for collisions with entities at a hypothetical location.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @return a list of all entities which collide with the entity
     */
    @Scriptable
    public <C> List<C> getCollisionsAt(Class<C> clazz, float x, float y) {
        return getCollisionsAt(clazz, x, y, 0);
    }

    /**
     * Checks for collisions with entities at a hypothetical location.
     * @param clazz class of entities to search for collisions against
     * @param v position to check for collisions
     * @return a list of all entities which collide with the entity
     */
    public <C> List<C> getCollisionsAt(Class<C> clazz, Vector3fc v) {
        return getCollisionsAt(clazz, v.x(), v.y(), v.z());
    }

    /**
     * Checks for collisions with entities at a hypothetical location.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @param z z position to check for collisions
     * @return a list of all entities which collide with the entity
     */
    @Scriptable
    public <C> List<C> getCollisionsAt(Class<C> clazz, float x, float y, float z) {
        getShape().setPosition(x - getShapeOffsetX(), y - getShapeOffsetY(), z - getShapeOffsetZ());
        List<C> entities = getScene().getCollisionSystem().getCollisions(getShape(), clazz);
        getShape().setPosition(this.getX() - getShapeOffsetX(), this.getY() - getShapeOffsetY(), this.getZ() - getShapeOffsetZ());
        return entities;
    }

    /**
     * Checks for a potential collision assuming that this entity is moved to a certain position.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @return whether a collision occurs at the specified location
     */
    @Scriptable
    public boolean placeMeeting(Class<?> clazz, float x, float y) {
        return placeMeeting(clazz, x, y, 0);
    }

    /**
     * The opposite of the placeMeeting method-- checks if a certain position is free from collisions with a certain entity.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @return whether a certain position is free from collisions.
     */
    @Scriptable
    public boolean placeFree(Class<?> clazz, float x, float y) {
        return placeFree(clazz, x, y, 0);
    }

    /**
     *  Checks for a potential collision assuming that this entity is moved to a certain position.
     * @param clazz class of entities to search for collisions against
     * @param v position to check for collisions
     * @return whether a collision occurs at the specified location
     */
    public boolean placeMeeting(Class<?> clazz, Vector3fc v) {
        return placeMeeting(clazz, v.x(), v.y(), v.z());
    }

    /**
     * The opposite of the placeMeeting method-- checks if a certain position is free from collisions with a certain entity.
     * @param clazz class of entities to search for collisions against
     * @param v position to check for collisions
     * @return whether a certain position is free from collisions.
     */
    public boolean placeFree(Class<?> clazz, Vector3fc v) {
        return placeFree(clazz, v.x(), v.y(), v.z());
    }

    /**
     * Checks for a potential collision assuming that this entity is moved to a certain position.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @param z z position to check for collisions
     * @return whether a collision occurs at the specified location
     */
    @Scriptable
    public boolean placeMeeting(Class<?> clazz, float x, float y, float z) {
        getShape().setPosition(x - getShapeOffsetX(), y - getShapeOffsetY(), z - getShapeOffsetZ());
        boolean returnValue = collidesWith(clazz);
        getShape().setPosition(this.getX() - getShapeOffsetX(), this.getY() - getShapeOffsetY(), this.getZ() - getShapeOffsetZ());
        return returnValue;
    }

    /**
     * The opposite of the placeMeeting method-- checks if a certain position is free from collisions with a certain entity.
     * @param clazz class of entities to search for collisions against
     * @param x x position to check for collisions
     * @param y y position to check for collisions
     * @param z z position to check for collisions
     * @return whether a certain position is free from collisions.
     */
    @Scriptable
    public boolean placeFree(Class<?> clazz, float x, float y, float z) {
        return !placeMeeting(clazz, x, y, z);
    }

    /**
     * Sweeps this entity'Scene mask across the game world until it impacts another solid.
     * @param result the result of the sweep test
     * @param velocity vector to sweep along
     * @param clazz type of entity to collide against
     */
    public <E> void sweep(SweepResult result, Vector3fc velocity, Class<E> clazz) {
        getScene().getCollisionSystem().sweep(result, this.getShape(), velocity, clazz);
    }

    /**
     * Sweeps this entity'Scene mask across the game world until it impacts another solid.
     * @param result the result of the sweep test
     * @param vx x component of vector to sweep along
     * @param vy y component of vector to sweep along
     * @param clazz type of entity to collide against
     */
    public <E> void sweep(SweepResult<E> result, float vx, float vy, Class<E> clazz) {
        getScene().getCollisionSystem().sweep(result, this.getShape(), vx, vy, clazz);
    }

    /**
     * Sweeps this entity'Scene mask across the game world until it impacts another solid.
     * @param result the result of the sweep test
     * @param vx x component of vector to sweep along
     * @param vy y component of vector to sweep along
     * @param vz z component of vector to sweep along
     * @param clazz type of entity to collide against
     */
    public <E> void sweep(SweepResult<E> result, float vx, float vy, float vz, Class<E> clazz) {
        getScene().getCollisionSystem().sweep(result, this.getShape(), vx, vy, vz, clazz);
    }

    /**
     * Sweeps this entity'Scene mask across the game world until it impacts another solid.
     * @param velocity vector to sweep along
     * @param clazz type of entity to collide against
     * @return the result of the sweep test
     */
    public <E> SweepResult<E> sweep(Vector3fc velocity, Class<E> clazz) {
        return getScene().getCollisionSystem().sweep(this.getShape(), velocity, clazz);
    }

    /**
     * Sweeps this entity'Scene mask across the game world until it impacts another solid.
     * @param vx x component of vector to sweep along
     * @param vy y component of vector to sweep along
     * @param clazz type of entity to collide against
     * @return the result of the sweep test
     */
    @Scriptable
    public <E> SweepResult<E> sweep(float vx, float vy, Class<E> clazz) {
        return getScene().getCollisionSystem().sweep(this.getShape(), vx, vy, clazz);
    }

    /**
     * Sweeps this entity'Scene mask across the game world until it impacts another solid.
     * @param vx x component of vector to sweep along
     * @param vy y component of vector to sweep along
     * @param vz z component of vector to sweep along
     * @param clazz type of entity to collide against
     * @return the result of the sweep test
     */
    @Scriptable
    public <E> SweepResult<E> sweep(float vx, float vy, float vz, Class<E> clazz) {
        return getScene().getCollisionSystem().sweep(this.getShape(), vx, vy, vy, clazz);
    }

    /**
     * Creates components based off of this entity'Scene annotations and adds them to the entity
     */
    private void loadAndInitializeComponents() {
        Class clazz = getClass();
        List<Pair<Class<? extends Component>, Argument[]>> componentClazzList = getComponentsForClass(getClass());

        for(Pair<Class<? extends Component>, Argument[]> componentAnnotation : componentClazzList) {
            try {
                Class<? extends Component> componentClass = componentAnnotation.getLeft();
                Argument[] arguments = componentAnnotation.getRight();
                Constructor<? extends Component> constructor = componentClass.getConstructor(Argument[].class);
                Component component = constructor.newInstance(new Object[]{arguments});
                component.componentAdded(this);
                addComponent(component);
            }
            catch(Exception e) {
                throw new GameEngineException("Unable to setup entity components", e);
            }
        }
    }

    /**
     * Recursively retrieves a list of components based off annotations from the superclass down to the current class.
     * @param clazz current class
     * @return a list of all of the loaded components.
     */
    private List<Pair<Class<? extends Component>, Argument[]>> getComponentsForClass(Class clazz) {
        List<Pair<Class<? extends Component>, Argument[]>> componentClazzList = new ArrayList<>();
        if(clazz != null) {

            Annotation inherit = clazz.getAnnotation(InheritComponents.class);
            if(inherit == null || ((InheritComponents) inherit).value()) {
                componentClazzList.addAll(getComponentsForClass(clazz.getSuperclass()));
            }

            for(Annotation annotation : clazz.getAnnotationsByType(UnregisterComponent.class)) {
                Class<? extends Component> componentClazz = ((UnregisterComponent)annotation).value();
                componentClazzList.removeIf(p -> p.getLeft().equals(componentClazz));
            }

            for(Annotation annotation : clazz.getAnnotationsByType(RegisterComponent.class)) {
                Class<? extends Component> componentClazz = ((RegisterComponent)annotation).value();
                Argument[] arguments = ((RegisterComponent)annotation).arguments();
                componentClazzList.add(new MutablePair<Class<? extends Component>, Argument[]>(componentClazz, arguments));
            }
        }
        return componentClazzList;
    }
}
