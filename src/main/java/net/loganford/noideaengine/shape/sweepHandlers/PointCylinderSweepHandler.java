package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Cylinder;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PointCylinderSweepHandler implements SweepHandler<Point, Cylinder> {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Cylinder cylinder) {
        result.clear();

        Vector3f edge = V3F.set(cylinder.getV1()).sub(cylinder.getV0());
        Vector3f v = V3F_1.set(cylinder.getV0()).sub(point.getPosition());

        float edgeSqrLen = edge.lengthSquared();
        float edgeDotVel = edge.dot(velocity);
        float edgeDotSphereVert = edge.dot(v);

        float a = edgeSqrLen * - velocity.lengthSquared() + edgeDotVel * edgeDotVel;
        float b = edgeSqrLen * (2f * velocity.dot(v)) - 2f * edgeDotVel * edgeDotSphereVert;
        float c = edgeSqrLen * (1f - v.lengthSquared()) + edgeDotSphereVert * edgeDotSphereVert;

        float discriminant = b * b - 4f * a *c;

        if(discriminant < 0) {
            return;
        }

        float disSqrt = (float)Math.sqrt(discriminant);
        float t1 = (-b - disSqrt) / (2f * a);
        float t2 = (-b + disSqrt) / (2f * a);
        float t = Math.min(t1, t2);

        float f = (edgeDotVel * t - edgeDotSphereVert) / edgeSqrLen;

        if(f < 0 || f > 1) {
            return;
        }

        Vector3f centerPoint = V3F_2.set(edge).mul(f).add(cylinder.getV0());
        Vector3f intersectionPoint = V3F_3.set(velocity).mul(t).add(point.getPosition());
        Vector3f normal = intersectionPoint.sub(centerPoint);

        result.setDistance(t);
        result.getNormal().set(normal);
        result.setShape(cylinder);
    }
}
