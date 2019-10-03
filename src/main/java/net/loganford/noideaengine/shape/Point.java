package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class Point extends Shape {
    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float z;

    public Point(float x, float y) {
        this(x, y, 0);
    }

    public Point(float x, float y, float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(Vector3f position) {
        this(position.x, position.y, position.z);
    }

    @Override
    public void setPosition(Vector3f position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
    }

    @Override
    public void getPosition(Vector3f position) {
        position.x = x;
        position.y = y;
        position.z = 0;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(0);
        rect.setHeight(0);
    }
}
