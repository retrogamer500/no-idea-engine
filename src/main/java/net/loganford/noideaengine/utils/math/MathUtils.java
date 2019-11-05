package net.loganford.noideaengine.utils.math;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MathUtils {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3d V3D = new Vector3d();
    private static Vector3d V3D_2 = new Vector3d();

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

    public static float distanceSqr(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt(distanceSqr(x1, y1, x2, y2));
    }

    public static float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
        return (float)Math.sqrt(distanceSqr(x1, y1, z1, x2, y2, z2));
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

    /**
     * Breaks down a vector into normal and orthogonal components
     * @param vector a vector
     * @param normal a normal
     * @param normalComponent if not null, this is set to the normal component of the vector
     * @param orthogonalComponent if not null, this is set to the orthogonal component of the vector
     */
    public static void vectorComponents(Vector3fc vector, Vector3fc normal, Vector3f normalComponent, Vector3f orthogonalComponent) {
        Vector3f vectorProjNormal = V3F;
        vectorProjection(vector, normal, V3F);

        if(normalComponent != null) {
            normalComponent.set(vectorProjNormal);
        }

        if(orthogonalComponent != null) {
            orthogonalComponent.set(V3F_2.set(vector).sub(normalComponent));
        }
    }

    public static void vectorProjection(Vector3fc vector, Vector3fc onto, Vector3f result) {
        result.set(V3F.set(onto).mul(vector.dot(onto) / onto.lengthSquared()));
    }
}
