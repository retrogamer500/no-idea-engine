package net.loganford.noideaengine.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.math.MathUtils;

import java.util.Comparator;
import java.util.List;

public class TransparentRenderSystem extends ProcessEntitySystem {
    public TransparentRenderSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);
        setSorted(false);
        setPriority(10);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {

    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {
        getEntities().resort(Comparator.comparingDouble(ec -> -MathUtils.distance(renderer.getCamera().getPosition(), ec.getEntity().getPos())));

        super.render(game, scene, renderer);
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {
        entity.render(game, scene, renderer);
    }
}
