package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Ellipsoid extends Shape {
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_2 = new Vector3f();

    private Vector3f pos = new Vector3f();
    @Getter @Setter private Vector3f radius = new Vector3f();

    public Ellipsoid(Vector3fc position, Vector3fc radius) {
        pos.set(position);
        this.radius.set(radius);
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
        V3F.set(pos).sub(radius);
        V3F_2.set(radius).mul(2);
        cube.setPosition(V3F);
        cube.setSize(V3F_2);
    }
}
