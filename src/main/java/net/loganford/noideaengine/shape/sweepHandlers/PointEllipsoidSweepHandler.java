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
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();
    private static UnitSphere UNIT_SPHERE = new UnitSphere(0, 0, 0);

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Ellipsoid ellipsoid) {
        //Transform point and velocity
        POINT.setPosition(V3F.set(point.getPosition()).div(ellipsoid.getRadius()));
        V3F_2.set(velocity).div(ellipsoid.getRadius());
        UNIT_SPHERE.setPosition(V3F_3.set(ellipsoid.getPosition()).div(ellipsoid.getRadius()));
        handler.sweep(result, POINT, V3F_2, UNIT_SPHERE);

        if(result.getShape() != null) {
            result.setShape(ellipsoid);
            result.getVelocity().mul(ellipsoid.getRadius());
            result.getNormal().mul(ellipsoid.getRadius()).normalize();
        }
    }
}
