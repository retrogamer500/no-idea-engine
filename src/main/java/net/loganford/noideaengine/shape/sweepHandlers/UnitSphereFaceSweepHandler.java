package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.graphics.Face;
import net.loganford.noideaengine.shape.*;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class UnitSphereFaceSweepHandler implements SweepHandler<UnitSphere, Face> {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();

    private static Face FACE = new Face(new Vector3f(), new Vector3f(), new Vector3f());
    private static Face FACE_2 = new Face(new Vector3f(), new Vector3f(), new Vector3f());

    private static UnitSphere UNIT_SPHERE_1 = new UnitSphere(new Vector3f());
    private static UnitSphere UNIT_SPHERE_2 = new UnitSphere(new Vector3f());
    private static UnitSphere UNIT_SPHERE_3 = new UnitSphere(new Vector3f());

    private static Cylinder CYLINDER_1 = new Cylinder(new Vector3f(), new Vector3f());
    private static Cylinder CYLINDER_2 = new Cylinder(new Vector3f(), new Vector3f());
    private static Cylinder CYLINDER_3 = new Cylinder(new Vector3f(), new Vector3f());

    private static SweepResult SWEEP_RESULT = new SweepResult();

    private static SweepHandler POINT_FACE = new PointFaceSweepHandler();
    private static SweepHandler POINT_SPHERE = new PointUnitSphereSweepHandler();
    private static SweepHandler POINT_CYLINDER = new PointCylinderSweepHandler();

    private static Point POINT = new Point(0, 0, 0);

    @Override
    public void sweep(SweepResult result, UnitSphere unitSphere, Vector3fc velocity, Face face) {
        result.clear();

        POINT.setPosition(unitSphere.getPosition());

        Vector3f edge0 = V3F.set(face.getV1()).sub(face.getV0());
        Vector3f edge1 = V3F_1.set(face.getV2()).sub(face.getV0());
        Vector3f normal = V3F_2.set(edge1).cross(edge0).normalize();

        //Front side
        Face extrudedFace1 = FACE;
        extrudedFace1.setV0(V3F_3.set(face.getV0()).add(normal));
        extrudedFace1.setV1(V3F_3.set(face.getV1()).add(normal));
        extrudedFace1.setV2(V3F_3.set(face.getV2()).add(normal));

        //Back side, change winding order
        Face extrudedFace2 = FACE_2;
        extrudedFace2.setV0(V3F_3.set(face.getV0()).sub(normal));
        extrudedFace2.setV1(V3F_3.set(face.getV2()).sub(normal));
        extrudedFace2.setV2(V3F_3.set(face.getV1()).sub(normal));

        UnitSphere sphere0 = UNIT_SPHERE_1;
        sphere0.setPosition(face.getV0());

        UnitSphere sphere1 = UNIT_SPHERE_2;
        sphere1.setPosition(face.getV1());

        UnitSphere sphere2 = UNIT_SPHERE_3;
        sphere2.setPosition(face.getV2());

        Cylinder cylinder0 = CYLINDER_1;
        cylinder0.setV0(face.getV0());
        cylinder0.setV1(face.getV1());

        Cylinder cylinder1 = CYLINDER_2;
        cylinder1.setV0(face.getV1());
        cylinder1.setV1(face.getV2());

        Cylinder cylinder2 = CYLINDER_3;
        cylinder2.setV0(face.getV2());
        cylinder2.setV1(face.getV0());

        Shape[] shapes = {extrudedFace1, extrudedFace2, sphere0, sphere1, sphere2, cylinder0, cylinder1, cylinder2};
        SweepHandler[] handlers = {POINT_FACE, POINT_FACE, POINT_SPHERE, POINT_SPHERE, POINT_SPHERE, POINT_CYLINDER, POINT_CYLINDER, POINT_CYLINDER};

        for(int i = 0; i < shapes.length; i++) {
            if(i == 2) {
                if(result.collides()) {
                    return;
                }
            }

            Shape shape = shapes[i];
            SweepHandler handler = handlers[i];

            handler.sweep(SWEEP_RESULT, POINT, velocity, shape);

            if(SWEEP_RESULT.getDistance() < result.getDistance()) {
                result.set(SWEEP_RESULT);
            }
        }
    }
}
