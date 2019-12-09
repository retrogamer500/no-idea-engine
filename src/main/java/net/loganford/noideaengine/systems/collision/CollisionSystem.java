package net.loganford.noideaengine.systems.collision;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.shape.*;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.components.collision.AbstractCollisionComponent;
import net.loganford.noideaengine.components.AbstractPositionComponent;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import net.loganford.noideaengine.systems.EntitySystem;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

@RegisterComponent(AbstractCollisionComponent.class)
@RegisterComponent(AbstractPositionComponent.class)
public abstract class CollisionSystem extends EntitySystem {
    private static SweepResult SWEEP_RESULT = new SweepResult();
    private static Line LINE = new Line(0, 0, 0, 0);
    private static Cuboid CUBE = new Cuboid(0, 0, 0, 1, 1, 1);
    private static Vector3f V3F = new Vector3f();

    public CollisionSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);
    }

    @Scriptable
    public abstract boolean collidesWith(Shape shape, Class<?> clazz);
    @Scriptable
    public abstract <C> C getCollision(Shape shape, Class<C> clazz);
    @Scriptable
    public abstract <C> List<C> getCollisions(Shape shape, Class<C> clazz);

    public abstract <C> void getCollisions(List<C> list, Shape shape, Class<C> clazz);

    @Override
    public void step(Game game, Scene scene, float delta) {

    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {

    }

    public abstract <E> void sweep(SweepResult result, Shape shape, Vector3fc velocity, Class<E> clazz);

    @SuppressWarnings("unchecked")
    public <E> void sweep(SweepResult<E> result, Shape shape, Vector2fc velocity, Class<E> clazz) {
        sweep(result, shape, V3F.set(velocity.x(), velocity.y(), 0), clazz);
    }

    @SuppressWarnings("unchecked")
    public <E> void sweep(SweepResult<E> result, Shape shape, float vx, float vy, Class<E> clazz) {
        sweep(result, shape, V3F.set(vx, vy, 0), clazz);
    }

    @SuppressWarnings("unchecked")
    public <E> void sweep(SweepResult<E> result, Shape shape, float vx, float vy, float vz, Class<E> clazz) {
        sweep(result, shape, V3F.set(vx, vy, vz), clazz);
    }


    @SuppressWarnings("unchecked")
    public <E> SweepResult<E> sweep(Shape shape, Vector3fc velocity, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, velocity, clazz);
        return SWEEP_RESULT;
    }

    @SuppressWarnings("unchecked")
    public <E> SweepResult<E> sweep(Shape shape, Vector2fc velocity, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, V3F.set(velocity.x(), velocity.y(), 0f), clazz);
        return SWEEP_RESULT;
    }

    @SuppressWarnings("unchecked")
    @Scriptable
    public <E> SweepResult<E> sweep(Shape shape, float vx, float vy, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, V3F.set(vx, vy, 0), clazz);
        return SWEEP_RESULT;
    }

    @SuppressWarnings("unchecked")
    @Scriptable
    public <E> SweepResult<E> sweep(Shape shape, float vx, float vy, float vz, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, V3F.set(vx, vy, vz), clazz);
        return SWEEP_RESULT;
    }

    protected final Shape getSweepMask(Shape shape, Vector3fc velocity) {
        if(shape instanceof Point) {
            Point point = (Point) shape;
            LINE.setX1(point.getX());
            LINE.setY1(point.getY());
            LINE.setZ1(point.getZ());
            LINE.setX2(point.getX() + velocity.x());
            LINE.setY2(point.getY() + velocity.y());
            LINE.setZ2(point.getZ() + velocity.z());
            return LINE;
        }
        else {
            shape.getBoundingBox(CUBE);

            if(velocity.x() < 0) {
                CUBE.setX(CUBE.getX() + velocity.x());
            }
            CUBE.setWidth(CUBE.getWidth() + Math.abs(velocity.x()));

            if(velocity.y() < 0) {
                CUBE.setY(CUBE.getY() + velocity.y());
            }
            CUBE.setHeight(CUBE.getHeight() + Math.abs(velocity.y()));

            if(velocity.z() < 0) {
                CUBE.setZ(CUBE.getZ() + velocity.z());
            }
            CUBE.setDepth(CUBE.getDepth() + Math.abs(velocity.z()));

            return CUBE;
        }
    }
}
