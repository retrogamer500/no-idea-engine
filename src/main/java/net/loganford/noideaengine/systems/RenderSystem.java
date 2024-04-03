package net.loganford.noideaengine.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.components.RenderComponent;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;

import java.util.List;

@RegisterComponent(RenderComponent.class)
public class RenderSystem extends ProcessEntitySystem {
    public RenderSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {

    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {
        entity.render(game, scene, renderer);
    }
}
