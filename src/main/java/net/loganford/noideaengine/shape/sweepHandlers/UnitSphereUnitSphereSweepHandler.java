package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Ellipsoid;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.shape.UnitSphere;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class UnitSphereUnitSphereSweepHandler implements SweepHandler<UnitSphere, UnitSphere> {
    private static PointEllipsoidSweepHandler sweepHandler = new PointEllipsoidSweepHandler();
    private static Point POINT = new Point(0, 0, 0);
    private static Ellipsoid ELLIPSOID = new Ellipsoid(new Vector3f(), new Vector3f());

    @Override
    public void sweep(SweepResult result, UnitSphere unitSphere, Vector3fc velocity, UnitSphere unitSphere2) {
        POINT.setPosition(unitSphere.getPosition());
        ELLIPSOID.setPosition(unitSphere2.getPosition());
        ELLIPSOID.getRadius().set(2f, 2f, 2f);
        sweepHandler.sweep(result, POINT, velocity, ELLIPSOID);

        if(result.getShape() != null) {
            result.setShape(unitSphere2);
        }
    }
}
