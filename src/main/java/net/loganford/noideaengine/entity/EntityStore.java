package net.loganford.noideaengine.entity;

import net.loganford.noideaengine.utils.pooling.ObjectPool;
import net.loganford.noideaengine.utils.pooling.Poolable;
import net.loganford.noideaengine.utils.pooling.Pools;

import java.util.*;
import java.util.function.Consumer;

public abstract class EntityStore<T> implements Iterable<T> {
    private ArrayList<T> items;
    private ArrayList<T> itemsToResort;
    private EntityTypeCache typeCache;



    public EntityStore() {
        items = new ArrayList<>();
        itemsToResort = new ArrayList<>();
        typeCache = new EntityTypeCache();
    }

    public int add(Entity entity) {
        T item = wrap(entity);
        int index = Collections.binarySearch(items, item, (o1, o2) -> Float.compare(unwrap(o2).getDepth(), unwrap(o1).getDepth()));
        if(index < 0) {
            index = -(index + 1);
        }
        items.add(index, item);
        typeCache.add(entity);
        return index;
    }

    public int remove(Entity entity) {
        int returnValue = -1;
        for(int i = items.size() - 1; i >= 0; i--) {
            if(unwrap(items.get(i)).equals(entity)) {
                items.remove(i);
                typeCache.remove(entity);
                returnValue = i;
            }
        }
        return returnValue;
    }

    public void resort() {
        for(int i = items.size() - 1; i >= 0; i--) {
            T item = items.get(i);
            if(unwrap(item).isDepthChanged()) {
                items.remove(i);
                unwrap(item).setDepthChanged(false);
                itemsToResort.add(item);
            }
        }

        for(T item : itemsToResort) {
            int index = Collections.binarySearch(items, item, (o1, o2) -> Float.compare(unwrap(o2).getDepth(), unwrap(o1).getDepth()));
            if(index < 0) {
                index = -(index + 1);
            }
            items.add(index, item);
        }
        itemsToResort.clear();
    }

    public void removeDestroyed() {
        removeDestroyed(false);
    }

    public void removeDestroyed(boolean returnToPool) {
        for(int i = items.size() - 1; i >= 0; i--) {
            T item = items.get(i);
            Entity entity = unwrap(item);
            if(entity.isDestroyed()) {
                items.remove(i);
                typeCache.remove(unwrap(item));

                if(returnToPool && entity instanceof Poolable) {
                    Poolable poolable = (Poolable) entity;
                    ObjectPool pool = Pools.getPool(poolable.getClass());
                    //noinspection unchecked
                    pool.put(poolable);
                }
            }
        }
    }

    public <C> List<C> byClass(Class<C> clazz) {
        return typeCache.get(clazz);
    }

    public int size() {
        return items.size();
    }

    public T get(int index) {
        return items.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        items.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return items.spliterator();
    }

    protected abstract Entity unwrap(T item);
    protected abstract T wrap(Entity entity);




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
        public <C> List<C> get(Class<C> clazz) {
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
