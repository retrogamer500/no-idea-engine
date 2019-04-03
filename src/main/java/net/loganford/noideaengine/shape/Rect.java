package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;

public class Rect extends Shape2D {
    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float width;
    @Getter @Setter private float height;

    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static boolean rectRectCollision(Rect rect1, Rect rect2) {
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
    }

    public static boolean rectPointCollision(Rect rect, Point2D point) {
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
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(width);
        rect.setHeight(height);
    }
}
