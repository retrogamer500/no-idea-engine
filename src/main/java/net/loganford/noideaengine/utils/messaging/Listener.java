package net.loganford.noideaengine.utils.messaging;

public interface Listener<T> {
    void receive(Signal<T> signal, T object);
}
