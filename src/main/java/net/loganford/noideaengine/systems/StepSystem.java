package net.loganford.noideaengine.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.components.StepComponent;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.entity.EntityComponentStore;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;

import java.util.List;

@RegisterComponent(StepComponent.class)
public class StepSystem extends ProcessEntitySystem {
    public StepSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);
    }

    @Override
    public void step(Game game, Scene scene, float delta) {
        if (isSorted() && isResort()) {
            getEntities().resort();
            setResort(false);
        }

        EntityComponentStore.EntityComponent entity;
        for(entity = getFirstEntity(); entity != null; entity = getNextEntity()) {
            entity.getEntity().beforeStep(game, scene, delta);
        }

        for(entity = getFirstEntity(); entity != null; entity = getNextEntity()) {
            entity.getEntity().step(game, scene, delta);
        }

        for(entity = getFirstEntity(); entity != null; entity = getNextEntity()) {
            entity.getEntity().afterStep(game, scene, delta);
        }
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {

    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }
}
