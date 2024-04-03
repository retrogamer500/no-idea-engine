package net.loganford.noideaengine.shape;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Cuboid extends Shape {

    private Vector3f pos = new Vector3f();
    @Getter @Setter private Vector3f size = new Vector3f();

    public Cuboid(Vector3fc position, Vector3fc size) {
        super();
        this.pos.set(position);
        this.size.set(size);
    }

    public Cuboid(float x, float y, float z, float width, float height, float depth) {
        super();

        pos.set(x, y, z);
        size.set(width, height, depth);
    }

    @Override
    public void getPosition(Vector3f position) {
        position.set(this.pos);
    }

    @Override
    public void setPosition(Vector3fc position) {
        this.pos.set(position);
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        cube.setX(pos.x);
        cube.setY(pos.y);
        cube.setZ(pos.z);
        cube.setWidth(size.x);
        cube.setHeight(size.y);
        cube.setDepth(size.z);
    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public float getZ() {
        return pos.z;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public float getDepth() {
        return size.z;
    }

    public void setX(float x) {
        pos.x = x;
    }

    public void setY(float y) {
        pos.y = y;
    }

    public void setZ(float z) {
        pos.z = z;
    }

    public void setWidth(float width) {
        size.x = width;
    }

    public void setHeight(float height) {
        size.y = height;
    }

    public void setDepth(float depth) {
        size.z = depth;
    }

    public void set(Cuboid other) {
        setX(other.getX());
        setY(other.getY());
        setZ(other.getZ());

        setWidth(other.getWidth());
        setHeight(other.getHeight());
        setDepth(other.getDepth());
    }

    public void expand(Cuboid other) {
        setX(Math.min(getX(), other.getX()));
        setY(Math.min(getY(), other.getY()));
        setZ(Math.min(getZ(), other.getZ()));

        setWidth(Math.max(getX() + getWidth(), other.getX() + other.getWidth()) - getX());
        setHeight(Math.max(getY() + getHeight(), other.getY() + other.getHeight()) - getY());
        setDepth(Math.max(getZ() + getDepth(), other.getZ() + other.getDepth()) - getZ());
    }

    public boolean fullyContains(Cuboid other) {
        if(getX() > other.getX()) {
            return false;
        }
        if(getY() > other.getY()) {
            return false;
        }
        if(getZ() > other.getZ()) {
            return false;
        }
        if(getX() + getWidth() < other.getX() + other.getWidth()) {
            return false;
        }
        if(getY() + getHeight() < other.getY() + other.getHeight()) {
            return false;
        }
        if(getZ() + getDepth() < other.getZ() + other.getDepth()) {
            return false;
        }

        return true;
    }
    
    public boolean cuboidCollision(Cuboid other) {
        if(other.getX() + other.getWidth() < this.getX()) {
            return false;
        }
        if(other.getX() > this.getX() + this.getWidth()) {
            return false;
        }
        if(other.getY() + other.getHeight() < this.getY()) {
            return false;
        }
        if(other.getY() > this.getY() + this.getHeight()) {
            return false;
        }
        if(other.getZ() + other.getDepth() < this.getZ()) {
            return false;
        }
        if(other.getZ() > this.getZ() + this.getDepth()) {
            return false;
        }

        return true;
    }

    public boolean lineCollision(Line line) {
        float deltaX = line.getX2() - line.getX1();
        float deltaY = line.getY2() - line.getY1();
        float deltaZ = line.getZ2() - line.getZ1();
        float scaleX = 1f / deltaX;
        float scaleY = 1f / deltaY;
        float scaleZ = 1f / deltaZ;
        float signX = Math.signum(scaleX);
        float signY = Math.signum(scaleY);
        float signZ = Math.signum(scaleZ);
        float halfX = .5f * getWidth();
        float halfY = .5f * getHeight();
        float halfZ = .5f * getDepth();
        float posX = getX() + halfX;
        float posY = getY() + halfY;
        float posZ = getZ() + halfZ;

        float nearTimeX = (posX - signX * halfX - line.getX1()) * scaleX;
        float nearTimeY = (posY - signY * halfY - line.getY1()) * scaleY;
        float nearTimeZ = (posZ - signZ * halfZ - line.getZ1()) * scaleZ;
        float farTimeX = (posX + signX * halfX - line.getX1()) * scaleX;
        float farTimeY = (posY + signY * halfY - line.getY1()) * scaleY;
        float farTimeZ = (posZ + signZ * halfZ - line.getZ1()) * scaleZ;

        if (nearTimeX > farTimeY || nearTimeY > farTimeX || nearTimeZ > farTimeZ) {
            return false;
        }

        float nearTime = Math.max(Math.max(nearTimeX, nearTimeY), nearTimeZ);
        float farTime = Math.min(Math.min(farTimeX, farTimeY), farTimeZ);

        if (nearTime >= 1 || farTime <= 0) {
            return false;
        }

        return true;
    }
}
