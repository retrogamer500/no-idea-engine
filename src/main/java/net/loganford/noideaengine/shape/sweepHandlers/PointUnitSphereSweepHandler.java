package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.shape.UnitSphere;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PointUnitSphereSweepHandler implements SweepHandler<Point, UnitSphere> {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, UnitSphere unitSphere) {
        result.clear();

        Vector3f oc = V3F.set(point.getPosition()).sub(unitSphere.getPosition());
        float a = velocity.dot(velocity);
        float b = 2f * oc.dot(velocity);
        float c = oc.dot(oc) - 1f;
        float discriminant = b * b - 4f * a * c;
        if(discriminant < 0) {
            return;
        }

        float disSqrt = (float)Math.sqrt(discriminant);
        float t1 = (-b - disSqrt) / (2f * a);
        float t2 = (-b + disSqrt) / (2f * a);
        float t = Math.min(t1, t2);

        if( t < 0 || t > 1) {
            return;
        }

        result.setDistance(t);
        result.setShape(unitSphere);
    }
}
