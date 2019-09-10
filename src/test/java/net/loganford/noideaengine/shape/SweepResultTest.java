package net.loganford.noideaengine.shape;

import org.joml.Vector3f;
import org.junit.Test;

public class SweepResultTest {
    @Test
    public void testSweepResult() {
        Rect rect1 = new Rect(0, 0, 1, 1);
        Point point = new Point(-.5f, .5f);
        SweepResult result = point.sweep(new Vector3f(1f, 0f, 0f), rect1);

        Rect rect2 = new Rect(2, 0, 1, 1);
        SweepResult result2 = rect1.sweep(new Vector3f(2f, 0f, 0f), rect2);

        System.out.println("test");
    }
}