package net.loganford.noideaengine.utils.math;

import net.loganford.noideaengine.state.lighting.PointLight;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilsTest {
    @Test
    public void testInterpolate() {
        float[] input = {0, 1, 2, 3, 4};
        float[] output = {0, 10, 20, 30, 40};

        Assert.assertEquals(20, MathUtils.interpolate(2, input, output), MathUtils.EPSILON);
        Assert.assertEquals(-10, MathUtils.interpolate(-1, input, output), MathUtils.EPSILON);
        Assert.assertEquals(15, MathUtils.interpolate(1.5f, input, output), MathUtils.EPSILON);

        System.out.println(MathUtils.interpolate(3f, PointLight.LIGHT_DISTANCE, PointLight.LINEAR_VALUE));
        System.out.println(MathUtils.interpolate(3f, PointLight.LIGHT_DISTANCE, PointLight.QUADRATIC_VALUE));
    }
}