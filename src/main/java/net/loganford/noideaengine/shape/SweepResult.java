package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.scripting.Scriptable;
import org.joml.Vector3f;

public class SweepResult<E> {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();
    private static Vector3f V3F_3 = new Vector3f();
    private static Vector3f V3F_4 = new Vector3f();

    @Getter(onMethod = @__({@Scriptable})) @Setter private float distance = 1f;
    @Getter @Setter private Vector3f normal = new Vector3f();
    @Getter(onMethod = @__({@Scriptable})) @Setter private Shape shape = null;
    @Getter(onMethod = @__({@Scriptable})) @Setter private E entity = null;
    @Getter @Setter private Vector3f position = new Vector3f();
    @Getter @Setter private Vector3f velocity = new Vector3f();

    @Scriptable
    public boolean collides() {
        return shape != null;
    }

    public void clear() {
        setDistance(1f);
        getNormal().set(0, 0, 0);
        getPosition().set(0, 0, 0);
        getVelocity().set(0, 0, 0);
        setShape(null);
        setEntity(null);
    }

    @SuppressWarnings("unchecked")
    public void set(SweepResult other) {
        this.distance = other.distance;
        this.normal.set(other.normal);
        this.position.set(position);
        this.velocity.set(other.velocity);
        this.shape = other.shape;
        this.entity = (E) other.entity;
    }

    /**
     * Reflects a given vector by the resultant normal
     * @param vector the vector to reflect
     */
    @Scriptable
    public void reflect(Vector3f vector) {
        if(collides()) {
            float dotted = 2 * V3F.set(vector).dot(normal);
            V3F.set(vector).sub(V3F_3.set(normal).mul(dotted)).normalize();
            V3F.mul(vector.length());
            vector.set(V3F);
        }
    }

    /**
     * Sets vector to slide against the normal
     * @param vector the vector
     */
    @Scriptable
    public void slide(Vector3f vector) {
        if(collides()) {
            float dotted = Math.abs(V3F.set(normal).dot(vector));
            vector.set(V3F.set(normal).mul(dotted).add(vector));
        }
    }

    /**
     * Sets vector to be the remaining movement distance
     * @param vector the vector
     */
    @Scriptable
    public void remainder(Vector3f vector) {
        vector.set(velocity.mul(1 - distance));
    }
}
