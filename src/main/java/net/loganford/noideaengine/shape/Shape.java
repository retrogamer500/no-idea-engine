package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.entity.Entity;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class Shape {
    private static SweepResult SWEEP_RESULT = new SweepResult();
    private static Vector3f V3F = new Vector3f();
    private static Cuboid CUBE = new Cuboid(0, 0, 0, 1, 1, 1);
    @Getter @Setter private Entity owningEntity;

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

    public void setPosition(float x, float y, float z) {
        V3F.set(x, y, z);
        setPosition(V3F);
    }

    public void setPosition(float x, float y) {
        setPosition(x, y, 0);
    }

    public abstract void getPosition(Vector3f position);
    public abstract void setPosition(Vector3fc position);

    /**
     * Sets cuboid to be a bounding box around the shape.
     * @param cube Cuboid which will be modified to be a bounding box.
     */
    public abstract void getBoundingBox(Cuboid cube);

    /**
     * Gets the bounding box for this shape. The return value is shared across all calls.
     * @return the bounding box
     */
    public Cuboid getBoundingBox() {
        getBoundingBox(CUBE);
        return CUBE;
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
