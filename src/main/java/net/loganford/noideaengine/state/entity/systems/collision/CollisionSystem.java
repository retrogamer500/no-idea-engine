package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.AbstractPositionComponent;
import net.loganford.noideaengine.state.entity.components.BasicCollisionComponent;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import net.loganford.noideaengine.state.entity.systems.EntitySystem;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

@RegisterComponent(BasicCollisionComponent.class)
@RegisterComponent(AbstractPositionComponent.class)
public abstract class CollisionSystem extends EntitySystem {
    private static SweepResult SWEEP_RESULT = new SweepResult();
    private static Vector3f V3F = new Vector3f();

    @Scriptable
    public abstract boolean collidesWith(Shape shape, Class<? extends Entity> clazz);
    @Scriptable
    public abstract <C extends Entity> C getCollision(Shape shape, Class<C> clazz);
    @Scriptable
    public abstract <C extends Entity> List<C> getCollisions(Shape shape, Class<C> clazz);

    public abstract <C extends Entity> void getCollisions(List<C> list, Shape shape, Class<C> clazz);

    @Override
    public void step(Game game, Scene scene, float delta) {

    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {

    }

    public abstract <E extends Entity> void sweep(SweepResult result, Shape shape, Vector3fc velocity, Class<E> clazz);

    @SuppressWarnings("unchecked")
    public <E extends Entity> void sweep(SweepResult<E> result, Shape shape, Vector2fc velocity, Class<E> clazz) {
        sweep(result, shape, V3F.set(velocity.x(), velocity.y(), 0), clazz);
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> void sweep(SweepResult<E> result, Shape shape, float vx, float vy, Class<E> clazz) {
        sweep(result, shape, V3F.set(vx, vy, 0), clazz);
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> void sweep(SweepResult<E> result, Shape shape, float vx, float vy, float vz, Class<E> clazz) {
        sweep(result, shape, V3F.set(vx, vy, vz), clazz);
    }


    @SuppressWarnings("unchecked")
    public <E extends Entity> SweepResult<E> sweep(Shape shape, Vector3fc velocity, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, velocity, clazz);
        return SWEEP_RESULT;
    }

    @SuppressWarnings("unchecked")
    public <E extends Entity> SweepResult<E> sweep(Shape shape, Vector2fc velocity, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, V3F.set(velocity.x(), velocity.y(), 0f), clazz);
        return SWEEP_RESULT;
    }

    @SuppressWarnings("unchecked")
    @Scriptable
    public <E extends Entity> SweepResult<E> sweep(Shape shape, float vx, float vy, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, V3F.set(vx, vy, 0), clazz);
        return SWEEP_RESULT;
    }

    @SuppressWarnings("unchecked")
    @Scriptable
    public <E extends Entity> SweepResult<E> sweep(Shape shape, float vx, float vy, float vz, Class<E> clazz) {
        sweep(SWEEP_RESULT, shape, V3F.set(vx, vy, vz), clazz);
        return SWEEP_RESULT;
    }
}