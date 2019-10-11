package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Face extends Shape {

    private static Vector3f V3F = new Vector3f();

    @Setter @Getter private Vector3f p1;
    @Setter @Getter private Vector3f p2;
    @Setter @Getter private Vector3f p3;

    public Face(Vector3fc p1, Vector3fc p2, Vector3fc p3) {
        this.p1.set(p1);
        this.p2.set(p2);
        this.p3.set(p3);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(p1);
    }

    @Override
    public void setPosition(Vector3fc position) {
        Vector3f difference = V3F.set(p1).sub(position);
        p1.add(difference);
        p2.add(difference);
        p3.add(difference);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(NumberUtils.min(p1.x, p2.x, p3.x));
        cube.setY(NumberUtils.min(p1.y, p2.y, p3.y));
        cube.setZ(NumberUtils.min(p1.z, p2.z, p3.z));

        cube.setWidth(NumberUtils.max(p1.x, p2.x, p3.x) - cube.getX());
        cube.setHeight(NumberUtils.max(p1.y, p2.y, p3.y) - cube.getY());
        cube.setDepth(NumberUtils.max(p1.z, p2.z, p3.z) - cube.getZ());
    }
}
