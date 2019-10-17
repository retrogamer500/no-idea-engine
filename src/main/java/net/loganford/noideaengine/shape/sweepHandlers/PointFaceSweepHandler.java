package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.graphics.Face;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PointFaceSweepHandler implements SweepHandler<Point, Face> {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();
    private static Vector3f V3F_4 = new Vector3f();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Face face) {
        result.clear();

        //Compute normal of face
        Vector3f edge1 = V3F.set(face.getV1()).sub(face.getV0());
        Vector3f edge2 = V3F_1.set(face.getV2()).sub(face.getV0());

        Vector3f h = V3F_2.set(velocity).cross(face.getV0());
        float det = edge1.dot(h);

        if(det > -MathUtils.EPSILON && det < MathUtils.EPSILON) {
            return;
        }

        float invDet = 1f / det;
        Vector3f s = V3F_3.set(point.getPosition()).sub(face.getV0());
        float u = invDet * (s.dot(h));

        if (u < 0.0 || u > 1.0) {
            return;
        }

        Vector3f q = V3F_4.set(s).cross(edge1);
        float v = invDet * velocity.dot(q);

        if (v < 0.0 || u + v > 1.0) {
            return;
        }

        float t = invDet * edge2.dot(q);

        if(t < 0 || t > 1) {
            return;
        }

        //Intersection takes place
        result.setDistance(t);
        result.setShape(face);
    }
}
