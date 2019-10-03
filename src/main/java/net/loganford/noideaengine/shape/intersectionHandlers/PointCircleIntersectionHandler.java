package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Circle;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.utils.math.MathUtils;

public class PointCircleIntersectionHandler implements IntersectionHandler<Point, Circle> {
    @Override
    public boolean intersects(Point point, Circle circle) {
        return MathUtils.distanceSqr(circle.getX(), circle.getY(), point.getX(), point.getY()) <=
                (circle.getRadius()) * (circle.getRadius());
    }
}
