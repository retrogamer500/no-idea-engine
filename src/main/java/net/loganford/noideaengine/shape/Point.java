package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class Point extends Shape {
    @Getter @Setter private Vector3f position = new Vector3f();

    public Point(float x, float y) {
        this(x, y, 0);
    }

    public Point(float x, float y, float z) {
        super();

        position.set(x, y, z);
    }

    public Point(Vector3f position) {
        this(position.x, position.y, position.z);
    }

    @Override
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(this.position);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(getX());
        cube.setY(getY());
        cube.setZ(getZ());
        cube.setWidth(0);
        cube.setHeight(0);
        cube.setDepth(0);
    }

    public float getX() {return position.x;}
    public float getY() {return position.y;}
    public float getZ() {return position.z;}

    public void setX(float x) {position.x = x;}
    public void setY(float y) {position.y = y;}
    public void setZ(float z) {position.z = z;}
}
