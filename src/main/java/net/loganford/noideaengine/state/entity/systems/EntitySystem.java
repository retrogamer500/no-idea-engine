package net.loganford.noideaengine.state.entity.systems;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.Component;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;
import net.loganford.noideaengine.state.entity.signals.ComponentRemovedSignal;
import net.loganford.noideaengine.state.entity.signals.DestructionSignal;
import net.loganford.noideaengine.utils.messaging.Listener;
import net.loganford.noideaengine.utils.messaging.Signal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public abstract class EntitySystem implements Listener<Entity> {
    @Getter private SystemPriorityChangedSignal systemPriorityChangedSignal = new SystemPriorityChangedSignal();
    @Getter private List<Class<? extends Component>> componentList;
    @Getter private float priority = 0;

    public EntitySystem(Game game, Scene scene, String[] args) {
        componentList = new ArrayList<>();

        Class clazz = getClass();
        while(clazz != null) {
            for (Annotation annotation : clazz.getAnnotationsByType(RegisterComponent.class)) {
                Class<? extends Component> componentClazz = ((RegisterComponent)annotation).value();
                componentList.add(componentClazz);
            }

            clazz = clazz.getSuperclass();
        }
    }

    public void addEntity(Entity entity) {
        entity.getComponentRemovedSignal().subscribe(this);
        entity.getDestructionSignal().subscribe(this);
    }
    public void removeEntity(Entity entity) {
        entity.getComponentRemovedSignal().unsubscribe(this);
        entity.getDestructionSignal().unsubscribe(this);
    }

    public abstract void step(Game game, Scene scene, float delta);
    public abstract void render(Game game, Scene scene, Renderer renderer);

    public boolean entityBelongs(Entity entity) {
        for(Class<? extends Component> clazz : getComponentList()) {
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
        else if(signal instanceof DestructionSignal) {
            removeEntity(entity);
        }
    }

    public void setPriority(float priority) {
        this.priority = priority;
        systemPriorityChangedSignal.dispatch(this);
    }
}
