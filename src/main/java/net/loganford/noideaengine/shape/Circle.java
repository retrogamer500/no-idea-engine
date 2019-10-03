package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

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
    public void setPosition(Vector3f position) {
        this.x = position.x;
        this.y = position.y;
    }

    @Override
    public void getPosition(Vector3f position) {
        position.x = x;
        position.y = y;
        position.z = 0;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x - radius);
        rect.setY(y - radius);
        rect.setWidth(2 * radius);
        rect.setHeight(2 * radius);
    }
}
