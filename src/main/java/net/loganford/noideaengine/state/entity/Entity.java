package net.loganford.noideaengine.state.entity;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.graphics.Frame;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Sprite;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.components.*;
import net.loganford.noideaengine.state.entity.signals.*;
import net.loganford.noideaengine.state.entity.systems.AbstractEntitySystem;
import net.loganford.noideaengine.utils.math.MathUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

@RegisterComponent(BasicPositionComponent.class)
@RegisterComponent(BasicCollisionComponent.class)
public abstract class Entity<G extends Game, S extends Scene<G>> {
    @Getter private boolean destroyed = false;
    @Getter private float depth = 0;
    @Getter @Setter private S scene;
    @Getter @Setter private G game;
    @Getter @Setter boolean depthChanged = false;
    @Getter @Setter private boolean persistent;

    @Getter private AlarmSystem alarms;
    @Getter private Map<Class, Component> components;
    @Getter private Set<AbstractEntitySystem> systems;

    @Getter @Setter private Sprite sprite;

    @Getter private ComponentRemovedSignal componentRemovedSignal = new ComponentRemovedSignal();
    @Getter private DepthChangedSignal depthChangedSignal = new DepthChangedSignal();
    @Getter private DestructionSignal destructionSignal = new DestructionSignal();
    @Getter private BeforeMotionSignal beforeMotionSignal = new BeforeMotionSignal();
    @Getter private AfterMotionSignal afterMotionSignal = new AfterMotionSignal();

    //Components that are frequently accessed-- want to avoid a hashmap access
    @Getter private AbstractPositionComponent positionComponent;
    @Getter private BasicCollisionComponent collisionComponent;

    public Entity() {
        alarms = new AlarmSystem();
        components = new HashMap<>();
        systems = new HashSet<>();
        loadComponents();
    }

    /**
     * This method is called at the beginning of the step, after the entity has been placed in the scene.
     * @param game
     * @param scene
     */
    public void onCreate(G game, S scene) {
        scene.getEntitySystemEngine().processNewEntityComponents(this);
    }

    /**
     * Load components for entity based on annotations
     */
    private void loadComponents() {
        Class clazz = getClass();
        List<Class<? extends Component>> componentClazzList = new ArrayList<>();
        while(clazz != null) {
            for (Annotation annotation : clazz.getAnnotationsByType(RegisterComponent.class)) {
                Class<? extends Component> componentClazz = ((RegisterComponent)annotation).value();
                componentClazzList.add(componentClazz);
            }

            clazz = clazz.getSuperclass();
        }

        for(Class<? extends Component> componentClazz : componentClazzList) {
            try {
                Constructor<? extends Component> constructor = componentClazz.getConstructor();
                Component component = constructor.newInstance();
                component.init(this);
                addComponent(component);
            }
            catch(Exception e) {
                throw new GameEngineException("Unable to setup entity components", e);
            }
        }
    }

    /**
     * Call this method to destroy this entity. Entities are removed from the scene at the end of the game loop.
     */
    public final void destroy() {
        destroyed = true;
        onDestroy(game, scene);
        destructionSignal.dispatch(this);
    }

    /**
     * Sets the depth of the entity. Entities are drawn from the entity with the highest depth to the lowest. The entity
     * with the highest depth will appear below other entities. The step method will also be called in this order.
     * @param depth
     */
    public void setDepth(float depth) {
        if(depth != this.depth) {
            depthChanged = true;
            depthChangedSignal.dispatch(this);
        }
        this.depth = depth;
    }

    /**
     * This method is called prior to step
     * @param game
     * @param scene
     * @param delta
     */
    public void beforeStep(G game, S scene, float delta) {}

    /**
     * This method is called every step of the game loop. Delta time is passed through here. Do not call any draw
     * methods within this or any of the other step methods.
     * @param game
     * @param scene
     * @param delta
     */
    public void step(G game, S scene, float delta) {
        alarms.step(delta);

        if(sprite != null) {
            sprite.step(delta);
        }
    }

    /**
     * This method is called after step
     * @param game
     * @param scene
     * @param delta
     */
    public void afterStep(G game, S scene, float delta) {}

    /**
     * This method is called once per step. Render the entity here. Do not change the state of the entity-- do that in
     * the step method.
     * @param game
     * @param scene
     * @param renderer
     */
    public void render(G game, S scene, Renderer renderer) {
        if(sprite != null) {
            sprite.render(renderer, getX(), getY());
        }
    }

    /**
     * This method is called when the entity is destroyed. You may place custom logic here.
     * @param game
     * @param scene
     */
    public void onDestroy(G game, S scene) {}

    public void addComponent(Component component) {
        Class clazz = component.getClass();
        while(clazz != null) {
            components.put(clazz, component);
            clazz = clazz.getSuperclass();
        }

        //We maintain a cache of important component types to avoid lookups every step for every entity
        if(component instanceof AbstractPositionComponent) {
            positionComponent = (AbstractPositionComponent)component;
        }
        else if(component instanceof BasicCollisionComponent) {
            collisionComponent = (BasicCollisionComponent) component;
        }

        if(scene != null) {
            scene.getEntitySystemEngine().processNewEntityComponents(this);
        }
    }

    public void removeComponent(Component component) {
        Class clazz = component.getClass();
        while(clazz != null) {
            components.remove(clazz, component);
            clazz = clazz.getSuperclass();
        }
        if(scene != null) {
            componentRemovedSignal.dispatch(this);
        }
    }

    public Component getComponent(Class<Component> clazz) {
        return components.get(clazz);
    }

    public float getX() {
        return positionComponent.getX();
    }

    /**
     * Sets the x position of the entity.
     * @param x
     */
    public void setX(float x) {
        positionComponent.setX(x);
    }

    public float getY() {
        return positionComponent.getY();
    }

    /**
     * Sets the y position of the entity.
     * @param y
     */
    public void setY(float y) {
        positionComponent.setY(y);
    }

    public float getZ() {
        return positionComponent.getZ();
    }

    public void setZ(float z) {
        positionComponent.setZ(z);
    }

    /**
     * Sets the position of the entity. This method is slightly faster then setting both x and y independently.
     * @param x
     * @param y
     */
    public void setPos(float x, float y) {
        positionComponent.setPos(x, y);
    }

    public void setPos(float x, float y, float z) {
        positionComponent.setPos(x, y, z);
    }

    public Shape getShape() {
        return collisionComponent.getShape();
    }

    /**
     * Sets the collision mask of this entity.
     * @param shape
     */
    public void setShape(Shape shape) {
        collisionComponent.setShape(shape);
    }

    public float getShapeOffsetX() {
        return collisionComponent.getShapeOffsetX();
    }

    /**
     * Sets the y offset of the collision mask from the entity's position.
     * @param shapeOffsetX
     */
    public void setShapeOffsetX(float shapeOffsetX) {
        collisionComponent.setShapeOffsetX(shapeOffsetX);
    }

    public float getShapeOffsetY() {
        return collisionComponent.getShapeOffsetY();
    }

    /**
     * Sets the y offset of the collision mask from the entity's position.
     * @param shapeOffsetY
     */
    public void setShapeOffsetY(float shapeOffsetY) {
        collisionComponent.setShapeOffsetY(shapeOffsetY);
    }

    public float getShapeOffsetZ() {
        return collisionComponent.getShapeOffsetZ();
    }

    public void setShapeOffsetZ(float shapeOffsetZ) {
        collisionComponent.setShapeOffsetZ(shapeOffsetZ);
    }

    /**
     * Creates a rectangular collision mask based off of the currently assigned sprite.
     */
    public void createMaskFromSprite() {
        Frame firstFrame = sprite.getFrames().get(0);
        Rect rect = new Rect(getX(), getY(), firstFrame.getImage().getWidth(), firstFrame.getImage().getHeight());
        setShapeOffsetX(firstFrame.getOffsetX());
        setShapeOffsetY(firstFrame.getOffsetY());
        setShape(rect);
    }

    public void beginScene(G game, S scene) {}
    public void endScene(G game, S scene) {}

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @return
     */
    public boolean collidesWith(Class<? extends Entity> clazz) {
        return getScene().getCollisionSystem().collidesWith(getShape(), clazz);
    }

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @return
     */
    public <C extends Entity> C getCollision(Class<C> clazz) {
        return getScene().getCollisionSystem().getCollision(getShape(), clazz);
    }

    public <C extends Entity> C getCollisionAt(Class<C> clazz, float x, float y) {
        getShape().setPosition(x - getShapeOffsetX(), y - getShapeOffsetY());
        C entity = getScene().getCollisionSystem().getCollision(getShape(), clazz);
        getShape().setPosition(this.getX() - getShapeOffsetX(), this.getY() - getShapeOffsetY());
        return entity;
    }

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends Entity> List<C> getCollisions(Class<C> clazz) {
        return getScene().getCollisionSystem().getCollisions(getShape(), clazz);
    }

    public <C extends Entity> List<C> getCollisionsAt(Class<C> clazz, float x, float y) {
        getShape().setPosition(x - getShapeOffsetX(), y - getShapeOffsetY());
        List<C> entities = getScene().getCollisionSystem().getCollisions(getShape(), clazz);
        getShape().setPosition(this.getX() - getShapeOffsetX(), this.getY() - getShapeOffsetY());
        return entities;
    }

    /**
     * Checks for a potential collision assuming that this entity is moved to a certain position.
     * @param clazz
     * @param x
     * @param y
     * @return
     */
    public boolean placeMeeting(Class<? extends Entity> clazz, float x, float y) {
        getShape().setPosition(x - getShapeOffsetX(), y - getShapeOffsetY());
        boolean returnValue = collidesWith(clazz);
        getShape().setPosition(this.getX() - getShapeOffsetX(), this.getY() - getShapeOffsetY());
        return returnValue;
    }

    /**
     * See the placeMeeting method.
     * @param clazz
     * @param x
     * @param y
     * @return
     */
    public boolean placeFree(Class<? extends Entity> clazz, float x, float y) {
        return !placeMeeting(clazz, x, y);
    }


    /**
     * Gets the nearest entity to this entity.
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends Entity> C nearest(Class<C> clazz) {
        return getScene().nearest(clazz, getX(), getY());
    }

    /**
     * Gets the furthest entity to this entity.
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends Entity> C furthest(Class<C> clazz) {
        return getScene().furthest(clazz, getX(), getY());
    }

    /**
     * Gets the N closest entities to this entity, sorted nearest to furthest.
     * @param clazz
     * @param count
     * @param <C>
     * @return
     */
    public <C extends Entity> List<EntityDistancePair<C>> nearest(Class<C> clazz, int count) {
        return getScene().nearest(clazz, getX(), getY(), count);
    }

    /**
     * Gets the distance between this entity and another entity.
     * @param other
     * @return
     */
    public float distance(Entity other) {
        return MathUtils.distance(getX(), getY(), other.getX(), other.getY());
    }

    /**
     * Gets the distance^2 between this entity and another entity.
     * @param other
     * @return
     */
    public float distanceSqr(Entity other) {
        return MathUtils.distanceSqr(getX(), getY(), other.getX(), other.getY());
    }
}
