package net.loganford.noideaengine.shape.intersectionHandlers;

import net.loganford.noideaengine.shape.Circle;
import net.loganford.noideaengine.shape.Line2D;

public class CircleLineIntersectionHandler implements IntersectionHandler<Circle, Line2D> {
    @Override
    public boolean intersects(Circle circle, Line2D line) {
        float a = line.getX2()-line.getX1();
        float b = line.getY2()-line.getY1();
        float c = circle.getX() - line.getX1();
        float d = circle.getY() - line.getY1();
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
}
