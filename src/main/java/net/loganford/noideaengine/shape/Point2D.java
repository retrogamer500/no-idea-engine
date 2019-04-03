package net.loganford.noideaengine.shape;


import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.MathUtils;

public class Point2D extends Shape2D {

    @Getter @Setter private float x;
    @Getter @Setter private float y;

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(0);
        rect.setHeight(0);
    }

    public static boolean pointPointCollision(Point2D point1, Point2D point2) {
        return Math.abs(point1.x - point2.x) < MathUtils.EPSILON && Math.abs(point1.y - point2.y) < MathUtils.EPSILON;
    }
}
