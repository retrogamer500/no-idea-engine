package net.loganford.noideaengine.state.entity.systems;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.resources.RequireGroup;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.Component;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import net.loganford.noideaengine.state.entity.signals.ComponentRemovedSignal;
import net.loganford.noideaengine.utils.messaging.Listener;
import net.loganford.noideaengine.utils.messaging.Signal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntitySystem implements Listener<Entity> {
    @Getter private List<Class<Component>> componentList;

    public AbstractEntitySystem() {
        componentList = new ArrayList<>();

        //Load components
        for (Annotation annotation : getClass().getAnnotations()) {
            if(annotation instanceof RequireGroup.List) {
                RegisterComponent.List requireComponentList = (RegisterComponent.List) annotation;
                for(RegisterComponent registerComponent: requireComponentList.value()) {
                    componentList.add(registerComponent.clazz());
                }
            }
        }
    }

    public void addEntity(Entity entity) {
        entity.getComponentRemovedSignal().subscribe(this);
    }
    public void removeEntity(Entity entity) {
        entity.getComponentRemovedSignal().unsubscribe(this);
    }

    public abstract void step(Game game, Scene scene, float delta);
    public abstract void render(Game game, Scene scene, Renderer renderer);

    public boolean entityBelongs(Entity entity) {
        for(Class<Component> clazz : getComponentList()) {
            if(entity.getComponents().get(clazz) == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void receive(Signal<Entity> signal, Entity entity) {
        if(signal instanceof ComponentRemovedSignal) {
            if(!entityBelongs(entity)) {
                removeEntity(entity);
            }
        }
    }
}
