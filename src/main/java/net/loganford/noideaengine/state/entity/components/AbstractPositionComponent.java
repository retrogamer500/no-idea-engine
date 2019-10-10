package net.loganford.noideaengine.state.entity.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class AbstractPositionComponent extends Component {
    private static Vector3f V3F = new Vector3f();

    public abstract void setX(float x);
    public abstract void setY(float y);
    public abstract void setZ(float z);

    public abstract float getX();
    public abstract float getY();
    public abstract float getZ();

    public abstract void setPos(float x, float y);
    public abstract void setPos(float x, float y, float z);

    public abstract Matrix4f getMatrix();

    public void move(float x, float y) {
        setPos(getX() + x, getY() + y);
    }

    public void move(float x, float y, float z) {
        setPos(getX() + x, getY() + y, getZ() + z);
    }

    public Vector3fc getPos() {
        return V3F.set(getX(), getY(), getZ());
    }

    public void setPos(Vector3fc v) {
        setPos(v.x(), v.y(), v.z());
    }
}
