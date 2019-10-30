package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.shape.AbstractCompoundShape;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.utils.annotations.Argument;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public class NaiveCollisionSystem extends CollisionSystem {
    private static List LIST = new ArrayList<>();
    private List<Entity> entities;

    public NaiveCollisionSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

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
    public boolean collidesWith(Shape shape, Class<?> clazz) {
        for(Entity entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass()) && entity.getShape() != null) {
                if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getCollision(Shape shape, Class<C> clazz) {
        for(Entity entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass()) && entity.getShape() != null) {
                if(entity.getShape() instanceof AbstractCompoundShape) {
                    for (Shape otherShape : (AbstractCompoundShape) entity.getShape()) {
                        if (entity.getShape() != otherShape && entity.getShape().collidesWith(otherShape)) {
                            return (C)entity;
                        }
                    }
                }
                else {
                    if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                        return (C)entity;
                    }
                }

            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> List<C> getCollisions(Shape shape, Class<C> clazz) {
        LIST.clear();
        getCollisions(LIST, shape, clazz);
        return LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> void getCollisions(List<C> list, Shape shape, Class<C> clazz) {
        for(Entity entity : entities) {
            if(clazz.isAssignableFrom(entity.getClass()) && entity.getShape() != null) {
                if(entity.getShape() instanceof AbstractCompoundShape) {
                    for (Shape otherShape : (AbstractCompoundShape) entity.getShape()) {
                        if (entity.getShape() != otherShape && entity.getShape().collidesWith(otherShape)) {
                            list.add((C)entity);
                        }
                    }
                }
                else {
                    if (entity.getShape() != shape && entity.getShape().collidesWith(shape)) {
                        list.add((C)entity);
                    }
                }

            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> void sweep(SweepResult result, Shape shape, Vector3fc velocity, Class<E> clazz) {
        result.clear();
        result.getPosition().set(shape.getPosition());
        result.getVelocity().set(velocity);

        for(Entity entity : entities) {
            if (clazz.isAssignableFrom(entity.getClass()) && entity.getShape() != null) {
                if(entity.getShape() instanceof AbstractCompoundShape) {
                    for (Shape otherShape : (AbstractCompoundShape) entity.getShape()) {
                        SweepResult otherResult = shape.sweep(velocity, otherShape);
                        otherResult.setEntity(entity);

                        if(otherResult.getDistance() < result.getDistance()) {
                            result.set(otherResult);
                        }
                    }
                }
                else {
                    SweepResult otherResult = shape.sweep(velocity, entity.getShape());
                    otherResult.setEntity(entity);

                    if(otherResult.getDistance() < result.getDistance()) {
                        result.set(otherResult);
                    }
                }
            }
        }
    }
}
