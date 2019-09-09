package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.AbstractPositionComponent;
import net.loganford.noideaengine.state.entity.components.BasicCollisionComponent;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import net.loganford.noideaengine.state.entity.systems.EntitySystem;

import java.util.List;

@RegisterComponent(BasicCollisionComponent.class)
@RegisterComponent(AbstractPositionComponent.class)
public abstract class CollisionSystem extends EntitySystem {
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
}
