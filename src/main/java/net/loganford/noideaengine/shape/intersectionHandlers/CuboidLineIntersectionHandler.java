package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Cuboid;
import net.loganford.noideaengine.shape.Line;

public class CuboidLineIntersectionHandler implements IntersectionHandler<Cuboid, Line> {
    @Override
    public boolean intersects(Cuboid cuboid, Line line) {
        return cuboid.collidesWith(line);
    }
}
