package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Ellipsoid;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.shape.UnitSphere;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EllipsoidUnitSphereSweepHandler implements SweepHandler<Ellipsoid, UnitSphere> {
    private static PointEllipsoidSweepHandler HANDLER = new PointEllipsoidSweepHandler();
    private static Point POINT = new Point(0, 0, 0);
    private static Ellipsoid ELLIPSOID = new Ellipsoid(new Vector3f(), new Vector3f());
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();

    @Override
    public void sweep(SweepResult result, Ellipsoid ellipsoid, Vector3fc velocity, UnitSphere unitSphere) {
        POINT.setPosition(V3F.set(ellipsoid.getPosition()));
        ELLIPSOID.setPosition(V3F_2.set(unitSphere.getPosition()));
        ELLIPSOID.setRadius(V3F_3.set(1).add(ellipsoid.getRadius()));

        HANDLER.sweep(result, POINT, velocity, ELLIPSOID);

        if(result.getShape() != null) {
            result.setShape(ellipsoid);
        }
    }
}
