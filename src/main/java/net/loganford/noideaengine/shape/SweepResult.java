package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.state.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class SweepResult<E extends Entity> {
    private static Vector2f V2F = new Vector2f();

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
}
