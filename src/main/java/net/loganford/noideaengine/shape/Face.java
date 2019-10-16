package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Face extends Shape {

    private static Vector3f V3F = new Vector3f();

    @Setter @Getter private Vector3f v0;
    @Setter @Getter private Vector3f v1;
    @Setter @Getter private Vector3f v2;

    public Face(Vector3fc v0, Vector3fc v1, Vector3fc v2) {
        this.v0.set(v0);
        this.v1.set(v1);
        this.v2.set(v2);
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
        v2.add(difference);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(NumberUtils.min(v0.x, v1.x, v2.x));
        cube.setY(NumberUtils.min(v0.y, v1.y, v2.y));
        cube.setZ(NumberUtils.min(v0.z, v1.z, v2.z));

        cube.setWidth(NumberUtils.max(v0.x, v1.x, v2.x) - cube.getX());
        cube.setHeight(NumberUtils.max(v0.y, v1.y, v2.y) - cube.getY());
        cube.setDepth(NumberUtils.max(v0.z, v1.z, v2.z) - cube.getZ());
    }
}
