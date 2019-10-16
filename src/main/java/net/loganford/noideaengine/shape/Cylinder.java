package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Cylinder extends Shape {

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();

    @Setter @Getter private Vector3f v0;
    @Setter @Getter private Vector3f v1;

    public Cylinder(Vector3f v0, Vector3f v1) {
        this.v0.set(v0);
        this.v1.set(v1);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(v0);
    }

    @Override
    public void setPosition(Vector3fc position) {
        Vector3f difference = V3F.set(v0).sub(position);
        v0.add(difference);
        v1.add(difference);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        Vector3f center = V3F.set(v0).add(v1).mul(.5f);
        float enclosingCircleRadius = (float) Math.sqrt(1);

        Vector3f cubePos = center.add(enclosingCircleRadius, enclosingCircleRadius, enclosingCircleRadius);
        Vector3f cubeSize = V3F_1.set(2 * enclosingCircleRadius);

        cube.setPosition(cubePos);
        cube.setSize(cubeSize);
    }
}
