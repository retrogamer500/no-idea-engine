package net.loganford.noideaengine.resources;

import net.loganford.noideaengine.GameEngineException;
import net.loganford.noideaengine.graphics.UnsafeMemory;
import net.loganford.noideaengine.resources.loading.LoadingContext;
import java.util.*;

public class ResourceManager<T extends Resource> {
    private Map<String, T> resources;

    public ResourceManager() {
        resources = new HashMap<>();
    }

    public void put(String key, T resource) {
        resources.put(key, resource);
    }

    @SuppressWarnings("unchecked")
    public T get(String key) {
        T resource = resources.get(key);
        if(resource == null) {
            throw new GameEngineException("Resource does not exist: " + key);
        }

        try {
            if (resource instanceof PrototypeResource) {
                return (T)((PrototypeResource) resource).clone();
            }
        }
        catch(CloneNotSupportedException e) {
            throw new GameEngineException(e);
        }

        return resource;
    }

    public boolean exists(String key) {
        return resources.containsKey(key);
    }

    public T getPrototype(String key) {
        T resource = resources.get(key);
        if(resource instanceof PrototypeResource) {
            return resource;
        }
        throw new GameEngineException("Resource isn't a prototype resource. Use ResourceManager.get(key).");
    }

    public List<T> getValues() {
        return new ArrayList<>(resources.values());
    }

    public List<String> getKeys() {
        return new ArrayList<>(resources.keySet());
    }

    public void unloadGroups(LoadingContext ctx) {
        for(Integer groupToUnload: ctx.getUnloadingGroups()) {
            Iterator<Map.Entry<String, T>> iterator = resources.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, T> entry = iterator.next();
                if(entry.getValue().getLoadingGroup() == groupToUnload) {
                    if(entry.getValue() instanceof UnsafeMemory) {
                        ((UnsafeMemory)entry.getValue()).freeMemory();
                    }
                    iterator.remove();
                }
            }
        }
    }
}
