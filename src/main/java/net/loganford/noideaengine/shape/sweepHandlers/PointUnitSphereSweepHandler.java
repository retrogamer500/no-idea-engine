package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.shape.UnitSphere;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class PointUnitSphereSweepHandler implements SweepHandler<Point, UnitSphere> {
    private static Vector3d V3D = new Vector3d();
    private static Vector3d V3D_1 = new Vector3d();
    private static Vector3d V3D_2 = new Vector3d();
    private static Vector3d V3D_3 = new Vector3d();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, UnitSphere unitSphere) {
        result.clear();
        Vector3d velocityD = V3D_3.set(velocity);

        Vector3d oc = V3D.set(point.getPosition()).sub(unitSphere.getPosition());
        double a = velocityD.lengthSquared();
        double b = 2.0 * oc.dot(velocityD);
        double c = oc.dot(oc) - 1.0;
        double t = MathUtils.getLowestRoot(a, b, c);

        if(Double.isNaN(t) || t < -MathUtils.EPSILON || t > 1) {
            return;
        }

        Vector3d intersectionPoint = V3D_1.set(velocity).mul(t).add(point.getPosition());
        Vector3d normal = V3D_2.set(intersectionPoint).sub(unitSphere.getPosition());

        result.setDistance((float)t);
        result.getNormal().set(normal);
        result.setShape(unitSphere);
    }
}
