package net.loganford.noideaengine.shape;

public abstract class Shape2D {
    public abstract void setPosition(float x, float y);

    /**
     * Sets rect to be a bounding box around the shape.
     * @param rect Rect which will be modified to be a bounding box.
     */
    public abstract void getBoundingBox(Rect rect);

    /**
     * Gets the bounding box for this shape. Since this allocates a new object, it is recommended that you call
     * getBoundingBox(Rect rect) instead.
     * @return the bounding box
     */
    public Rect getBoundingBox() {
        Rect rect = new Rect(0f, 0f, 1f, 1f);
        getBoundingBox(rect);
        return rect;
    }

    public boolean collidesWith(Shape2D other) {
        if(this instanceof Point2D) {
            if(other instanceof Point2D) {
                return Point2D.pointPointCollision((Point2D) this, (Point2D) other);
            }
            else if(other instanceof Rect) {
                return Rect.rectPointCollision((Rect) other, (Point2D) this);
            }
            else if(other instanceof Circle) {
                return Circle.circlePointCollision((Circle) other, (Point2D) this);
            }
        }
        else if(this instanceof Rect) {
            if(other instanceof Point2D) {
                return Rect.rectPointCollision((Rect) this, (Point2D) other);
            }
            else if(other instanceof Rect) {
                return Rect.rectRectCollision((Rect) this, (Rect) other);
            }
            else if(other instanceof Circle) {
                return Circle.circleRectCollision((Circle) other, (Rect) this);
            }
            else if(other instanceof Line) {
                return Line.lineRectCollision((Line) other, (Rect) this);
            }
        }
        else if(this instanceof Circle) {
            if(other instanceof Point2D) {
                return Circle.circlePointCollision((Circle) this, (Point2D) other);
            }
            else if(other instanceof Rect) {
                return Circle.circleRectCollision((Circle) this, (Rect) other);
            }
            else if(other instanceof Circle) {
                return Circle.circleCircleCollision((Circle) this, (Circle) other);
            }
            else if(other instanceof Line) {
                return Line.lineCircleCollision((Line) other, (Circle) this);
            }
        }
        else if(this instanceof Line) {
            if(other instanceof Rect) {
                return Line.lineRectCollision((Line) this, (Rect) other);
            }
            else if(other instanceof Circle) {
                return Line.lineCircleCollision((Line) this, (Circle) other);
            }
            else if(other instanceof Line) {
                return Line.lineLineCollision((Line) this, (Line) other);
            }
        }

        return false;
    }
}
