package net.loganford.noideaengine.state.entity;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.graphics.Frame;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Sprite;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.components.*;
import net.loganford.noideaengine.state.entity.signals.*;
import net.loganford.noideaengine.state.entity.systems.EntitySystem;
import net.loganford.noideaengine.utils.math.MathUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

@RegisterComponent(BasicPositionComponent.class)
@RegisterComponent(BasicCollisionComponent.class)
public class Entity<G extends Game, S extends Scene<G>> {
    @Getter @Setter private S scene;
    @Getter @Setter private G game;
    @Getter @Setter private boolean persistent;
    @Getter private boolean destroyed = false;
    @Scriptable @Getter private float depth = 0;
    @Getter @Setter boolean depthChanged = false;

    @Getter private AlarmSystem alarms;
    private Map<Class, Component> components;
    private Map<Class, Component> unmodifiableComponents;
    @Getter private Set<EntitySystem> systems;

    //Entity's sprite, when set gets drawn in the render method
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
    @Getter private BasicCollisionComponent collisionComponent;

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
    public void onCreate(G game, S scene) {
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
    public void beforeStep(G game, S scene, float delta) {}

    /**
     * This method is called every step of the game loop. Delta time is passed through here. Do not call any draw
     * methods within this or any of the other step methods.
     * @param game the current game
     * @param scene the current scene
     * @param delta time since the previous frame, in milliseconds
     */
    public void step(G game, S scene, float delta) {
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
    public void afterStep(G game, S scene, float delta) {}

    /**
     * This method is called once per step. Render the entity here. Do not change the state of the entity-- do that in
     * the step method.
     * @param game the current game
     * @param scene the current scene
     * @param renderer reference to the renderer for primate rendering and more advanced drawing functionality
     */
    public void render(G game, S scene, Renderer renderer) {
        if(sprite != null) {
            sprite.render(renderer, getX(), getY());
        }
    }

    /**
     * This method is called when the entity is destroyed. You may place custom logic here.
     * @param game the current game
     * @param scene the current scene
     */
    public void onDestroy(G game, S scene) {}

    /**
     * Called after onCreate when the scene begins.
     * @param game the current game
     * @param scene the current scene
     */
    public void beginScene(G game, S scene) {}

    /**
     * Called when the scene ends.
     * @param game the current game
     * @param scene the current scene
     */
    public void endScene(G game, S scene) {}

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
    public <C extends Entity> C nearest(Class<C> clazz) {
        return getScene().nearest(clazz, getX(), getY());
    }

    /**
     * Gets the furthest entity to this entity.
     * @param clazz class of entity to find
     * @return the furthest entity
     */
    @Scriptable
    public <C extends Entity> C furthest(Class<C> clazz) {
        return getScene().furthest(clazz, getX(), getY());
    }

    /**
     * Gets the N closest entities to this entity, sorted nearest to furthest.
     * @param clazz class of entity to find
     * @param count the number of entities to find
     * @return a list of nearby entities
     */
    @Scriptable
    public <C extends Entity> List<EntityDistancePair<C>> nearest(Class<C> clazz, int count) {
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
     * Adds a component to the entity. A call to this may add this entity to any of the scene's systems.
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
        else if(component instanceof BasicCollisionComponent) {
            collisionComponent = (BasicCollisionComponent) component;
        }
    }

    /**
     * Removes a component from the entity. This may remove the entity from the systems which it is registered to.
     * @param component the component which to remove.
     */
    public void removeComponent(Component component) {
        component.componentRemoved();
        Class clazz = component.getClass();
        while(clazz != null) {
            if(components.get(clazz).equals(component)) {
                components.remove(clazz, component);
                clazz = clazz.getSuperclass();
            }
        }
        if(scene != null) {
            componentRemovedSignal.dispatch(this);
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
        positionComponent.setPos((positionComponent.getX() + x), (positionComponent.getY() + y));
    }

    /**
     * Moves the entity by a relative amount.
     * @param x - x amount
     * @param y - y amount
     * @param z - z amount
     */
    @Scriptable
    public void move(float x, float y, float z) {
        positionComponent.setPos((positionComponent.getX() + x), (positionComponent.getY() + y), (positionComponent.getZ() + z));
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
    public boolean collidesWith(Class<? extends Entity> clazz) {
        return getScene().getCollisionSystem().collidesWith(getShape(), clazz);
    }

    /**
     * Checks for collisions with entities.
     * @param clazz class of entities to search for collisions against
     * @return one entity of the specific class which collides with this entity, or null if none exists
     */
    @Scriptable
    public <C extends Entity> C getCollision(Class<C> clazz) {
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
    public <C extends Entity> C getCollisionAt(Class<C> clazz, float x, float y) {
        return getCollisionAt(clazz, x, y, 0);
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
    public <C extends Entity> C getCollisionAt(Class<C> clazz, float x, float y, float z) {
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
    public <C extends Entity> List<C> getCollisions(Class<C> clazz) {
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
    public <C extends Entity> List<C> getCollisionsAt(Class<C> clazz, float x, float y) {
        return getCollisionsAt(clazz, x, y, 0);
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
    public <C extends Entity> List<C> getCollisionsAt(Class<C> clazz, float x, float y, float z) {
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
    public boolean placeMeeting(Class<? extends Entity> clazz, float x, float y) {
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
    public boolean placeFree(Class<? extends Entity> clazz, float x, float y) {
        return placeFree(clazz, x, y, 0);
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
    public boolean placeMeeting(Class<? extends Entity> clazz, float x, float y, float z) {
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
    public boolean placeFree(Class<? extends Entity> clazz, float x, float y, float z) {
        return !placeMeeting(clazz, x, y, z);
    }

    /**
     * Creates components based off of this entity's annotations and adds them to the entity
     */
    private void loadAndInitializeComponents() {
        Class clazz = getClass();
        List<Class<? extends Component>> componentClazzList = getComponentsForClass(getClass());

        for(Class<? extends Component> componentClazz : componentClazzList) {
            try {
                Constructor<? extends Component> constructor = componentClazz.getConstructor();
                Component component = constructor.newInstance();
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
    private List<Class<? extends Component>> getComponentsForClass(Class clazz) {
        List<Class<? extends Component>> componentClazzList = new ArrayList<>();
        if(clazz != null) {

            Annotation inherit = clazz.getAnnotation(InheritComponents.class);
            if(inherit == null || ((InheritComponents) inherit).value()) {
                componentClazzList.addAll(getComponentsForClass(clazz.getSuperclass()));
            }

            for(Annotation annotation : clazz.getAnnotationsByType(RegisterComponent.class)) {
                Class<? extends Component> componentClazz = ((RegisterComponent)annotation).value();
                componentClazzList.add(componentClazz);
            }

            for(Annotation annotation : clazz.getAnnotationsByType(UnregisterComponent.class)) {
                Class<? extends Component> componentClazz = ((UnregisterComponent)annotation).value();
                componentClazzList.remove(componentClazz);
            }
        }
        return componentClazzList;
    }
}
