package net.loganford.noideaengine.utils.memory;

import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Log4j2
public class UnsafeMemoryTracker {
    public static HashSet<UnsafeMemory> set = new HashSet<>();

    public static void track(UnsafeMemory unsafeMemory) {
        set.add(unsafeMemory);
    }

    public static void untrack(UnsafeMemory unsafeMemory) {
        set.remove(unsafeMemory);
    }

    public static void printMemoryStatus() {
        HashMap<Class, Integer> status = new HashMap<>();

        for(UnsafeMemory unsafeMemory : set) {
            Class clazz = unsafeMemory.getClass();
            if(status.containsKey(clazz)) {
                int count = status.get(clazz);
                status.put(clazz, count+1);
            }
            else {
                status.put(clazz, 1);
            }
        }

        log.debug("Unsafe memory report:");
        for(Map.Entry<Class, Integer> entry : status.entrySet()) {
            log.debug("Class: " + entry.getKey().getName() + "   Count: " + entry.getValue());
        }
        log.debug("");
    }
}
