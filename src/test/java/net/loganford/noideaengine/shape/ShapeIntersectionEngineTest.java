package net.loganford.noideaengine.shape;

import org.junit.Assert;
import org.junit.Test;

public class ShapeIntersectionEngineTest {
    @Test
    public void testShapeIntersectionEngine() {
        Assert.assertFalse(new  Line(402.39148f, -261.60147f, 161.61887f, -82.89295f).collidesWith(new Rect(256f, -320f, 64f, 64f)));
        Assert.assertTrue(new  Line(-1, 5, 11, 5).collidesWith(new Rect(0, 0, 10, 10)));
        Assert.assertTrue(new  Line(5, 11, 11, 5).collidesWith(new Rect(0, 0, 10, 10)));
        Assert.assertFalse(new  Line(5, 11, 1100, 5).collidesWith(new Rect(0, 0, 10, 10)));
    }
}