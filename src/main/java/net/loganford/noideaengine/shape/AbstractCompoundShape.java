package net.loganford.noideaengine.shape;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractCompoundShape extends Shape implements Iterable<Shape> {

    private static SweepResult SWEEP_RESULT = new SweepResult();
    private static Vector3f V3F = new Vector3f();
    private static Vector3f V3F_1 = new Vector3f();
    private Cuboid boundingBox;

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

        if(boundingBox != null && difference != null) {
            boundingBox.setPosition(V3F.set(boundingBox.getPosition()).add(difference));
        }
    }

    @Override
    public void getBoundingBox(Cuboid cube) {
        if(boundingBox == null) {
            boundingBox = new Cuboid(new Vector3f(), new Vector3f());
            regenerateBoundingBox();
        }

        cube.set(boundingBox);
    }

    public void regenerateBoundingBox() {
        int i = 0;
        for(Shape shape : this) {
            if(i == 0) {
                boundingBox.set(shape.getBoundingBox());
            }
            else {
                boundingBox.expand(shape.getBoundingBox());
            }

            i++;
        }

        float radius = boundingBox.getSize().length() * .5f;
        Vector3f center = V3F_1.set(boundingBox.getSize()).mul(.5f).add(boundingBox.getPosition());
        boundingBox.setPosition(center.sub(radius, radius, radius));
        boundingBox.getSize().set(V3F.set(radius * 2));
    }

    @Override
    public abstract Iterator<Shape> iterator();

    public List<Shape> getShapeList() {
        ArrayList<Shape> list = new ArrayList<>();
        for(Shape shape : this) {
            list.add(shape);
        }
        return list;
    }
}
