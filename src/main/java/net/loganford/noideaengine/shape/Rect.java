package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Rect extends Shape {

    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float width;
    @Getter @Setter private float height;

    public Rect(float x, float y, float width, float height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
        cube.setX(x);
        cube.setY(y);
        cube.setZ(0);
        cube.setWidth(width);
        cube.setHeight(height);
        cube.setDepth(0);
    }
}
