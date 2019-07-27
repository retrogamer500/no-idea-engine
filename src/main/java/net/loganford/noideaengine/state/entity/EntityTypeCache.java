package net.loganford.noideaengine.state.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class EntityTypeCache {
    private Map<Class<? extends AbstractEntity>, Set<AbstractEntity>> map;

    public EntityTypeCache() {
        map = new HashMap<>();
    }

    public void add(AbstractEntity entity) {
        map.computeIfAbsent(entity.getClass(), key -> new HashSet<>()).add(entity);
    }

    public void remove(AbstractEntity entity) {
        map.computeIfPresent(entity.getClass(), (k, v) -> {v.remove(entity); return v;});
    }

    @SuppressWarnings("unchecked")
    public <C extends AbstractEntity> Set<C> get(Class<C> clazz) {
        return (Set<C>) map.get(clazz);
    }
}
