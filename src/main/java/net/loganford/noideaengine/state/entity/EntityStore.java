package net.loganford.noideaengine.state.entity;

import java.util.*;
import java.util.function.Consumer;

public class EntityStore implements Iterable<AbstractEntity> {
    private ArrayList<AbstractEntity> entities;
    private EntityTypeCache typeCache;
    private ArrayList<AbstractEntity> entitiesToResort;

    public EntityStore() {
        entities = new ArrayList<>();
        typeCache = new EntityTypeCache();
        entitiesToResort = new ArrayList<>();
    }

    public int add(AbstractEntity entity) {
        int index = Collections.binarySearch(entities, entity, (o1, o2) -> Float.compare(o2.getDepth(), o1.getDepth()));
        if(index < 0) {
            index = -(index + 1);
        }
        entities.add(index, entity);
        typeCache.add(entity);
        return index;
    }

    public void resortEntities() {
        for(int i = entities.size() - 1; i >= 0; i--) {
            AbstractEntity entity = entities.get(i);
            if(entity.isDepthChanged()) {
                entities.remove(i);
                entity.setDepthChanged(false);
                entitiesToResort.add(entity);
            }
        }

        for(AbstractEntity entity : entitiesToResort) {
            int index = Collections.binarySearch(entities, entity, (o1, o2) -> Float.compare(o2.getDepth(), o1.getDepth()));
            if(index < 0) {
                index = -(index + 1);
            }
            entities.add(index, entity);
        }
        entitiesToResort.clear();
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

    public <C extends AbstractEntity> List<C> getByClass(Class<C> clazz) {
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
    public Spliterator<AbstractEntity> spliterator() {
        return entities.spliterator();
    }


    /**
     * Internal class which provides a quick map between Class and entities
     */
    private class EntityTypeCache {
        private Map<Class<? extends AbstractEntity>, Set<AbstractEntity>> map;

        private EntityTypeCache() {
            map = new HashMap<>();
        }

        public void add(AbstractEntity entity) {
            map.computeIfAbsent(entity.getClass(), key -> new HashSet<>()).add(entity);
        }

        public void remove(AbstractEntity entity) {
            map.computeIfPresent(entity.getClass(), (k, v) -> {v.remove(entity); return v;});
        }

        @SuppressWarnings("unchecked")
        public <C extends AbstractEntity> List<C> get(Class<C> clazz) {
            ArrayList<C> entityList = new ArrayList();

            for (Map.Entry<Class<? extends AbstractEntity>, Set<AbstractEntity>> entry : map.entrySet()) {
                if (clazz.isAssignableFrom(entry.getKey())) {
                    entityList.addAll((Collection<? extends C>) entry.getValue());
                }
            }
            return entityList;
        }
    }

}
