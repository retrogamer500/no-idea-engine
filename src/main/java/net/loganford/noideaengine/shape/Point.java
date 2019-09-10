package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3f;

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
    public void setPosition(Vector3f position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
    }

    @Override
    public void getPosition(Vector3f position) {
        position.x = x;
        position.y = y;
        position.z = 0;
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
        ShapeIntersectionEngine.getInstance().addIntersectionHandler(Point.class, Point.class, (point1, point2) ->
                Math.abs(point1.x - point2.x) < MathUtils.EPSILON && Math.abs(point1.y - point2.y) < MathUtils.EPSILON
        );

        ShapeIntersectionEngine.getInstance().addIntersectionHandler(Point.class, Circle.class, (point, circle) ->
                MathUtils.distanceSqr(circle.getX(), circle.getY(), point.getX(), point.getY()) <=
                (circle.getRadius()) * (circle.getRadius())
        );
    }
}
