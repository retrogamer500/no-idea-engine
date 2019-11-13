package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Ellipsoid;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EllipsoidEllipsoidSweepHandler implements SweepHandler<Ellipsoid, Ellipsoid> {
    private static PointEllipsoidSweepHandler HANDLER = new PointEllipsoidSweepHandler();
    private static Point POINT = new Point(0, 0, 0);
    private static Ellipsoid ELLIPSOID = new Ellipsoid(new Vector3f(), new Vector3f());
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();

    @Override
    public void sweep(SweepResult result, Ellipsoid ellipsoid1, Vector3fc velocity, Ellipsoid ellipsoid2) {
        POINT.setPosition(V3F.set(ellipsoid1.getPosition()));
        ELLIPSOID.setPosition(V3F_2.set(ellipsoid2.getPosition()));
        ELLIPSOID.setRadius(V3F_3.set(ellipsoid2.getRadius()).add(ellipsoid1.getRadius()));

        HANDLER.sweep(result, POINT, velocity, ELLIPSOID);

        if(result.getShape() != null) {
            result.setShape(ellipsoid2);
            //Todo: need to handle normals
        }
    }
}
