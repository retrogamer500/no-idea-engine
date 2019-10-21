package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.graphics.Face;
import net.loganford.noideaengine.shape.*;
import org.joml.Vector3f;
import org.junit.Test;

public class PointUnitSphereSweepHandlerTest {

    @Test
    public void testSweep() throws Exception {
        SweepHandler<Point, UnitSphere> sweepHandler = new PointUnitSphereSweepHandler();
        Point point = new Point(0, 0, 0);
        UnitSphere unitSphere = new UnitSphere(new Vector3f(2, 0, 0));
        Vector3f velocity = new Vector3f(2, .5f, 0);
        SweepResult result = new SweepResult();

        sweepHandler.sweep(result, point, velocity, unitSphere);

        System.out.println("Collides: " + result.collides());

    }



    @Test
    public void testSweep2() throws Exception {
        SweepHandler<Point, Face> sweepHandler = new PointFaceSweepHandler();
        Point point = new Point(.3f, .3f, -1);
        Face face = new Face(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f(0, 1, 0));
        Vector3f velocity = new Vector3f(0, 0, .5f);
        SweepResult result = new SweepResult();

        sweepHandler.sweep(result, point, velocity, face);

        System.out.println("Collides: " + result.collides());
    }

    @Test
    public void testSweep3() throws Exception {
        SweepHandler<Point, Cylinder> sweepHandler = new PointCylinderSweepHandler();
        Point point = new Point(.5f, 0, 2);
        Cylinder cylinder = new Cylinder(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0));
        Vector3f velocity = new Vector3f(0, 0, -2f);
        SweepResult result = new SweepResult();

        sweepHandler.sweep(result, point, velocity, cylinder);

        System.out.println("Collides: " + result.collides());
    }


    @Test
    public void testSweep4() throws Exception {
        SweepHandler<Ellipsoid, Face> sweepHandler = new EllipsoidFaceSweepHandler();
        Ellipsoid ellipsoid = new Ellipsoid(new Vector3f(-.5f, 5, -3f), new Vector3f(2, 2, 2));
        Face face = new Face(new Vector3f(0, 0, 0), new Vector3f(0, 10, 0), new Vector3f(10, 0, 0));
        Vector3f velocity = new Vector3f(0, 0, 3f);
        SweepResult result = new SweepResult();

        sweepHandler.sweep(result, ellipsoid, velocity, face);

        System.out.println("Collides: " + result.collides());
    }
}