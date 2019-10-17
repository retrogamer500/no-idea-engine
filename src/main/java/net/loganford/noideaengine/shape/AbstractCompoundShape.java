package net.loganford.noideaengine.shape;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;

public abstract class AbstractCompoundShape extends Shape {

    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();

    @Override
    public void getPosition(Vector3f position) {
        getShapes().get(0).getPosition(position);
    }

    @Override
    public void setPosition(Vector3fc position) {
        List<? extends Shape> shapes = getShapes();
        Vector3f difference = V3F.set(shapes.get(0).getPosition()).sub(position);
        for(int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            shape.setPosition(V3F_1.set(shape.getPosition()).add(difference));
        }
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        int i = 0;
        for(Shape shape : getShapes()) {
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
        List<? extends Shape> shapes = getShapes();
        for(int i = 0; i < shapes.size(); i++) {
            if(shapes.get(i).collidesWith(other)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void sweep(SweepResult result, Vector3fc velocity, Shape b) {
        //Todo: implement
    }

    public abstract List<? extends Shape> getShapes();
}
