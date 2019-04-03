package net.loganford.noideaengine.resources;

import net.loganford.noideaengine.GameEngineException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceManager<T> {
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
}
