package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Line;

public class LineLineIntersectionHandler implements IntersectionHandler<Line, Line> {
    @Override
    public boolean intersects(Line line1, Line line2) {
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
    }
}
