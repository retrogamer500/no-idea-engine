package net.loganford.noideaengine.state.entity;

import java.util.*;
import java.util.function.Consumer;

public class EntityStore implements Iterable<Entity> {
    private ArrayList<Entity> entities;
    private EntityTypeCache typeCache;
    private ArrayList<Entity> entitiesToResort;

    public EntityStore() {
        entities = new ArrayList<>();
        typeCache = new EntityTypeCache();
        entitiesToResort = new ArrayList<>();
    }

    public int add(Entity entity) {
        int index = Collections.binarySearch(entities, entity, (o1, o2) -> Float.compare(o2.getDepth(), o1.getDepth()));
        if(index < 0) {
            index = -(index + 1);
        }
        entities.add(index, entity);
        typeCache.add(entity);
        return index;
    }

    public void remove(Entity entity) {
        for(int i = entities.size() - 1; i >= 0; i--) {
            if(entities.get(i).equals(entity)) {
                entities.remove(i);
                typeCache.remove(entity);
            }
        }
    }

    public void resort() {
        for(int i = entities.size() - 1; i >= 0; i--) {
            Entity entity = entities.get(i);
            if(entity.isDepthChanged()) {
                entities.remove(i);
                entity.setDepthChanged(false);
                entitiesToResort.add(entity);
            }
        }

        for(Entity entity : entitiesToResort) {
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
            Entity entity = entities.get(i);
            if(entity.isDestroyed()) {
                entities.remove(i);
                typeCache.remove(entity);
            }
        }
    }

    public <C extends Entity> List<C> byClass(Class<C> clazz) {
        return typeCache.get(clazz);
    }

    public int size() {
        return entities.size();
    }

    public Entity get(int index) {
        return entities.get(index);
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }

    @Override
    public void forEach(Consumer<? super Entity> action) {
        entities.forEach(action);
    }

    @Override
    public Spliterator<Entity> spliterator() {
        return entities.spliterator();
    }


    /**
     * Internal class which provides a quick map between Class and entities
     */
    private class EntityTypeCache {
        private Map<Class<? extends Entity>, Set<? extends Entity>> map;

        private EntityTypeCache() {
            map = new HashMap<>();
        }

        @SuppressWarnings("unchecked")
        public void add(Entity entity) {
            Class clazz = entity.getClass();
            while(clazz != null) {
                map.computeIfAbsent(clazz, key -> new HashSet<>()).add(entity);
                clazz = clazz.getSuperclass();
            }
        }

        @SuppressWarnings("unchecked")
        public void remove(Entity entity) {
            Class clazz = entity.getClass();
            while(clazz != null) {
                map.computeIfPresent(clazz, (k, v) -> {v.remove(entity); return v;});
                clazz = clazz.getSuperclass();
            }
        }

        @SuppressWarnings("unchecked")
        public <C extends Entity> List<C> get(Class<C> clazz) {
            ArrayList<C> entityList = new ArrayList<>();

            for (Map.Entry<Class<? extends Entity>, Set<? extends Entity>> entry : map.entrySet()) {
                if (clazz.isAssignableFrom(entry.getKey())) {
                    entityList.addAll((Collection<? extends C>) entry.getValue());
                }
            }
            return entityList;
        }
    }

}
