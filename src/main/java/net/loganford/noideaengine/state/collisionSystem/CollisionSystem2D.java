package net.loganford.noideaengine.state.collisionSystem;

import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.entity.Entity2D;

import java.util.List;

public interface CollisionSystem2D {
    void init();
    void destroy();

    void collisionSystemAddEntity(Entity2D entity);
    void collisionSystemBeforeMove(Entity2D entity);
    void collisionSystemAfterMove(Entity2D entity);
    void collisionSystemRemoveEntity(Entity2D entity);

    boolean collidesWith(Shape2D shape, Class<? extends Entity2D> clazz);
    Entity2D getCollision(Shape2D shape, Class<? extends Entity2D> clazz);
    <C extends Entity2D> List<C> getCollisions(Shape2D shape, Class<C> clazz);
}
