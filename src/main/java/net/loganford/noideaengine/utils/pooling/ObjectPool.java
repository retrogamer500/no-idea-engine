package net.loganford.noideaengine.utils.pooling;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.GameEngineException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ObjectPool<O extends Poolable> {
    @Getter @Setter private int maxSize = 1024;
    private List<O> objects = new ArrayList<>();
    private Constructor<O> constructor;
    private Class<O> clazz;

    public ObjectPool(Class<O> clazz) {
        this.clazz = clazz;
    }

    public O obtain() {
        if(objects.size() == 0) {
            try {
                if(constructor == null) {
                    constructor = clazz.getConstructor();
                }
                O object = constructor.newInstance();
                return object;
            }
            catch(Exception e) {
                throw new GameEngineException("Tried to pool an object with no default constructor!");
            }
        }

        return objects.remove(objects.size() - 1);
    }

    public void put(O object) {
        if(objects.size() < maxSize || maxSize == -1) {
            object.reset();
            objects.add(object);
        }
    }
}
