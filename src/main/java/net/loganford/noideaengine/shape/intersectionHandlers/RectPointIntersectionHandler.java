package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.Rect;

public class RectPointIntersectionHandler implements IntersectionHandler<Rect, Point> {
    @Override
    public boolean intersects(Rect rect, Point point) {
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
}
