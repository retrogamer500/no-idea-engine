package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.scripting.Scriptable;
import net.loganford.noideaengine.state.entity.Entity;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

public class SweepResult<E extends Entity> {
    private static Vector2f V2F = new Vector2f();

    @Getter(onMethod = @__({@Scriptable})) @Setter private float distance = 1f;
    @Getter @Setter private Vector3f normal3 = new Vector3f();
    @Getter(onMethod = @__({@Scriptable})) @Setter private Shape shape = null;
    @Getter(onMethod = @__({@Scriptable})) @Setter private E entity = null;
    @Getter @Setter private Vector3f position = new Vector3f();
    @Getter @Setter private Vector3f velocity = new Vector3f();

    @Scriptable
    public boolean collides() {
        return shape != null;
    }
    public Vector2fc getNormal2() {
        return V2F.set(normal3.x, normal3.y);
    }

    public void clear() {
        setDistance(1f);
        getNormal3().set(0, 0, 0);
        getPosition().set(0, 0, 0);
        getVelocity().set(0, 0, 0);
        setShape(null);
        setEntity(null);
    }

    @SuppressWarnings("unchecked")
    public void set(SweepResult other) {
        this.distance = other.distance;
        this.normal3.set(other.normal3);
        this.position.set(position);
        this.velocity.set(other.velocity);
        this.shape = other.shape;
        this.entity = (E) other.entity;
    }
}
