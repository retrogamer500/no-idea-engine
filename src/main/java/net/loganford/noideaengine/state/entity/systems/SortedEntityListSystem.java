package net.loganford.noideaengine.state.entity.systems;

import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.EntityStore;
import net.loganford.noideaengine.state.entity.signals.DepthChangedSignal;
import net.loganford.noideaengine.state.entity.signals.DestructionSignal;
import net.loganford.noideaengine.utils.messaging.Signal;

public abstract class SortedEntityListSystem extends AbstractEntitySystem {

    private boolean resort = false;

    private EntityStore entities;

    public SortedEntityListSystem() {
        super();
        entities = new EntityStore();
    }

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);

        entity.getDestructionSignal().subscribe(this);
        entity.getDepthChangedSignal().subscribe(this);
    }

    @Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);

        entity.getDestructionSignal().unsubscribe(this);
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
        renderEntities(entities, game, scene, renderer);
    }

    abstract void renderEntities(EntityStore entities, Game game, Scene scene, Renderer renderer);

    @Override
    public void receive(Signal<Entity> signal, Entity entity) {
        super.receive(signal, entity);

        if(signal instanceof DepthChangedSignal) {
            resort = true;
        }
    }
}
