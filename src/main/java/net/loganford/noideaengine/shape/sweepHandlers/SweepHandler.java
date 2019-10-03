package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3fc;

public interface SweepHandler<A extends Shape, B extends Shape> {
    void sweep(SweepResult result, A a, Vector3fc velocity, B b);
}
