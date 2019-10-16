package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Face;
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

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Face face) {
        result.clear();

        //Compute normal of face
        Vector3f edge0 = V3F.set(face.getV1()).sub(face.getV0());
        Vector3f edge1 = V3F_1.set(face.getV2()).sub(face.getV0());
        Vector3f normal = V3F_2.set(edge0).cross(edge1);

        float normalDotVelocity = normal.dot(velocity);
        if(Math.abs(normalDotVelocity) < MathUtils.EPSILON) {
            return;
        }

        float d = normal.dot(face.getV0());
        float t = (normal.dot(point.getPosition()) + d) / normalDotVelocity;

        if(t < 0 || t > 1) {
            return;
        }

        //Ray intersects plane

        //Test if outside edge 0
        Vector3f vp0 = V3F_3.set(point.getPosition()).sub(face.getV0());
        Vector3f cross = V3F_1.set(edge0).cross(vp0);
        if(normal.dot(cross) < 0) {
            return;
        }

        //Test if outside edge 1
        edge1 = V3F.set(face.getV2()).sub(face.getV1());
        Vector3f vp1 = V3F_3.set(point.getPosition()).sub(face.getV1());
        cross = V3F_1.set(edge1).cross(vp1);
        if(normal.dot(cross) < 0) {
            return;
        }

        //Test if outside edge 2
        Vector3f edge2 = V3F.set(face.getV0()).sub(face.getV2());
        Vector3f vp2 = V3F_3.set(point.getPosition()).sub(face.getV2());
        cross = V3F_1.set(edge2).cross(vp2);
        if(normal.dot(cross) < 0) {
            return;
        }

        //Intersection takes place
        result.setDistance(t);
        result.setShape(face);
    }
}
