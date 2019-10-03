package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.utils.math.MathUtils;

public class PointPointIntersectionHandler implements IntersectionHandler<Point, Point> {
    @Override
    public boolean intersects(Point point1, Point point2) {
        return Math.abs(point1.getX() - point2.getX()) < MathUtils.EPSILON &&
                Math.abs(point1.getY() - point2.getY()) < MathUtils.EPSILON;
    }
}
