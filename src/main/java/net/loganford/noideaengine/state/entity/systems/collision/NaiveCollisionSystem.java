package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class NaiveCollisionSystem extends CollisionSystem {
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
