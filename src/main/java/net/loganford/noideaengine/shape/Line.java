package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Line extends Shape {
    @Getter @Setter private float x1;
    @Getter @Setter private float y1;
    @Getter @Setter private float z1;
    @Getter @Setter private float x2;
    @Getter @Setter private float y2;
    @Getter @Setter private float z2;

    private Vector3f beginning = new Vector3f();
    private Vector3f ending = new Vector3f();

    public Line(float x1, float y1, float x2, float y2) {
        super();
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Line(Vector3f begin, Vector3f end) {
        super();

        this.x1 = begin.x;
        this.y1 = begin.y;
        this.z1 = begin.z;

        this.x2 = end.x;
        this.y2 = end.y;
        this.z2 = end.z;
    }

    @Override
    public void setPosition(Vector3f position) {
        float diffX = position.x - x1;
        float diffY = position.y - y1;
        x1 += diffX;
        x2 += diffX;
        y1 += diffY;
        y2 += diffY;
    }

    public Vector3fc getBeginning() {
        return beginning.set(x1, y1, z1);
    }

    public Vector3fc getEnding() {
        return ending.set(x2, y2, z2);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.x = x1;
        position.y = y1;
        position.z = z1;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(Math.min(x1, x2));
        rect.setY(Math.min(y1, y2));
        rect.setWidth(Math.abs(x1 - x2));
        rect.setHeight(Math.abs(y1 - y2));
    }
}
