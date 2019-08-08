package net.loganford.noideaengine.utils.messaging;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Signal<T> {
    private List<WeakReference<Listener<T>>> listeners;

    public Signal() {
        listeners = new ArrayList<>();
    }

    public void subscribe(Listener<T> listener) {
        listeners.add(new WeakReference<>(listener));

        //Flush weak references every 16 items
        if((listeners.size() & 15) == 0) {
            cleanReferences();
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
        boolean dirty = false;
        for(int i = 0; i < listeners.size(); i++) {
            WeakReference<Listener<T>> reference = listeners.get(i);
            if(reference.get() != null) {
                reference.get().receive(this, object);
            }
            else {
                dirty = true;
            }
        }

        if(dirty) {
            cleanReferences();
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
