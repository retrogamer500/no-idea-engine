package net.loganford.noideaengine.shape.sweepHandlers;

import net.loganford.noideaengine.shape.Point;
import net.loganford.noideaengine.shape.SweepResult;
import net.loganford.noideaengine.shape.UnitSphere;
import org.joml.Vector3f;
import org.junit.Test;

public class PointUnitSphereSweepHandlerTest {

    @Test
    public void testSweep() throws Exception {
        SweepHandler<Point, UnitSphere> sweepHandler = new PointUnitSphereSweepHandler();
        Point point = new Point(0, 0, 0);
        UnitSphere unitSphere = new UnitSphere(new Vector3f(2, 0, 0));
        Vector3f velocity = new Vector3f(3, 0, 0);
        SweepResult result = new SweepResult();

        sweepHandler.sweep(result, point, velocity, unitSphere);

        System.out.println("test");

    }
}