package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Cylinder;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PointCylinderSweepHandler implements SweepHandler<Point, Cylinder> {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Cylinder cylinder) {
        result.clear();

        Vector3f edge = V3F.set(cylinder.getV1()).sub(cylinder.getV0());
        Vector3f oc = V3F.set(cylinder.getV0()).sub(point.getPosition());

        float edgeSqrLen = edge.lengthSquared();
        float edgeDotVel = edge.dot(velocity);
        float edgeDotOc = edge.dot(oc);

        float a = edgeSqrLen * - velocity.lengthSquared() + edgeDotVel * edgeDotVel;
        float b = edgeSqrLen * (2f * velocity.dot(oc)) - 2f * edgeDotVel * edgeDotOc;
        float c = edgeSqrLen * (1f - velocity.lengthSquared()) + edgeDotOc * edgeDotOc;

        float discriminant = b * b - 4f * a *c;

        if(discriminant < 0) {
            return;
        }

        float disSqrt = (float)Math.sqrt(discriminant);
        float t1 = (-b - disSqrt) / (2f * a);
        float t2 = (-b + disSqrt) / (2f * a);
        float t = Math.min(t1, t2);

        float f = (edgeDotVel * t - edgeDotOc) / edgeSqrLen;

        if(f < 0 || f > 1) {
            return;
        }

        result.setDistance(f);
        result.setShape(cylinder);
    }
}
