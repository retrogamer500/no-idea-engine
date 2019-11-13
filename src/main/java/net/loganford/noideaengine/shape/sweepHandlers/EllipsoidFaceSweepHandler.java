package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.graphics.Face;
import net.loganford.noideaengine.shape.Ellipsoid;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.shape.UnitSphere;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EllipsoidFaceSweepHandler implements SweepHandler<Ellipsoid, Face> {

    private static UnitSphereFaceSweepHandler SWEEP_HANDLER = new UnitSphereFaceSweepHandler();

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();
    private static UnitSphere UNIT_SPHERE = new UnitSphere(0, 0, 0);
    private static Face FACE = new Face(new Vector3f(), new Vector3f(), new Vector3f());

    @Override
    public void sweep(SweepResult result, Ellipsoid ellipsoid, Vector3fc velocity, Face face) {
        V3F.set(velocity).div(ellipsoid.getRadius());
        UNIT_SPHERE.setPosition(V3F_2.set(ellipsoid.getPosition()).div(ellipsoid.getRadius()));
        FACE.setV0(face.getV0());
        FACE.setV1(face.getV1());
        FACE.setV2(face.getV2());
        FACE.getV0().div(ellipsoid.getRadius());
        FACE.getV1().div(ellipsoid.getRadius());
        FACE.getV2().div(ellipsoid.getRadius());

        SWEEP_HANDLER.sweep(result, UNIT_SPHERE, V3F, FACE);

        if(result.getShape() != null) {
            result.setShape(face);
            result.getVelocity().mul(ellipsoid.getRadius());
            result.getNormal().div(V3F_3.set(ellipsoid.getRadius()).mul(ellipsoid.getRadius())).normalize();
        }
    }
}
