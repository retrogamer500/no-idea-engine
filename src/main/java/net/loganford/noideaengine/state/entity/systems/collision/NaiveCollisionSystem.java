package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.entity.Entity;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public class NaiveCollisionSystem extends CollisionSystem {
    private static List LIST = new ArrayList<>();
    private List<Entity> entities;

    public NaiveCollisionSystem() {
        super();
        entities = new ArrayList<>();
    }

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);
        entities.add(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);
        entities.remove(entity);
    }

    @Override
    public boolean collidesWith(Shape shape, Class<? extends Entity> clazz) {
        for(Entity entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Entity> C getCollision(Shape shape, Class<C> clazz) {
        for(Entity entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    return (C)entity;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Entity> List<C> getCollisions(Shape shape, Class<C> clazz) {
        LIST.clear();
        getCollisions(LIST, shape, clazz);
        return LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Entity> void getCollisions(List<C> list, Shape shape, Class<C> clazz) {
        for(Entity entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    list.add((C)entity);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> void sweepImpl(SweepResult result, Shape shape, Vector3fc velocity, Class<E> clazz) {
        for(Entity entity : entities) {
            if (clazz.isAssignableFrom(entity.getClass())) {
                SweepResult otherResult = shape.sweep(velocity, entity.getShape());
                otherResult.setEntity(entity);

                if(otherResult.getDistance() < result.getDistance()) {
                    result.set(otherResult);
                }
            }
        }
    }
}
