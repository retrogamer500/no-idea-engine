package net.loganford.noideaengine.state.entity;

public interface EntityAction<T extends Entity> {
    void doAction(T entity);
}
