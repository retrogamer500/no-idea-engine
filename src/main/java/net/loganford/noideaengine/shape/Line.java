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
        ShapeIntersectionEngine.getInstance().addIntersectionHandler(Line.class, Line.class, (line1, line2) -> {
            float s1x = line1.getX2() - line1.getX1();
            float s1y = line1.getY2() - line1.getY1();

            float s2x = line2.getX2() - line2.getX1();
            float s2y = line2.getY2() - line2.getY1();

            float d = (-s2x * s1y + s1x * s2y);

            if(d != 0) {
                float dInv = 1 / d;
                float s = (-s1y * (line1.getX1() - line2.getX1()) + s1x * (line1.getY1() - line2.getY1())) * dInv;
                float t = (s2x * (line1.getY1() - line2.getY1()) - s2y * (line1.getX1() - line2.getX1())) * dInv;

                if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
                    return true;
                }
            }
            else {
                //Lines are collinear. We can approximate the lines as rectangles and attempt intersection test
                float x1 = s1x > 0 ? line1.getX1() : line1.getX2();
                float y1 = s1y > 0 ? line1.getY1() : line1.getY2();
                float width1 = Math.abs(s1x);
                float height1 = Math.abs(s1y);
                float x2 = s2x > 0 ? line2.getX1() : line2.getX2();
                float y2 = s1y > 0 ? line1.getY1() : line1.getY2();
                float width2 = Math.abs(s2x);
                float height2 = Math.abs(s2y);

                if(x2 + width2 < x1 || x2 > x1 + width1
                        || y2 + height2 < y1 || y2 > y1 + height1) {
                    return false;
                }
                return true;
            }

            return false;
        });
    }
}
