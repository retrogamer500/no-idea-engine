package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Cylinder extends Shape {

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();

    @Setter @Getter private Vector3f p1;
    @Setter @Getter private Vector3f p2;
    @Setter @Getter private float radius;

    @Override
    public void getPosition(Vector3f position) {
        position.set(p1);
    }

    @Override
    public void setPosition(Vector3fc position) {
        Vector3f difference = V3F.set(p1).sub(position);
        p1.add(difference);
        p2.add(difference);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        Vector3f center = V3F.set(p1).add(p2).mul(.5f);
        float halfHeightSquared = V3F_1.set(p1).sub(p2).absolute().mul(.5f).lengthSquared();
        float radiusSquared = radius * radius;
        float enclosingCircleRadius = (float) Math.sqrt(radiusSquared);

        Vector3f cubePos = center.add(enclosingCircleRadius, enclosingCircleRadius, enclosingCircleRadius);
        Vector3f cubeSize = V3F_1.set(2 * enclosingCircleRadius);

        cube.setPosition(cubePos);
        cube.setSize(cubeSize);
    }
}
