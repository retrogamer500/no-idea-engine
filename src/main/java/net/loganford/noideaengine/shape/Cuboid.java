package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Cuboid extends Shape {

    private Vector3f pos = new Vector3f();
    @Getter @Setter private Vector3f size = new Vector3f();

    public Cuboid(Vector3fc position, Vector3fc size) {
        super();
        this.pos.set(position);
        this.size.set(size);
    }

    public Cuboid(float x, float y, float z, float width, float height, float depth) {
        super();

        pos.set(x, y, z);
        size.set(width, height, depth);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(this.pos);
    }

    @Override
    public void setPosition(Vector3fc position) {
        this.pos.set(position);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(pos.x);
        cube.setY(pos.y);
        cube.setZ(pos.z);
        cube.setWidth(size.x);
        cube.setHeight(size.y);
        cube.setDepth(size.z);
    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public float getZ() {
        return pos.z;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public float getDepth() {
        return size.z;
    }

    public void setX(float x) {
        pos.x = x;
    }

    public void setY(float y) {
        pos.y = y;
    }

    public void setZ(float z) {
        pos.z = z;
    }

    public void setWidth(float width) {
        size.x = width;
    }

    public void setHeight(float height) {
        size.y = height;
    }

    public void setDepth(float depth) {
        size.z = depth;
    }

    public void set(Cuboid other) {
        setX(other.getX());
        setY(other.getY());
        setZ(other.getZ());

        setWidth(other.getWidth());
        setHeight(other.getHeight());
        setDepth(other.getDepth());
    }

    public void expand(Cuboid other) {
        setX(Math.min(getX(), other.getX()));
        setY(Math.min(getY(), other.getY()));
        setZ(Math.min(getZ(), other.getZ()));

        setWidth(Math.max(getX() + getWidth(), other.getX() + other.getWidth()) - getX());
        setHeight(Math.max(getY() + getHeight(), other.getY() + other.getHeight()) - getY());
        setDepth(Math.max(getZ() + getDepth(), other.getZ() + other.getDepth()) - getZ());
    }
}
