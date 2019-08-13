package net.loganford.noideaengine.shape;

public interface IntersectionHandler<A extends Shape, B extends Shape> {
    boolean intersects(A a, B b);
}
