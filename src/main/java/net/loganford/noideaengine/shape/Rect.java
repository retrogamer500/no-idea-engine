package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.math.MathUtils;

public class Rect extends Shape {

    @Getter @Setter private float x;
    @Getter @Setter private float y;
    @Getter @Setter private float width;
    @Getter @Setter private float height;

    public Rect(float x, float y, float width, float height) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x; this.y = y;
    }

    @Override
    public void getBoundingBox(Rect rect) {
        rect.setX(x);
        rect.setY(y);
        rect.setWidth(width);
        rect.setHeight(height);
    }

    //Rectangle collision handlers
    static {
        ShapeIntersectionEngine.addHandler(Rect.class, Rect.class, (IntersectionHandler<Rect, Rect>) (rect1, rect2) -> {
            if(rect2.getX() + rect2.getWidth() < rect1.getX()) {
                return false;
            }
            if(rect2.getX() > rect1.getX() + rect1.getWidth()) {
                return false;
            }
            if(rect2.getY() + rect2.getHeight() < rect1.getY()) {
                return false;
            }
            if(rect2.getY() > rect1.getY() + rect1.getHeight()) {
                return false;
            }
            return true;
        });

        ShapeIntersectionEngine.addHandler(Rect.class, Circle.class, (IntersectionHandler<Rect, Circle>) (rect, circle) -> {
            float cDisX = Math.abs(circle.getX() - (rect.getX() + (rect.getWidth()/2)));
            float cDisY = Math.abs(circle.getY() - (rect.getY() + (rect.getHeight()/2)));

            float halfWidth = .5f * rect.getWidth();
            float halfHeight = .5f * rect.getHeight();

            if(cDisX > (halfWidth + circle.getRadius())) {
                return false;
            }
            if(cDisY > (halfHeight + circle.getRadius())) {
                return false;
            }

            if(cDisX <= halfWidth + circle.getRadius() && cDisY <= halfHeight) {
                return true;
            }

            if(cDisY <= halfHeight + circle.getRadius() && cDisX <= halfWidth) {
                return true;
            }

            float cornerDistance = (cDisX - halfWidth) * (cDisX - halfWidth) +
                    (cDisY - halfHeight) * (cDisY - halfHeight);

            return cornerDistance <= circle.getRadius() * circle.getRadius();
        });

        ShapeIntersectionEngine.addHandler(Rect.class, Point.class, (IntersectionHandler<Rect, Point>) (rect, point) -> {
            if(point.getX() < rect.getX()) {
                return false;
            }
            if(point.getX() > rect.getX() + rect.getWidth()) {
                return false;
            }
            if(point.getY() < rect.getY()) {
                return false;
            }
            if(point.getY() > rect.getY() + rect.getHeight()) {
                return false;
            }
            return true;
        });

        ShapeIntersectionEngine.addHandler(Rect.class, Line.class, (IntersectionHandler<Rect, Line>) (rect, line) -> {
            float len = MathUtils.distance(line.getX1(), line.getY1(), line.getX2(), line.getY2());

            float unitX = (line.getX2() - line.getX1())/len;
            float unitY = (line.getY2() - line.getX1())/len;
            float fx = 1f/unitX;
            float fy = 1f/unitY;

            float t1 = ((rect.getX()) - line.getX1())*fx;
            float t2 = ((rect.getX() + rect.getWidth()) - line.getX1())*fx;
            float t3 = ((rect.getY() + rect.getHeight()) - line.getY1())*fy;
            float t4 = ((rect.getY()) - line.getY1())*fy;

            float t5 = ((rect.getX()) - line.getX2())*-fx;
            float t6 = ((rect.getX() + rect.getWidth()) - line.getX2())*-fx;
            float t7 = ((rect.getY() + rect.getHeight()) - line.getY2())*-fy;
            float t8 = ((rect.getY()) - line.getY2())*-fy;

            float tMin1 = Math.max(Math.min(t1, t2), Math.min(t3, t4));
            float tMax1 = Math.min(Math.max(t1, t2), Math.max(t3, t4));
            float tMin2 = Math.max(Math.min(t5, t6), Math.min(t7, t8));
            float tMax2 = Math.min(Math.max(t5, t6), Math.max(t7, t8));

            if(tMax1 < 0 || tMax2 < 0)
                return false;
            if(tMin1 > tMax1 || tMin2 > tMax2)
                return false;
            return true;
        });
    }
}
