package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.graphics.Face;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class PointFaceSweepHandler implements SweepHandler<Point, Face> {
    private static Vector3d V3D = new Vector3d();
    private static Vector3d V3D_1 = new Vector3d();
    private static Vector3d V3D_2 = new Vector3d();
    private static Vector3d V3D_3 = new Vector3d();
    private static Vector3d V3D_4 = new Vector3d();

    private static Vector3d velocityD = new Vector3d();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Face face) {
        result.clear();
        velocityD.set(velocity);

        //Compute normal of face
        Vector3d edge1 = V3D.set(V3D_4.set(face.getV1())).sub(face.getV0());
        Vector3d edge2 = V3D_1.set(V3D_4.set(face.getV2())).sub(face.getV0());

        Vector3d h = V3D_2.set(velocity).cross(edge2);
        double det = edge1.dot(h);

        if(det == 0) {
            return;
        }

        double invDet = 1f / det;
        Vector3d s = V3D_3.set(V3D_4.set(point.getPosition())).sub(face.getV0());
        double u = invDet * (s.dot(h));

        if (u < 0.0 || u > 1.0) {
            return;
        }

        Vector3d q = V3D_4.set(s).cross(edge1);
        double v = invDet * velocityD.dot(q);

        if (v < 0.0 || u + v > 1.0) {
            return;
        }

        double t = invDet * edge2.dot(q);

        if(t < 0 || t > 1) {
            return;
        }

        //Intersection takes place
        result.setDistance((float) t);
        result.getNormal().set(edge2.cross(edge1).normalize());
        result.setShape(face);
    }
}
