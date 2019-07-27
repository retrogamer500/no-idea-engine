package net.loganford.noideaengine.state.entity;

import java.util.*;
import java.util.function.Consumer;

public class EntityStore implements Iterable {
    private ArrayList<AbstractEntity> entities;
    private EntityTypeCache typeCache;

    public EntityStore() {
        entities = new ArrayList<>();
        typeCache = new EntityTypeCache();
    }

    public void add(AbstractEntity entity) {
        int index = Collections.binarySearch(entities, entity, (o1, o2) -> Float.compare(o2.getDepth(), o1.getDepth()));
        if(index < 0) {
            index = -(index + 1);
        }
        entities.add(index, entity);
        typeCache.add(entity);
    }

    public void removeDestroyed() {
        for(int i = entities.size() - 1; i >= 0; i--) {
            AbstractEntity entity = entities.get(i);
            if(entity.isDestroyed()) {
                entities.remove(i);
                typeCache.remove(entity);
            }
        }
    }

    public <C extends AbstractEntity> Set<C> getByClass(Class<C> clazz) {
        return typeCache.get(clazz);
    }

    public int size() {
        return entities.size();
    }

    public AbstractEntity get(int index) {
        return entities.get(index);
    }

    @Override
    public Iterator iterator() {
        return entities.iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEach(Consumer action) {
        entities.forEach(action);
    }

    @Override
    public Spliterator spliterator() {
        return entities.spliterator();
    }
}
