package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Cylinder;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class PointCylinderSweepHandler implements SweepHandler<Point, Cylinder> {
    private static Vector3d V3D = new Vector3d();
    private static Vector3d V3D_1 = new Vector3d();
    private static Vector3d V3D_2 = new Vector3d();
    private static Vector3d V3D_3 = new Vector3d();

    private static Vector3d velocityD = new Vector3d();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Cylinder cylinder) {
        result.clear();
        velocityD.set(velocity);

        Vector3d edge = V3D.set(cylinder.getV1()).sub(cylinder.getV0());
        Vector3d v = V3D_1.set(cylinder.getV0()).sub(point.getPosition());

        double edgeSqrLen = edge.lengthSquared();
        double edgeDotVel = edge.dot(velocityD);
        double edgeDotSphereVert = edge.dot(v);

        double a = edgeSqrLen * - velocity.lengthSquared() + edgeDotVel * edgeDotVel;

        if(a == 0) {
            return;
        }

        double b = edgeSqrLen * (2f * velocityD.dot(v)) - 2f * edgeDotVel * edgeDotSphereVert;
        double c = edgeSqrLen * (1f - v.lengthSquared()) + edgeDotSphereVert * edgeDotSphereVert;

        double discriminant = b * b - 4f * a *c;

        if(discriminant < 0) {
            return;
        }

        double disSqrt = (float)Math.sqrt(discriminant);
        double t1 = (-b - disSqrt) / (2f * a);
        double t2 = (-b + disSqrt) / (2f * a);
        double t = Math.min(t1, t2);

        if( t < 0 || t > 1) {
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
