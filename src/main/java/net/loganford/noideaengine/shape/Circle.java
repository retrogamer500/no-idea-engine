package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Circle extends Shape {
    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float radius;

    public Circle(float x, float y, float radius) {
        super();
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public void setPosition(Vector3fc position) {
        this.x = position.x();
        this.y = position.y();
    }

    @Override
    public void getPosition(Vector3f position) {
        position.x = x;
        position.y = y;
        position.z = 0;
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(x - radius);
        cube.setY(y - radius);
        cube.setZ(0);
        cube.setWidth(2 * radius);
        cube.setHeight(2 * radius);
        cube.setDepth(0);
    }
}
