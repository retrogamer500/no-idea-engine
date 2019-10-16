package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Ellipsoid;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.shape.UnitSphere;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PointEllipsoidSweepHandler implements SweepHandler<Point, Ellipsoid> {
    private static PointUnitSphereSweepHandler handler = new PointUnitSphereSweepHandler();
    private static Point POINT = new Point(0, 0, 0);
    private static Vector3f V3F = new Vector3f();
    private static UnitSphere UNIT_SPHERE = new UnitSphere(0, 0, 0);

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Ellipsoid ellipsoid) {
        //Transform point and velocity
        POINT.setPosition(V3F.set(point.getPosition()).div(ellipsoid.getRadius()));
        V3F.set(velocity).div(ellipsoid.getRadius());
        UNIT_SPHERE.setPosition(ellipsoid.getPosition());
        handler.sweep(result, POINT, V3F, UNIT_SPHERE);

        if(result.getShape() != null) {
            result.setShape(ellipsoid);
            result.getNormal().mul(ellipsoid.getRadius()).normalize();
        }
    }
}
