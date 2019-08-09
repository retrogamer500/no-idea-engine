package net.loganford.noideaengine.state.entity.systems;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.EntityStore;
import net.loganford.noideaengine.state.entity.signals.ComponentRemovedSignal;
import net.loganford.noideaengine.state.entity.signals.DepthChangedSignal;
import net.loganford.noideaengine.state.entity.signals.DestructionSignal;
import net.loganford.noideaengine.utils.messaging.Listener;
import net.loganford.noideaengine.utils.messaging.Signal;

public abstract class SortedEntityListSystem extends AbstractEntitySystem implements Listener<Entity> {

    private boolean resort = false;

    @Getter private EntityStore entities;

    public SortedEntityListSystem() {
        super();
        entities = new EntityStore();
    }

    @Override
    public void addEntity(Entity entity) {
        entity.getDestructionSignal().subscribe(this);
        entity.getDepthChangedSignal().subscribe(this);
        entity.getComponentRemovedSignal().subscribe(this);
    }

    @Override
    public void removeEntity(Entity entity) {
        entity.getDepthChangedSignal().unsubscribe(this);
        entities.remove(entity);
    }

    @Override
    public void step(Game game, Scene scene, float delta) {
        if(resort) {
            entities.resort();
        }
        stepEntities(entities, game, scene, delta);

        resort = false;
    }

    abstract void stepEntities(EntityStore entities, Game game, Scene scene, float delta);

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {

    }

    @Override
    public void receive(Signal<Entity> signal, Entity entity) {
        if(signal instanceof DestructionSignal) {
            removeEntity(entity);
        }
        else if(signal instanceof DepthChangedSignal) {
            resort = true;
        }
        else if(signal instanceof ComponentRemovedSignal) {
            if(!entityBelongs(entity)) {
                removeEntity(entity);
            }
        }
    }
}
