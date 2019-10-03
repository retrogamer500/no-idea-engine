package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Line2D;
import net.loganford.noideaengine.shape.Rect;

public class RectLineIntersectionHandler implements IntersectionHandler<Rect, Line2D> {
    @Override
    public boolean intersects(Rect rect, Line2D line) {
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

        if (nearTimeX > farTimeY || nearTimeY > farTimeX) {
            return false;
        }

        float nearTime = nearTimeX > nearTimeY ? nearTimeX : nearTimeY;
        float farTime = farTimeX < farTimeY ? farTimeX : farTimeY;

        if (nearTime >= 1 || farTime <= 0) {
            return false;
        }

        return true;
    }
}
