package net.loganford.noideaengine.shape;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class UnitSphere extends Shape {

    private Vector3f pos = new Vector3f();

    public UnitSphere(Vector3fc pos) {
        this.pos.set(pos);
    }

    public UnitSphere(float x, float y, float z) {
        this.pos.set(x, y, z);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(pos);
    }

    @Override
    public void setPosition(Vector3fc position) {
        pos.set(position);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(pos.x - 1);
        cube.setY(pos.y - 1);
        cube.setZ(pos.z - 1);

        cube.setWidth(2);
        cube.setHeight(2);
        cube.setDepth(2);
    }
}
