package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.*;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EllipsoidFaceSweepHandler implements SweepHandler<Ellipsoid, Face> {

    private static UnitSphereFaceSweepHandler SWEEP_HANDLER = new UnitSphereFaceSweepHandler();

    private static Vector3f V3F = new Vector3f();
    private static UnitSphere UNIT_SPHERE = new UnitSphere(0, 0, 0);
    private static Face FACE = new Face(new Vector3f(), new Vector3f(), new Vector3f());

    @Override
    public void sweep(SweepResult result, Ellipsoid ellipsoid, Vector3fc velocity, Face face) {
        V3F.set(velocity).div(ellipsoid.getRadius());
        UNIT_SPHERE.setPosition(ellipsoid.getPosition());
        FACE.getV0().div(ellipsoid.getRadius());
        FACE.getV1().div(ellipsoid.getRadius());
        FACE.getV2().div(ellipsoid.getRadius());

        SWEEP_HANDLER.sweep(result, UNIT_SPHERE, V3F, FACE);

        if(result.getShape() != null) {
            result.setShape(face);
            result.getNormal().mul(ellipsoid.getRadius()).normalize();
        }
    }
}
