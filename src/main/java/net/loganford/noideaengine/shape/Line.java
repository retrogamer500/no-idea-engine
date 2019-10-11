package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Line extends Shape {
    private static Vector3f V3F = new Vector3f();

    @Getter @Setter private Vector3f beginning = new Vector3f();
    @Getter @Setter private Vector3f ending = new Vector3f();

    public Line(float x1, float y1, float x2, float y2) {
        super();

        this.beginning.set(x1, y1, 0);
        this.ending.set(x2, y2, 0);
    }

    public Line(float x1, float y1, float z1, float x2, float y2, float z2) {
        super();

        this.beginning.set(x1, y1, z1);
        this.ending.set(x2, y2, z2);
    }

    public Line(Vector3f beginning, Vector3f ending) {
        super();

        this.beginning.set(beginning);
        this.ending.set(ending);
    }

    @Override
    public void setPosition(Vector3fc position) {
        Vector3f difference = V3F.set(beginning).sub(position);
        beginning.add(difference);
        ending.add(difference);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(beginning);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(Math.min(getX1(), getX2()));
        cube.setY(Math.min(getY1(), getY2()));
        cube.setZ(Math.min(getZ1(), getZ2()));
        cube.setWidth(Math.abs(getX1() - getX2()));
        cube.setHeight(Math.abs(getY1() - getY2()));
        cube.setDepth(Math.abs(getZ1() - getZ2()));
    }

    public float getX1() {return beginning.x;}
    public float getY1() {return beginning.y;}
    public float getZ1() {return beginning.z;}

    public float getX2() {return ending.x;}
    public float getY2() {return ending.y;}
    public float getZ2() {return ending.z;}

    public void setX1(float x1) {beginning.x = x1;}
    public void setY1(float y1) {beginning.y = y1;}
    public void setZ1(float z1) {beginning.z = z1;}

    public void setX2(float x2) {ending.x = x2;}
    public void setY2(float y2) {ending.y = y2;}
    public void setZ2(float z2) {ending.z = z2;}
}
