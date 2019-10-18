package net.loganford.noideaengine.shape;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Iterator;

public abstract class AbstractCompoundShape extends Shape implements Iterable<Shape> {

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();

    @Override
    public void getPosition(Vector3f position) {
        iterator().next().getPosition(position);
    }

    @Override
    public void setPosition(Vector3fc position) {
        Vector3f difference = null;
        int i = 0;
        for(Shape shape : this) {
            if(i == 0) {
                difference = V3F.set(shape.getPosition()).sub(position);
            }

            shape.setPosition(V3F_1.set(shape.getPosition()).add(difference));
            i++;
        }
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        int i = 0;
        for(Shape shape : this) {
            if(i == 0) {
                cube.set(shape.getBoundingBox());
            }
            else {
                cube.expand(shape.getBoundingBox());
            }

            i++;
        }
    }

    @Override
    public boolean collidesWith(Shape other) {
        for(Shape shape : this) {
            if(shape.collidesWith(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void sweep(SweepResult result, Vector3fc velocity, Shape b) {
        //Todo: implement
    }

    @Override
    public abstract Iterator<Shape> iterator();
}
