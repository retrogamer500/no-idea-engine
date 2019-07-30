package net.loganford.noideaengine.state.collisionSystem;

import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class NaiveBroadphase implements CollisionSystem2D {
    private List<Entity> entities;

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
    public void collisionSystemAddEntity(Entity entity) {
        if(entity.getShape() != null) {
            entities.add(entity);
        }
    }

    @Override
    public void collisionSystemBeforeMove(Entity entity) {
        //Do nothing
    }

    @Override
    public void collisionSystemAfterMove(Entity entity) {
        //Do nothing
    }

    @Override
    public void collisionSystemRemoveEntity(Entity entity) {
        entities.remove(entity);
    }

    @Override
    public boolean collidesWith(Shape2D shape, Class<? extends Entity> clazz) {
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
    public <C extends Entity> C getCollision(Shape2D shape, Class<C> clazz) {
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
    public <C extends Entity> List<C> getCollisions(Shape2D shape, Class<C> clazz) {
        List<C> results = new ArrayList<>();
        for(Entity entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass())) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    results.add((C)entity);
                }
            }
        }
        return results;
    }
}
