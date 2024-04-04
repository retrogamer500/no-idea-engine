package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Rect;

public class RectRectIntersectionHandler implements IntersectionHandler<Rect, Rect> {
    @Override
    public boolean intersects(Rect rect1, Rect rect2) {
        if(rect2.getX() + rect2.getWidth() <= rect1.getX()) {
            return false;
        }
        if(rect2.getX() >= rect1.getX() + rect1.getWidth()) {
            return false;
        }
        if(rect2.getY() + rect2.getHeight() <= rect1.getY()) {
            return false;
        }
        if(rect2.getY() >= rect1.getY() + rect1.getHeight()) {
            return false;
        }
        return true;
    }
}
