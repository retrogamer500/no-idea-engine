package net.loganford.noideaengine.utils.math;

public class MathUtils {
    public static float EPSILON = .001f;
    public static float PI = (float)Math.PI;

    public static float randRangeF(float f0, float f1) {
        return Math.min(f0, f1) + (float)(Math.abs(f1 - f0) * Math.random());
    }

    public static int randRangeI(int i0, int i1) {
        return Math.min(i0, i1) + (int)((Math.abs(i0 - i1) * Math.random()));
    }

    public static float distanceSqr(float x1, float y1, float x2, float y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt(distanceSqr(x1, y1, x2, y2));
    }

    public static float direction(float x, float y) {
        float result = (float)Math.atan2(-y, x);
        return result >= 0 ? result : result + 2 * PI;
    }

    public static float direction(float x1, float y1, float x2, float y2) {
        return direction(x2 - x1, y2 - y1);
    }

    public static int sign(float n)
    {
        return n>0?1:(n<0?-1:0);
    }

    public static int clamp(int low, int high, int value) {
        return Math.min(Math.max(value, low), high);
    }

    public static float clamp(float low, float high, float value) {
        return Math.min(Math.max(value, low), high);
    }

    public static float lerp(float a, float b, float amount) {
        float diff = b - a;
        return a + diff * amount;
    }
}
