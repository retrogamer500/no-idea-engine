package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.math.MathUtils;
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
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
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

    //Circle collision handlers
    static {
        ShapeIntersectionEngine.getInstance().addIntersectionHandler(Circle.class, Circle.class, (circle1, circle2) ->
                MathUtils.distanceSqr(circle1.getX(), circle1.getY(), circle2.getX(), circle2.getY()) <=
                (circle1.getRadius() + circle2.getRadius()) * (circle1.getRadius() + circle2.getRadius())
        );

        ShapeIntersectionEngine.getInstance().addIntersectionHandler(Circle.class, Line.class, (circle, line) -> {
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
        });
    }
}
