package net.loganford.noideaengine.state.entity;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Frame;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.Sprite;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.math.MathUtils;

import java.util.List;

public abstract class Entity2D<G extends Game, S extends Scene<G>> extends AbstractEntity<G, S> {
    @Getter @Setter private Sprite sprite;
    @Getter private Shape2D shape;

    @Getter private float x;
    @Getter private float y;
    @Getter private float shapeOffsetX = 0;
    @Getter private float shapeOffsetY = 0;

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

    @Override
    public void step(G game, S scene, float delta) {
        if(sprite != null) {
            sprite.step(delta);
        }
    }

    @Override
    public void render(G game, S scene, Renderer renderer) {
        if(sprite != null) {
            sprite.render(renderer, getX(), getY());
        }
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
     * Creates a rectangular collision mask based off of the currently assigned sprite.
     */
    public void createMaskFromSprite() {
        Frame firstFrame = sprite.getFrames().get(0);
        Rect rect = new Rect(x, y, firstFrame.getImage().getWidth(), firstFrame.getImage().getHeight());
        shapeOffsetX = firstFrame.getOffsetX();
        shapeOffsetY = firstFrame.getOffsetY();
        setShape(rect);
    }

    //Broadphase Methods
    @Override
    public final void postCreate(S scene) {
        if(shape != null) {
            shape.setPosition(x - shapeOffsetX, y - shapeOffsetY);
        }
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemAddEntity(this);
        }
    }
    @Override
    public final void postDestroy(S scene) {
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemRemoveEntity(this);
        }
    }
    @Override
    public final void beforeMove(S scene) {
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemBeforeMove(this);
        }
    }
    @Override
    public final void afterMove(S scene) {
        if(shape != null) {
            shape.setPosition(x - shapeOffsetX, y - shapeOffsetY);
        }
        if(scene != null) {
            scene.getCollisionSystem2D().collisionSystemAfterMove(this);
        }
    }


    //Collision functionality

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @return
     */
    public boolean collidesWith(Class<? extends Entity2D> clazz) {
        return getScene().getCollisionSystem2D().collidesWith(shape, clazz);
    }

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @return
     */
    public Entity2D getCollision(Class<? extends Entity2D> clazz) {
        return getScene().getCollisionSystem2D().getCollision(shape, clazz);
    }

    /**
     * Checks for collisions with entities.
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends Entity2D> List<C> getCollisions(Class<C> clazz) {
        return getScene().getCollisionSystem2D().getCollisions(shape, clazz);
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
    public <C extends Entity2D> C nearest(Class<C> clazz) {
        return getScene().nearest(clazz, getX(), getY());
    }

    /**
     * Gets the furthest entity to this entity.
     * @param clazz
     * @param <C>
     * @return
     */
    public <C extends Entity2D> C furthest(Class<C> clazz) {
        return getScene().furthest(clazz, getX(), getY());
    }

    /**
     * Gets the N closest entities to this entity, sorted nearest to furthest.
     * @param clazz
     * @param count
     * @param <C>
     * @return
     */
    public <C extends Entity2D> List<EntityDistancePair<C>> nearest(Class<C> clazz, int count) {
        return getScene().nearest(clazz, getX(), getY(), count);
    }

    /**
     * Gets the distance between this entity and another entity.
     * @param other
     * @return
     */
    public float distance(Entity2D other) {
        return MathUtils.distance(x, y, other.getX(), other.getY());
    }

    /**
     * Gets the distance^2 between this entity and another entity.
     * @param other
     * @return
     */
    public float distanceSqr(Entity2D other) {
        return MathUtils.distanceSqr(x, y, other.getX(), other.getY());
    }
}
