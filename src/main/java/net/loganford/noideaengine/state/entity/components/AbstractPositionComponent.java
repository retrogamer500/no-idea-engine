package net.loganford.noideaengine.state.entity.components;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class AbstractPositionComponent extends Component {
    private static Vector2f V2F = new Vector2f();
    private static Vector3f V3F = new Vector3f();

    public abstract void setX(float x);
    public abstract void setY(float y);
    public abstract void setZ(float z);

    public abstract float getX();
    public abstract float getY();
    public abstract float getZ();

    public abstract void setPos(float x, float y);
    public abstract void setPos(float x, float y, float z);

    public void move(float x, float y) {
        setPos(getX() + x, getY() + y);
    }

    public void move(float x, float y, float z) {
        setPos(getX() + x, getY() + y, getZ() + z);
    }

    public Vector2fc getPos2() {
        return V2F.set(getX(), getY());
    }
    public Vector3fc getPos3() {
        return V3F.set(getX(), getY(), getZ());
    }

    public void setPos(Vector2fc v) {
        setPos(v.x(), v.y());
    }

    public void setPos(Vector3fc v) {
        setPos(v.x(), v.y(), v.z());
    }
}
