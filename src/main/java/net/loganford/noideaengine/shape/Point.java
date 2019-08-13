package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.math.MathUtils;

public class Point extends Shape {
    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float z;

    public Point(float x, float y) {
        this(x, y, 0);
    }

    public Point(float x, float y, float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(0);
        rect.setHeight(0);
    }

    //Point collision handlers
    static {
        ShapeIntersectionEngine.addHandler(Point.class, Point.class, (point1, point2) ->
                Math.abs(point1.x - point2.x) < MathUtils.EPSILON && Math.abs(point1.y - point2.y) < MathUtils.EPSILON
        );

        ShapeIntersectionEngine.addHandler(Point.class, Circle.class, (point, circle) ->
                MathUtils.distanceSqr(circle.getX(), circle.getY(), point.getX(), point.getY()) <=
                (circle.getRadius()) * (circle.getRadius())
        );
    }
}
