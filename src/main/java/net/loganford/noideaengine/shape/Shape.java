package net.loganford.noideaengine.shape;

import lombok.Getter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class Shape {
    private static SweepResult SWEEP_RESULT = new SweepResult();
    private static Vector3f V3F = new Vector3f();
    private static Rect RECT = new Rect(0, 0, 1, 1);

    @Getter private int registration;
    private ShapeIntersectionEngine shapeIntersectionEngine;

    public Shape() {
        shapeIntersectionEngine = ShapeIntersectionEngine.getInstance();
        registration = shapeIntersectionEngine.registerShape(getClass());
    }

    public Vector3f getPosition() {
        getPosition(V3F);
        return V3F;
    }

    public abstract void getPosition(Vector3f position);
    public abstract void setPosition(Vector3f position);


    public void setPosition(float x, float y, float z) {
        V3F.set(x, y, z);
        setPosition(V3F);
    }

    public void setPosition(float x, float y) {
        setPosition(x, y, 0);
    }





    /**
     * Sets rect to be a bounding box around the shape.
     * @param rect Rect which will be modified to be a bounding box.
     */
    public abstract void getBoundingBox(Rect rect);

    /**
     * Gets the bounding box for this shape. Since this allocates a new object, it is recommended that you call
     * getBoundingBox(Rect rect) instead.
     * @return the bounding box
     */
    public Rect getBoundingBox() {
        getBoundingBox(RECT);
        return RECT;
    }

    public boolean collidesWith(Shape other) {
        return shapeIntersectionEngine.collides(this, other);
    }

    public void sweep(SweepResult result, Vector3fc velocity, Shape b) {
        shapeIntersectionEngine.sweep(result, this, velocity, b);
    }

    public SweepResult sweep(Vector3fc velocity, Shape b) {
        SWEEP_RESULT.clear();
        sweep(SWEEP_RESULT, velocity, b);
        return SWEEP_RESULT;
    }
}
