package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.MathUtils;

public class Circle extends Shape2D {
    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float radius;

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x - radius);
        rect.setY(y - radius);
        rect.setWidth(2 * radius);
        rect.setHeight(2 * radius);
    }

    public static boolean circleCircleCollision(Circle circle1, Circle circle2) {
        return MathUtils.distanceSqr(circle1.getX(), circle1.getY(), circle2.getX(), circle2.getY()) <=
                (circle1.getRadius() + circle2.getRadius()) * (circle1.getRadius() + circle2.getRadius());
    }

    public static boolean circlePointCollision(Circle circle, Point2D s2) {
        return MathUtils.distanceSqr(circle.getX(), circle.getY(), s2.getX(), s2.getY()) <=
                (circle.getRadius()) * (circle.getRadius());
    }

    public static boolean circleRectCollision(Circle circle, Rect rect) {
        float cDisX = Math.abs(circle.x - (rect.getX() + (rect.getWidth()/2)));
        float cDisY = Math.abs(circle.y - (rect.getY() + (rect.getHeight()/2)));

        float halfWidth = .5f * rect.getWidth();
        float halfHeight = .5f * rect.getHeight();

        if(cDisX > (halfWidth + circle.getRadius())) {
            return false;
        }
        if(cDisY > (halfHeight + circle.getRadius())) {
            return false;
        }

        if(cDisX <= halfWidth + circle.getRadius() && cDisY <= halfHeight) {
            return true;
        }

        if(cDisY <= halfHeight + circle.getRadius() && cDisX <= halfWidth) {
            return true;
        }

        float cornerDistance = (cDisX - halfWidth) * (cDisX - halfWidth) +
                (cDisY - halfHeight) * (cDisY - halfHeight);

        return cornerDistance <= circle.getRadius() * circle.getRadius();
    }
}
