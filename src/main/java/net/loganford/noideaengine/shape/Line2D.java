package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class Line2D extends Shape {
    @Getter @Setter private float x1;
    @Getter @Setter private float y1;
    @Getter @Setter private float x2;
    @Getter @Setter private float y2;

    public Line2D(float x1, float y1, float x2, float y2) {
        super();
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
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

    @Override
    public void getPosition(Vector3f position) {
        position.x = x1;
        position.y = y1;
        position.z = 0;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(Math.min(x1, x2));
        rect.setY(Math.min(y1, y2));
        rect.setWidth(Math.abs(x1 - x2));
        rect.setHeight(Math.abs(y1 - y2));
    }
}
