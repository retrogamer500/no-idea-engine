package net.loganford.noideaengine.state.entity.components;

public class BasicPositionComponent extends AbstractPositionComponent {
    private float x;
    private float y;
    private float z;

    @Override
    public void setX(float x) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.x = x;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
        //Todo: refactor this to use a new CollisionMask class and use a listener?
        if(getEntity().getShape() != null) {
            getEntity().getShape().setPosition(x + getEntity().getShapeOffsetX(), y + getEntity().getShapeOffsetY());
        }
    }

    @Override
    public void setY(float y) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.y = y;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
        if(getEntity().getShape() != null) {
            getEntity().getShape().setPosition(x + getEntity().getShapeOffsetX(), y + getEntity().getShapeOffsetY());
        }

    }

    @Override
    public void setZ(float z) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.z = z;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
        if(getEntity().getShape() != null) {
            getEntity().getShape().setPosition(x + getEntity().getShapeOffsetX(), y + getEntity().getShapeOffsetY());
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public void setPos(float x, float y) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.x = x;
        this.y = y;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
        if(getEntity().getShape() != null) {
            getEntity().getShape().setPosition(x + getEntity().getShapeOffsetX(), y + getEntity().getShapeOffsetY());
        }
    }

    @Override
    public void setPos(float x, float y, float z) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.x = x;
        this.y = y;
        this.z = z;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
        if(getEntity().getShape() != null) {
            getEntity().getShape().setPosition(x + getEntity().getShapeOffsetX(), y + getEntity().getShapeOffsetY());
        }
    }
}
