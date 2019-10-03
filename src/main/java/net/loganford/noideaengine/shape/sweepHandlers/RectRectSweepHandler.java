package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3fc;

public class RectRectSweepHandler implements SweepHandler<Rect, Rect> {
    private static Rect RECT = new Rect(0, 0, 1, 1);
    private static Point POINT = new Point(0, 0);
    private static PointRectSweepHandler RECT_POINT_SWEEP_HANDLER = new PointRectSweepHandler();

    @Override
    public void sweep(SweepResult result, Rect rect1, Vector3fc velocity, Rect rect2) {
        POINT.setX(rect1.getX() + rect1.getWidth()/2f);
        POINT.setY(rect1.getY() + rect1.getHeight()/2f);
        RECT.setX(rect2.getX() - rect1.getWidth()/2f);
        RECT.setY(rect2.getY() - rect1.getHeight()/2f);
        RECT.setWidth(rect2.getWidth() + rect1.getWidth());
        RECT.setHeight(rect2.getHeight() + rect1.getHeight());
        RECT_POINT_SWEEP_HANDLER.sweep(result, POINT, velocity, RECT);
    }
}
