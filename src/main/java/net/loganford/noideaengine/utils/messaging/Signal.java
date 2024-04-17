package net.loganford.noideaengine.utils.messaging;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Signal<T> {
    public static final int CLEANUP_PERIOD = 64;

    private List<WeakReference<Listener<T>>> listeners;
    private int modCounter = 0;

    public Signal() {
        listeners = new ArrayList<>();
    }

    public void subscribe(Listener<T> listener) {
        listeners.add(new WeakReference<>(listener));
        modCounter++;

        if(modCounter > CLEANUP_PERIOD) {
            cleanReferences();
            modCounter = 0;
        }
    }

    public void unsubscribe(Listener<T> listener) {
        for(int i = listeners.size() - 1; i >= 0; i--) {
            WeakReference<Listener<T>> reference = listeners.get(i);
            if(Objects.equals(reference.get(), listener)) {
                listeners.remove(i);
            }
        }
    }

    @SuppressWarnings({"ConstantConditions", "ForLoopReplaceableByForEach"})
    public void dispatch(T object) {
        for(int i = listeners.size() - 1; i >= 0; i--) {
            WeakReference<Listener<T>> reference = listeners.get(i);
            if(reference.get() != null) {
                reference.get().receive(this, object);
            }
            else {
                modCounter++;
            }
        }

        if(modCounter > CLEANUP_PERIOD) {
            cleanReferences();
            modCounter = 0;
        }
    }

    private void cleanReferences() {
        for(int i = listeners.size() - 1; i >= 0; i--) {
            if(listeners.get(i).get() == null) {
                listeners.remove(i);
            }
        }
    }
}
