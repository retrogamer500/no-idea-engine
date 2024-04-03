package net.loganford.noideaengine.utils.math;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {
    @Test
    public void testInterpolate() {
        float[] input = {0, 1, 2, 3, 4};
        float[] output = {0, 10, 20, 30, 40};

        Assert.assertEquals(20, MathUtils.interpolate(2, input, output), MathUtils.EPSILON);
        Assert.assertEquals(-10, MathUtils.interpolate(-1, input, output), MathUtils.EPSILON);
        Assert.assertEquals(15, MathUtils.interpolate(1.5f, input, output), MathUtils.EPSILON);
    }
}