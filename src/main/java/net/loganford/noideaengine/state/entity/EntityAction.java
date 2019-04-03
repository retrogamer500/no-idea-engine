package net.loganford.noideaengine.state.entity;

public interface EntityAction<T extends AbstractEntity> {
    void doAction(T entity);
}
