package net.loganford.noideaengine.shape;

import org.joml.Vector3fc;

public interface SweepHandler<A extends Shape, B extends Shape> {
    void sweep(SweepResult result, A a, Vector3fc velocity, B b);
}
