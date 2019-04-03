package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.MathUtils;

public class Line extends Shape2D {
    @Getter @Setter private float x1;
    @Getter @Setter private float y1;
    @Getter @Setter private float x2;
    @Getter @Setter private float y2;

    public Line(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void setPosition(float x, float y) {
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

    public static boolean lineRectCollision(Line line, Rect rect) {
        float len = MathUtils.distance(line.x1, line.y1, line.x2, line.y2);

        float unitX = (line.x2 - line.x1)/len;
        float unitY = (line.y2 - line.y1)/len;
        float fx = 1f/unitX;
        float fy = 1f/unitY;

        float t1 = ((rect.getX()) - line.x1)*fx;
        float t2 = ((rect.getX() + rect.getWidth()) - line.x1)*fx;
        float t3 = ((rect.getY() + rect.getHeight()) - line.y1)*fy;
        float t4 = ((rect.getY()) - line.y1)*fy;

        float t5 = ((rect.getX()) - line.x2)*-fx;
        float t6 = ((rect.getX() + rect.getWidth()) - line.x2)*-fx;
        float t7 = ((rect.getY() + rect.getHeight()) - line.y2)*-fy;
        float t8 = ((rect.getY()) - line.y2)*-fy;

        float tMin1 = Math.max(Math.min(t1, t2), Math.min(t3, t4));
        float tMax1 = Math.min(Math.max(t1, t2), Math.max(t3, t4));
        float tMin2 = Math.max(Math.min(t5, t6), Math.min(t7, t8));
        float tMax2 = Math.min(Math.max(t5, t6), Math.max(t7, t8));

        if(tMax1 < 0 || tMax2 < 0)
            return false;
        if(tMin1 > tMax1 || tMin2 > tMax2)
            return false;
        return true;
    }

    public static boolean lineCircleCollision(Line line, Circle circle) {
        float a = line.x2-line.x1;
        float b = line.y2-line.y1;
        float c = circle.getX() - line.x1;
        float d = circle.getY() - line.y1;
        float r = circle.getRadius();

        if ((d*a-c*b)*(d*a-c*b) <= r*r*(a*a+b*b))
        {
            if (c*c + d*d <= r*r)
                return true;
            else if ((a-c)*(a-c)+(b-d)*(b-d) <= r*r)
                return true;
            else if(c*a+d*b>=0 && c*a+d*b <= a*a+b*b)
                return true;
        }
        return false;
    }

    public static boolean lineLineCollision(Line line1, Line line2) {
        if(ccw(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.x2) * ccw(line1.x1, line1.y1, line1.x2, line1.y2, line2.x1, line2.x2) > 0)
            return false;
        if(ccw(line2.x1, line2.y1, line2.x2, line2.y2, line1.x1, line1.x2) * ccw(line2.x1, line2.y1, line2.x2, line2.y2, line1.x1, line1.x2) > 0)
            return false;
        return true;
    }

    private static float ccw(float x1,float y1,float x2,float y2,float x3,float y3)
    {
        return (x2-x1)*(y3-y1)-(x3-x1)*(y2-y1);
    }
}
