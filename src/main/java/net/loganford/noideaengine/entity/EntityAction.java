package net.loganford.noideaengine.entity;

/**
 * Represents an action to perform on an entity
 */
public interface EntityAction<T extends Entity> {
    /**
     * The action to perform on the entity
     * @param entity the entity
     */
    void doAction(T entity);
}
