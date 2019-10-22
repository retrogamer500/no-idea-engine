package net.loganford.noideaengine.utils.pooling;

import java.util.HashMap;
import java.util.Map;

public class Pools {
    private static Map<Class<? extends Poolable>, ObjectPool> poolMap = new HashMap<>();

    private Pools() {}

    @SuppressWarnings("unchecked")
    public static <O extends Poolable> ObjectPool<O> getPool(Class<O> clazz) {
        if(poolMap.containsKey(clazz)) {
            return poolMap.get(clazz);
        }
        else {
            ObjectPool pool = new ObjectPool(clazz);
            poolMap.put(clazz, pool);
            return pool;
        }
    }
}
