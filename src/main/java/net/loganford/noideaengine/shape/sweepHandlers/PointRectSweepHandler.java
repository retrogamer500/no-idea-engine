package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3fc;

public class PointRectSweepHandler implements SweepHandler<Point, Rect> {
    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Rect rect) {
        result.clear();

        float deltaX = velocity.x();
        float deltaY = velocity.y();
        float scaleX = 1f / deltaX;
        float scaleY = 1f / deltaY;
        float signX = Math.signum(scaleX);
        float signY = Math.signum(scaleY);
        float halfX = .5f * rect.getWidth();
        float halfY = .5f * rect.getHeight();
        float posX = rect.getX() + halfX;
        float posY = rect.getY() + halfY;

        float nearTimeX = (posX - signX * halfX - point.getX()) * scaleX;
        float nearTimeY = (posY - signY * halfY - point.getY()) * scaleY;
        float farTimeX = (posX + signX * halfX - point.getX()) * scaleX;
        float farTimeY = (posY + signY * halfY - point.getY()) * scaleY;

        if (nearTimeX > farTimeY || nearTimeY > farTimeX) {
            return;
        }

        float nearTime = nearTimeX > nearTimeY ? nearTimeX : nearTimeY;
        float farTime = farTimeX < farTimeY ? farTimeX : farTimeY;

        if (nearTime >= 1 || farTime <= 0) {
            return;
        }

        result.setDistance(Math.max(0f, Math.min(1f, nearTime)));
        result.setShape(rect);
        if(nearTimeX > nearTimeY) {
            result.getNormal().x = -signX;
            result.getNormal().y = 0;
        }
        else {
            result.getNormal().x = 0;
            result.getNormal().y = -signY;
        }
    }
}
