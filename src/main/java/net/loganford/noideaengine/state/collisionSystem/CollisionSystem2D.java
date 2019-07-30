package net.loganford.noideaengine.state.collisionSystem;

import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.entity.Entity;

import java.util.List;

public interface CollisionSystem2D {
    void init();
    void destroy();

    void collisionSystemAddEntity(Entity entity);
    void collisionSystemBeforeMove(Entity entity);
    void collisionSystemAfterMove(Entity entity);
    void collisionSystemRemoveEntity(Entity entity);

    boolean collidesWith(Shape2D shape, Class<? extends Entity> clazz);
    <C extends Entity> C getCollision(Shape2D shape, Class<C> clazz);
    <C extends Entity> List<C> getCollisions(Shape2D shape, Class<C> clazz);
}
