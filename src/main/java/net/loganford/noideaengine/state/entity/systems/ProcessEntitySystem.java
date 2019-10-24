package net.loganford.noideaengine.state.entity.systems;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.EntityComponentStore;
import net.loganford.noideaengine.state.entity.components.Component;
import net.loganford.noideaengine.utils.messaging.Listener;
import net.loganford.noideaengine.utils.messaging.Signal;

import java.util.List;

public abstract class ProcessEntitySystem extends EntitySystem {

    @Getter @Setter private boolean sorted = true;
    private int currentEntity = 0;
    private boolean resort = false;

    private EntityComponentStore entities;
    private EntityDepthChangedListener entityDepthChangedListener = new EntityDepthChangedListener();

    public ProcessEntitySystem(Game game, Scene scene, String[] args) {
        super(game, scene, args);

        entities = new EntityComponentStore(getComponentList());
    }

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);

        int index = entities.add(entity);

        if(index < currentEntity) {
            currentEntity++;
        }

        entity.getDepthChangedSignal().subscribe(entityDepthChangedListener);
    }

    @Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);

        entity.getDepthChangedSignal().unsubscribe(entityDepthChangedListener);

        int index = entities.remove(entity);
        if(index <= currentEntity) {
            currentEntity--;
        }
    }

    @Override
    public void step(Game game, Scene scene, float delta) {
        if (sorted && resort) {
            entities.resort();
            resort = false;
        }

        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            Entity entity = entities.get(currentEntity).getEntity();
            List<Component> components = entities.get(currentEntity).getComponents();
            if(!entity.isDestroyed()) {
                processEntity(entity, components, game, scene, delta);
            }
        }
    }

    abstract void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta);

    public int getComponentLocation(Class<? extends Component> component) {
        int index = -1;

        for (int i = 0; i < getComponentList().size(); i++) {
            Class<? extends Component> registeredComponent = getComponentList().get(i);
            if (registeredComponent.isAssignableFrom(registeredComponent)) {
                index = i;
            }
        }

        return index;
    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {
        for(currentEntity = 0; currentEntity < entities.size(); currentEntity++) {
            Entity entity = entities.get(currentEntity).getEntity();
            List<Component> components = entities.get(currentEntity).getComponents();
            if(!entity.isDestroyed()) {
                renderEntity(entity, components, game, scene, renderer);
            }
        }
    }

    abstract void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer);


    private class EntityDepthChangedListener implements Listener<Entity> {
        @Override
        public void receive(Signal<Entity> signal, Entity entity) {
            resort = true;
        }
    }
}
