package net.loganford.noideaengine.state.entity.systems;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.resources.RequireGroup;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.components.Component;
import net.loganford.noideaengine.state.entity.components.RegisterComponent;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntitySystem {
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

    public abstract void addEntity(Entity entity);
    public abstract void removeEntity(Entity entity);
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
}
