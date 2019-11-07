package net.loganford.noideaengine.entity;

public class SimpleEntityStore extends EntityStore<Entity> {

    @Override
    protected Entity unwrap(Entity item) {
        return item;
    }

    @Override
    protected Entity wrap(Entity entity) {
        return entity;
    }
}
