package net.loganford.noideaengine.shape;

public interface IntersectionHandler<A, B> {
    boolean intersects(A a, B b);
}
