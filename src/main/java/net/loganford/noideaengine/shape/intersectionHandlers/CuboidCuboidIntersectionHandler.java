package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Cuboid;

public class CuboidCuboidIntersectionHandler implements IntersectionHandler<Cuboid, Cuboid> {
    @Override
    public boolean intersects(Cuboid cube1, Cuboid cube2) {
        return cube1.collidesWith(cube2);
    }
}
