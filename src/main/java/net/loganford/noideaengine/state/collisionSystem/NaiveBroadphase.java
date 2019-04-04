package net.loganford.noideaengine.state.collisionSystem;

import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.entity.Entity2D;

import java.util.ArrayList;
import java.util.List;

public class NaiveBroadphase implements CollisionSystem2D {
    private List<Entity2D> entities;

    public NaiveBroadphase() {

    }

    @Override
    public void init() {
        entities = new ArrayList<>();
    }

    @Override
    public void destroy() {
        entities = null;
    }

    @Override
    public void collisionSystemAddEntity(Entity2D entity) {
        if(entity.getShape() != null) {
            entities.add(entity);
        }
    }

    @Override
    public void collisionSystemBeforeMove(Entity2D entity) {
        //Do nothing
    }

    @Override
    public void collisionSystemAfterMove(Entity2D entity) {
        //Do nothing
    }

    @Override
    public void collisionSystemRemoveEntity(Entity2D entity) {
        entities.remove(entity);
    }

    @Override
    public boolean collidesWith(Shape2D shape, Class<? extends Entity2D> clazz) {
        for(Entity2D entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public <C extends Entity2D> C getCollision(Shape2D shape, Class<C> clazz) {
        for(Entity2D entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    return (C)entity;
                }
            }
        }
        return null;
    }

    @Override
    public <C extends Entity2D> List<C> getCollisions(Shape2D shape, Class<C> clazz) {
        List<C> results = new ArrayList<>();
        for(Entity2D entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    results.add((C)entity);
                }
            }
        }
        return results;
    }
}
