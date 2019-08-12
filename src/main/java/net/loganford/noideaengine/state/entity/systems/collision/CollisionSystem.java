package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.CollisionComponent;
import net.loganford.noideaengine.state.entity.components.PositionComponent;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import net.loganford.noideaengine.state.entity.systems.AbstractEntitySystem;

import java.util.List;

@RegisterComponent(CollisionComponent.class)
@RegisterComponent(PositionComponent.class)
public abstract class CollisionSystem extends AbstractEntitySystem {
    public abstract boolean collidesWith(Shape2D shape, Class<? extends Entity> clazz);
    public abstract <C extends Entity> C getCollision(Shape2D shape, Class<C> clazz);
    public abstract <C extends Entity> List<C> getCollisions(Shape2D shape, Class<C> clazz);

    @Override
    public void step(Game game, Scene scene, float delta) {

    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {

    }
}
