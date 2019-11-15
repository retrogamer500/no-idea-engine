package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.graphics.Face;
import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3d;
import org.joml.Vector3fc;

public class PointFaceSweepHandler implements SweepHandler<Point, Face> {
    private static Vector3d V3D = new Vector3d();
    private static Vector3d V3D_1 = new Vector3d();
    private static Vector3d V3D_2 = new Vector3d();
    private static Vector3d V3D_3 = new Vector3d();
    private static Vector3d V3D_4 = new Vector3d();
    private static Vector3d V3D_5 = new Vector3d();
    private static Vector3d V3D_6 = new Vector3d();
    private static Vector3d V3D_7 = new Vector3d();
    private static Vector3d V3D_8 = new Vector3d();
    private static Vector3d V3D_9 = new Vector3d();
    private static Vector3d V3D_10 = new Vector3d();
    private static Vector3d V3D_11 = new Vector3d();

    @Override
    public void sweep(SweepResult result, Point point, Vector3fc velocity, Face face) {
        result.clear();
        Vector3d velocityD = V3D_5.set(velocity);

        //Compute normal of face
        Vector3d edge1 = V3D.set(V3D_4.set(face.getV1())).sub(face.getV0());
        Vector3d edge2 = V3D_1.set(V3D_4.set(face.getV2())).sub(face.getV0());

        Vector3d h = V3D_2.set(velocityD).cross(edge2);
        double det = edge1.dot(h);
        double invDet = 1.0 / det;

        if(Math.abs(det) < MathUtils.EPSILON || invDet < MathUtils.EPSILON) {
            if(velocityD.lengthSquared() < MathUtils.EPSILON * MathUtils.EPSILON) {
                Vector3d s = V3D_3.set(V3D_4.set(point.getPosition())).sub(face.getV0());
                Vector3d normal = V3D_6.set(edge1).cross(edge2);
                Vector3d projection = MathUtils.vectorProjection(s, normal, V3D_7);
                Vector3d planePoint = V3D_8.set(point.getPosition()).sub(projection);

                Vector3d v0 = V3D_9.set(face.getV1()).sub(face.getV0());
                Vector3d v1 = V3D_10.set(face.getV2()).sub(face.getV0());
                Vector3d v2 = V3D_11.set(planePoint).sub(face.getV0());
                double d00 = v0.dot(v0);
                double d01 = v0.dot(v1);
                double d11 = v1.dot(v1);
                double d20 = v2.dot(v0);
                double d21 = v2.dot(v1);
                double denom = d00 * d11 - d01 * d01;
                double v = (d11 * d20 - d01 * d21) / denom;
                double w = (d00 * d21 - d01 * d20) / denom;
                double u = 1.0f - v - w;

                if (u < 0.0 || u > 1.0) {
                    return;
                }

                if (v < 0.0 || u + v > 1.0) {
                    return;
                }

                result.setDistance(0f);
                result.getNormal().set(velocity).mul(-1).normalize();
                result.setShape(face);
            }
            else {
                return;
            }
        }
        else if(det == 0) {
            return;
        }
        else {
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

            if (t < 0 || t > 1) {
                return;
            }

            //Intersection takes place
            result.setDistance((float) t);
            result.getNormal().set(edge2.cross(edge1).normalize());
            result.setShape(face);
        }
    }
}
