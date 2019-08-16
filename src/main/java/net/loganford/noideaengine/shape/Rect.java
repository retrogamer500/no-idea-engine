package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;

public class Rect extends Shape {

    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float width;
    @Getter @Setter private float height;

    public Rect(float x, float y, float width, float height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x; this.y = y;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(width);
        rect.setHeight(height);
    }

    //Rectangle collision handlers
    static {
        ShapeIntersectionEngine.addHandler(Rect.class, Rect.class, (rect1, rect2) -> {
            if(rect2.getX() + rect2.getWidth() < rect1.getX()) {
                return false;
            }
            if(rect2.getX() > rect1.getX() + rect1.getWidth()) {
                return false;
            }
            if(rect2.getY() + rect2.getHeight() < rect1.getY()) {
                return false;
            }
            if(rect2.getY() > rect1.getY() + rect1.getHeight()) {
                return false;
            }
            return true;
        });

        ShapeIntersectionEngine.addHandler(Rect.class, Circle.class, (rect, circle) -> {
            float cDisX = Math.abs(circle.getX() - (rect.getX() + (rect.getWidth()/2)));
            float cDisY = Math.abs(circle.getY() - (rect.getY() + (rect.getHeight()/2)));

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
        });

        ShapeIntersectionEngine.addHandler(Rect.class, Point.class, (rect, point) -> {
            if(point.getX() < rect.getX()) {
                return false;
            }
            if(point.getX() > rect.getX() + rect.getWidth()) {
                return false;
            }
            if(point.getY() < rect.getY()) {
                return false;
            }
            if(point.getY() > rect.getY() + rect.getHeight()) {
                return false;
            }
            return true;
        });

        ShapeIntersectionEngine.addHandler(Rect.class, Line.class, (rect, line) -> {
            float deltaX = line.getX2() - line.getX1();
            float deltaY = line.getY2() - line.getY1();
            float scaleX = 1f / deltaX;
            float scaleY = 1f / deltaY;
            float signX = Math.signum(scaleX);
            float signY = Math.signum(scaleY);
            float halfX = .5f * rect.getWidth();
            float halfY = .5f * rect.getHeight();
            float posX = rect.getX() + halfX;
            float posY = rect.getY() + halfY;

            float nearTimeX = (posX - signX * halfX - line.getX1()) * scaleX;
            float nearTimeY = (posY - signY * halfY - line.getY1()) * scaleY;
            float farTimeX = (posX + signX * halfX - line.getX1()) * scaleX;
            float farTimeY = (posY + signY * halfY - line.getY1()) * scaleY;

            float nearTime = nearTimeX > nearTimeY ? nearTimeX : nearTimeY;
            float farTime = farTimeX < farTimeY ? farTimeX : farTimeY;

            return !(nearTime >= 1 || farTime <= 0);
        });
    }
}
