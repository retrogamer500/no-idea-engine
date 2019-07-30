package net.loganford.noideaengine.state.entity;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.alarm.AlarmSystem;
import net.loganford.noideaengine.graphics.Frame;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Sprite;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.math.MathUtils;

import java.util.List;

public abstract class Entity<G extends Game, S extends Scene<G>> {
    @Getter private boolean destroyed = false;
    @Getter private float depth = 0;
    @Getter @Setter private S scene;
    @Getter @Setter private G game;
    @Getter @Setter boolean depthChanged = false;
    @Getter @Setter private boolean persistent;

    @Getter private AlarmSystem alarms;

    @Getter @Setter private Sprite sprite;
    @Getter private Shape2D shape;

    @Getter private float x;
    @Getter private float y;
    @Getter private float shapeOffsetX = 0;
    @Getter private float shapeOffsetY = 0;

    /**
     * This method is called at the beginning of the step, after the entity has been placed in the scene.
     * @param game
     * @param scene
     */
    public void onCreate(G game, S scene) {
        alarms = new AlarmSystem();
    }

    /**
     * Call this method to destroy this entity. Entities are removed from the scene at the end of the game loop.
     */
    public final void destroy() {
        destroyed = true;
        onDestroy(game, scene);
        postDestroy(scene);
    }

    /**
     * Sets the depth of the entity. Entities are drawn from the entity with the highest depth to the lowest. The entity
     * with the highest depth will appear below other entities. The step method will also be called in this order.
     * @param depth
     */
    public void setDepth(float depth) {
        if(depth != this.depth) {
            depthChanged = true;
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

    /**
     * Sets the x position of the entity.
     * @param x
     */
    public void setX(float x) {
        beforeMove(getScene());
        this.x = x;
        afterMove(getScene());
    }

    /**
     * Sets the y position of the entity.
     * @param y
     */
    public void setY(float y) {
        beforeMove(getScene());
        this.y = y;
        afterMove(getScene());
    }

    /**
     * Sets the position of the entity. This method is slightly faster then setting both x and y independently.
     * @param x
     * @param y
     */
    public void setPos(float x, float y) {
        beforeMove(getScene());
        this.x = x;
        this.y = y;
        afterMove(getScene());
    }

    /**
     * Sets the collision mask of this entity.
     * @param shape
     */
    public void setShape(Shape2D shape) {
        beforeMove(getScene());
        this.shape = shape;
        afterMove(getScene());
    }

    /**
     * Sets the y offset of the collision mask from the entity's position.
     * @param shapeOffsetX
     */
    public void setShapeOffsetX(float shapeOffsetX) {
        beforeMove(getScene());
        this.shapeOffsetX = shapeOffsetX;
        afterMove(getScene());
    }

    /**
     * Sets the y offset of the collision mask from the entity's position.
     * @param shapeOffsetY
     */
    public void setShapeOffsetY(float shapeOffsetY) {
        beforeMove(getScene());
        this.shapeOffsetY = shapeOffsetY;
        afterMove(getScene());
    }

    /**
     * Creates a rectangular collision mask based off of the currently assigned sprite.
     */
    public void createMaskFromSprite() {
        Frame firstFrame = sprite.getFrames().get(0);
        Rect rect = new Rect(x, y, firstFrame.getImage().getWidth(), firstFrame.getImage().getHeight());
        shapeOffsetX = firstFrame.getOffsetX();
        shapeOffsetY = firstFrame.getOffsetY();
        setShape(rect);
    }

    //Broadphase management methods
    //Broadphase Methods
    public final void postCreate(S scene) {
        if(shape != null) {
            shape.setPosition(x - shapeOffsetX, y - shapeOffsetY);
        }
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemAddEntity(this);
        }
    }

    public final void postDestroy(S scene) {
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemRemoveEntity(this);
        }
    }

    public final void beforeMove(S scene) {
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemBeforeMove(this);
        }
    }

    public final void afterMove(S scene) {
        if(shape != null) {
            shape.setPosition(x - shapeOffsetX, y - shapeOffsetY);
        }
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemAfterMove(this);
        }
    }

    public void beginScene(G game, S scene) {}
    public void endScene(G game, S scene) {}

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @return
     */
    public boolean collidesWith(Class<? extends Entity> clazz) {
        return getScene().getCollisionSystem2D().collidesWith(shape, clazz);
    }

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @return
     */
    public <C extends Entity> C getCollision(Class<C> clazz) {
        return getScene().getCollisionSystem2D().getCollision(shape, clazz);
    }

    public <C extends Entity> C getCollisionAt(Class<C> clazz, float x, float y) {
        shape.setPosition(x - shapeOffsetX, y - shapeOffsetY);
        C entity = getScene().getCollisionSystem2D().getCollision(shape, clazz);
        shape.setPosition(this.x - shapeOffsetX, this.y - shapeOffsetY);
        return entity;
    }

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends Entity> List<C> getCollisions(Class<C> clazz) {
        return getScene().getCollisionSystem2D().getCollisions(shape, clazz);
    }

    public <C extends Entity> List<C> getCollisionsAt(Class<C> clazz, float x, float y) {
        shape.setPosition(x - shapeOffsetX, y - shapeOffsetY);
        List<C> entities = getScene().getCollisionSystem2D().getCollisions(shape, clazz);
        shape.setPosition(this.x - shapeOffsetX, this.y - shapeOffsetY);
        return entities;
    }

    /**
     * Checks for a potential collision assuming that this entity is moved to a certain position.
     * @param clazz
     * @param x
     * @param y
     * @return
     */
    public boolean placeMeeting(Class clazz, float x, float y) {
        shape.setPosition(x - shapeOffsetX, y - shapeOffsetY);
        boolean returnValue = collidesWith(clazz);
        shape.setPosition(this.x - shapeOffsetX, this.y - shapeOffsetY);
        return returnValue;
    }

    /**
     * See the placeMeeting method.
     * @param clazz
     * @param x
     * @param y
     * @return
     */
    public boolean placeFree(Class clazz, float x, float y) {
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
        return MathUtils.distance(x, y, other.getX(), other.getY());
    }

    /**
     * Gets the distance^2 between this entity and another entity.
     * @param other
     * @return
     */
    public float distanceSqr(Entity other) {
        return MathUtils.distanceSqr(x, y, other.getX(), other.getY());
    }
}
