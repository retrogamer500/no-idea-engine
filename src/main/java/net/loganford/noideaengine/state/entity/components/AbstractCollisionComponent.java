package net.loganford.noideaengine.state.entity.components;

import lombok.Getter;
import net.loganford.noideaengine.shape.Shape;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.signals.AfterMotionSignal;
import net.loganford.noideaengine.utils.messaging.Listener;
import net.loganford.noideaengine.utils.messaging.Signal;

public abstract class AbstractCollisionComponent extends Component implements Listener<Entity> {
    @Getter
    private Shape shape;
    @Getter private float shapeOffsetX = 0;
    @Getter private float shapeOffsetY = 0;
    @Getter private float shapeOffsetZ = 0;

    public AbstractCollisionComponent(String[] args) {
        super(args);
    }

    @Override
    public void componentAdded(Entity entity) {
        super.componentAdded(entity);
        entity.getAfterMotionSignal().subscribe(this);
    }

    public void setShape(Shape shape) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        if(this.shape != null) {
            shape.setOwningEntity(null);
        }
        this.shape = shape;
        if(this.shape != null) {
            shape.setOwningEntity(getEntity());
        }
        getEntity().getAfterMotionSignal().dispatch(getEntity());
    }

    public void setShapeOffsetX(float x) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.shapeOffsetX = x;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
    }

    public void setShapeOffsetY(float y) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.shapeOffsetY = y;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
    }

    public void setShapeOffsetZ(float z) {
        getEntity().getBeforeMotionSignal().dispatch(getEntity());
        this.shapeOffsetZ = z;
        getEntity().getAfterMotionSignal().dispatch(getEntity());
    }

    @Override
    public void receive(Signal<Entity> signal, Entity entity) {
        if(signal instanceof AfterMotionSignal) {
            if(shape != null) {
                shape.setPosition(entity.getX() + shapeOffsetX, entity.getY() + shapeOffsetY, entity.getZ() + shapeOffsetZ);
            }
        }
    }

    @Override
    public void componentRemoved() {
        super.componentRemoved();
        //Set shape to null so that any listeners are informed that the component is being removed from the entity
        setShape(null);
    }
}
