package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Circle;
import net.loganford.noideaengine.shape.Rect;

public class RectCircleIntersectionHandler implements IntersectionHandler<Rect, Circle> {
    @Override
    public boolean intersects(Rect rect, Circle circle) {
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
    }
}
