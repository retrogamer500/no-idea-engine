package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Circle;
import net.loganford.noideaengine.utils.math.MathUtils;

public class CircleCircleIntersectionHandler implements IntersectionHandler<Circle, Circle> {
    @Override
    public boolean intersects(Circle circle1, Circle circle2) {
        return MathUtils.distanceSqr(circle1.getX(), circle1.getY(), circle2.getX(), circle2.getY()) <=
                (circle1.getRadius() + circle2.getRadius()) * (circle1.getRadius() + circle2.getRadius());
    }
}
