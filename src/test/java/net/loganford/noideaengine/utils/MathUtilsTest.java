package net.loganford.noideaengine.utils;

import net.loganford.noideaengine.utils.math.MathUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class MathUtilsTest {
    @Test
    public void testDirection() {
        float epsilon = .0001f;
        assertEquals(0, MathUtils.direction(1, 0), epsilon);
        assertEquals(MathUtils.PI / 2, MathUtils.direction(0, -1), epsilon);
        assertEquals(MathUtils.PI, MathUtils.direction(-1, 0), epsilon);
        assertEquals(3 * MathUtils.PI/2, MathUtils.direction(0, 1), epsilon);

        assertEquals(MathUtils.PI / 2, MathUtils.direction(0, 0, 0, -1), epsilon);
        assertEquals(MathUtils.PI, MathUtils.direction(0, 0, -1, 0), epsilon);
    }

}