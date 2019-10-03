package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Shape;

public interface IntersectionHandler<A extends Shape, B extends Shape> {
    boolean intersects(A a, B b);
}
