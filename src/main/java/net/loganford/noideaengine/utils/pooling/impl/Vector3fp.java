package net.loganford.noideaengine.utils.pooling.impl;

import net.loganford.noideaengine.utils.pooling.Poolable;
import org.joml.Vector3f;

/**
 * Poolable implementation of Vector3f
 */
public class Vector3fp extends Vector3f implements Poolable {
    @Override
    public void reset() {
        set(0, 0, 0);
    }
}
