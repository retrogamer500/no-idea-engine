package net.loganford.noideaengine.state.entity.components;

public abstract class AbstractPositionComponent extends Component {
    public abstract void setX(float x);
    public abstract void setY(float y);
    public abstract void setZ(float z);

    public abstract float getX();
    public abstract float getY();
    public abstract float getZ();

    public abstract void setPos(float x, float y);
    public abstract void setPos(float x, float y, float z);

    public void move(float x, float y) {
        setPos(getX() + x, getY() + y);
    }

    public void move(float x, float y, float z) {
        setPos(getX() + x, getY() + y, getZ() + z);
    }
}
