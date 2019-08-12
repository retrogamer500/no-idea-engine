package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;

public class Line extends Shape {
    @Getter @Setter private float x1;
    @Getter @Setter private float y1;
    @Getter @Setter private float x2;
    @Getter @Setter private float y2;

    public Line(float x1, float y1, float x2, float y2) {
        super();
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        float diffX = x - x1;
        float diffY = y - y1;
        x1 += diffX;
        x2 += diffX;
        y1 += diffY;
        y2 += diffY;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(Math.min(x1, x2));
        rect.setY(Math.min(y1, y2));
        rect.setWidth(Math.abs(x1 - x2));
        rect.setHeight(Math.abs(y1 - y2));
    }

    //Line collision handlers
    static {
        ShapeIntersectionEngine.addHandler(Line.class, Line.class, (IntersectionHandler<Line, Line>) (line1, line2) -> {
            if(ccw(line1.getX1(), line1.getY1(), line1.getX2(), line1.getY2(), line2.getX1(), line2.getX2()) * ccw(line1.getX1(), line1.getY1(), line1.getX2(), line1.getY2(), line2.getX1(), line2.getX2()) > 0)
                return false;
            if(ccw(line2.getX1(), line2.getY1(), line2.getX2(), line2.getY2(), line1.getX1(), line1.getX2()) * ccw(line2.getX1(), line2.getY1(), line2.getX2(), line2.getY2(), line1.getX1(), line1.getX2()) > 0)
                return false;
            return true;
        });
    }

    private static float ccw(float x1,float y1,float x2,float y2,float x3,float y3) {
        return (x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
    }
}
