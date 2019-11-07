package net.loganford.noideaengine.entity;

import lombok.Getter;
import net.loganford.noideaengine.components.Component;

import java.util.ArrayList;
import java.util.List;

public class EntityComponentStore extends EntityStore<EntityComponentStore.EntityComponent> {

    private List<Class<? extends Component>> componentList;

    public EntityComponentStore(List<Class<? extends Component>> componentList) {
        this.componentList = componentList;
    }


    @Override
    protected Entity unwrap(EntityComponent item) {
        return item.getEntity();
    }

    @Override
    protected EntityComponent wrap(Entity entity) {
        List<Component> componentsForEntity = new ArrayList<>();
        for(Class<? extends Component> componentClass : componentList) {
            componentsForEntity.add(entity.getComponent(componentClass));
        }

        return new EntityComponent(entity, componentsForEntity);
    }

    public static class EntityComponent {
        @Getter private Entity entity;
        @Getter private List<Component> components;

        public EntityComponent(Entity entity, List<Component> components) {
            this.entity = entity;
            this.components = components;
        }
    }
}
