package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Cylinder;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class PointCylinderSweepHandler implements SweepHandler<Point, Cylinder> {
    private static Vector3d V3D = new Vector3d();
    private static Vector3d V3D_1 = new Vector3d();
    private static Vector3d V3D_2 = new Vector3d();
    private static Vector3d V3D_3 = new Vector3d();
    private static Vector3d V3D_4 = new Vector3d();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Cylinder cylinder) {
        result.clear();
        Vector3d velocityD = V3D_4.set(velocity);

        Vector3d edge = V3D.set(cylinder.getV1()).sub(cylinder.getV0());
        Vector3d v = V3D_1.set(cylinder.getV0()).sub(point.getPosition());

        double edgeSqrLen = edge.lengthSquared();
        double edgeDotVel = edge.dot(velocityD);
        double edgeDotSphereVert = edge.dot(v);

        double a = edgeSqrLen * - velocityD.lengthSquared() + edgeDotVel * edgeDotVel;

        if(a == 0) {
            return;
        }

        double b = edgeSqrLen * (2.0 * velocityD.dot(v)) - 2.0 * edgeDotVel * edgeDotSphereVert;
        double c = edgeSqrLen * (1.0 - v.lengthSquared()) + edgeDotSphereVert * edgeDotSphereVert;
        double t = MathUtils.getLowestRoot(a, b, c);

        if(Double.isNaN(t) || t < -1.0/velocity.length() || t > 1) {
            return;
        }

        double f = (edgeDotVel * t - edgeDotSphereVert) / edgeSqrLen;

        if(f < 0 || f > 1) {
            return;
        }

        Vector3d centerPoint = V3D_2.set(edge).mul(f).add(cylinder.getV0());
        Vector3d intersectionPoint = V3D_3.set(velocity).mul(t).add(point.getPosition());
        Vector3d normal = intersectionPoint.sub(centerPoint);

        result.setDistance((float) t);
        result.getNormal().set(normal);
        result.setShape(cylinder);
    }
}
