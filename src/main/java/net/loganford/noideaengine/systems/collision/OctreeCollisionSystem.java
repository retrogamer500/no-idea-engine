package net.loganford.noideaengine.systems.collision;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.entity.signals.AfterMotionSignal;
import net.loganford.noideaengine.entity.signals.BeforeMotionSignal;
import net.loganford.noideaengine.shape.*;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.messaging.Signal;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class OctreeCollisionSystem extends CollisionSystem {
    private static Vector3f V3F = new Vector3f();
    private static Set SET = new HashSet<>();
    private static List LIST = new ArrayList<>();

    @Getter @Setter private int maxDepth = 12;
    @Getter @Setter private int maxContents = 8;
    @Getter @Setter private float initialSize = 1024f;
    private Node root;

    public OctreeCollisionSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

        AnnotationUtil.getArgumentOptional("maxDepth", args).ifPresent((a) -> maxDepth = a.intValue());
        AnnotationUtil.getArgumentOptional("maxContents", args).ifPresent((a) -> maxContents = a.intValue());
        AnnotationUtil.getArgumentOptional("initialSize", args).ifPresent((a) -> initialSize = a.floatValue());

        root = new Node(new Vector3f(), initialSize, 0);
    }

    @Override
    public boolean collidesWith(Shape shape, Class<?> clazz) {
        boolean[] result = {false};

        performOctreeAction(shape, (shapes) -> {
            for (int i = 0; i < shapes.size(); i++) {
                Shape otherShape = shapes.get(i);
                if (otherShape != shape &&
                        clazz.isAssignableFrom(otherShape.getOwningEntity().getClass()) &&
                        otherShape.collidesWith(shape)) {
                    result[0] = true;
                    return OctreeActionResult.EXIT_EARLY;
                }
            }

            return OctreeActionResult.CONTINUE;
        });

        return result[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getCollision(Shape shape, Class<C> clazz) {
        Entity[] result = {null};

        performOctreeAction(shape, (shapes) -> {
            for (int i = 0; i < shapes.size(); i++) {
                Shape otherShape = shapes.get(i);
                if (otherShape != shape &&
                        clazz.isAssignableFrom(otherShape.getOwningEntity().getClass()) &&
                        otherShape.collidesWith(shape)) {
                    result[0] = otherShape.getOwningEntity();
                    return OctreeActionResult.EXIT_EARLY;
                }
            }

            return OctreeActionResult.CONTINUE;
        });

        return (C) result[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> List<C> getCollisions(Shape shape, Class<C> clazz) {
        LIST.clear();
        getCollisions(LIST, shape, clazz);
        return LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> void getCollisions(List<C> list, Shape shape, Class<C> clazz) {
        SET.clear();
        Set<C> resultSet = (Set<C>) SET;

        performOctreeAction(shape, (shapes) -> {
            for (int i = 0; i < shapes.size(); i++) {
                Shape otherShape = shapes.get(i);
                if (otherShape != shape &&
                        clazz.isAssignableFrom(otherShape.getOwningEntity().getClass()) &&
                        otherShape.collidesWith(shape)) {
                    resultSet.add((C)otherShape.getOwningEntity());
                }
            }

            return OctreeActionResult.CONTINUE;
        });

        list.clear();
        list.addAll(resultSet);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> void sweep(SweepResult result, Shape shape, Vector3fc velocity, Class<E> clazz) {
        SET.clear();
        result.clear();
        result.getPosition().set(shape.getPosition());
        result.getVelocity().set(velocity);

        Shape sweepMask = getSweepMask(shape, velocity);

        performOctreeAction(sweepMask, (shapes) -> {
            for (int i = 0; i < shapes.size(); i++) {
                Shape otherShape = shapes.get(i);

                if(SET.contains(otherShape)) {
                    continue;
                }
                SET.add(otherShape);

                if (otherShape != shape &&
                        clazz.isAssignableFrom(otherShape.getOwningEntity().getClass())) {

                    SweepResult otherResult = shape.sweep(velocity, otherShape);
                    otherResult.setEntity(otherShape.getOwningEntity());

                    if(otherResult.getDistance() < result.getDistance()) {
                        result.set(otherResult);
                    }
                }
            }

            return OctreeActionResult.CONTINUE;
        });
    }



    private void performOctreeAction(Shape shape, OctreeAction action) {
        if (shape instanceof Line) {
            Line testLine = (Line) shape;
            performOctreeActionWithLine(testLine, action, root);
        }
        else {
            Cuboid testCuboid = shape.getBoundingBox();
            performOctreeActionWithCuboid(testCuboid, action, root);
        }
    }

    private OctreeActionResult performOctreeActionWithLine(Line line, OctreeAction action, Node node) {
        if(node.shape.lineCollision(line)) {
            if(node.hasChildNodes()) {
                for(Node child : node.children) {
                    OctreeActionResult result = performOctreeActionWithLine(line, action, child);
                    if(result == OctreeActionResult.EXIT_EARLY) {
                        return OctreeActionResult.EXIT_EARLY;
                    }
                }
            }
            else {
                return action.perform(node.contents);
            }
        }

        return OctreeActionResult.CONTINUE;
    }

    private OctreeActionResult performOctreeActionWithCuboid(Cuboid cuboid, OctreeAction action, Node node) {
        if(node.shape.cuboidCollision(cuboid)) {
            if(node.hasChildNodes()) {
                for(Node child : node.children) {
                    OctreeActionResult result = performOctreeActionWithCuboid(cuboid, action, child);
                    if(result == OctreeActionResult.EXIT_EARLY) {
                        return OctreeActionResult.EXIT_EARLY;
                    }
                }
            }
            else {
                return action.perform(node.contents);
            }
        }

        return OctreeActionResult.CONTINUE;
    }

    private enum OctreeActionResult {
        EXIT_EARLY,
        CONTINUE
    }

    private interface OctreeAction {
        /**
         * Lambda function to perform on a node
         * @param node the current node to perform the action with
         * @return - OctreeActionResult.EXIT_EARLY to stop, or OctreeActionResult.CONTINUE to keep performing node actions
         */
        OctreeActionResult perform(List<Shape> node);
    }

    @Override
    public void addEntity(Entity entity) {
        super.addEntity(entity);
        entity.getBeforeMotionSignal().subscribe(this);
        entity.getAfterMotionSignal().subscribe(this);
        handleEntityAddition(entity);
    }

    @Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);
        entity.getBeforeMotionSignal().unsubscribe(this);
        entity.getAfterMotionSignal().unsubscribe(this);
        handleEntityRemoval(entity);
    }

    @Override
    public void receive(Signal<Entity> signal, Entity entity) {
        super.receive(signal, entity);

        if(signal instanceof BeforeMotionSignal) {
            handleEntityRemoval(entity);
        }
        else if(signal instanceof AfterMotionSignal) {
            handleEntityAddition(entity);
        }
    }

    private void handleEntityAddition(Entity entity) {
        if(entity.getShape() != null) {
            if (entity.getShape() instanceof AbstractCompoundShape) {
                for (Shape shape : (AbstractCompoundShape) entity.getShape()) {
                    shape.setOwningEntity(entity);
                    handleShapeAddition(shape);
                }
            } else {
                entity.getShape().setOwningEntity(entity);
                handleShapeAddition(entity.getShape());
            }
        }
    }

    private void handleShapeAddition(Shape shape) {
        while(! root.shape.fullyContains(shape.getBoundingBox())) {
            Vector3f difference = V3F;
            difference.set(shape.getPosition()).sub(root.position);
            boolean positiveX = difference.x > 0;
            boolean positiveY = difference.y > 0;
            boolean positiveZ = difference.z > 0;
            float newPosX = root.position.x + (positiveX ? 1: -1) * root.size;
            float newPosY = root.position.y + (positiveY ? 1: -1) * root.size;
            float newPosZ = root.position.z + (positiveZ ? 1: -1) * root.size;
            Node newRootNode = new Node(new Vector3f(newPosX, newPosY, newPosZ), root.size * 2, root.depth - 1);
            newRootNode.subdivide();
            int index = (positiveX ? 4 : 0) + (positiveY ? 2 : 0) + (positiveZ ? 1 : 0);
            newRootNode.children[index] = root;
            root = newRootNode;
        }

        root.add(shape);
    }

    private void handleEntityRemoval(Entity entity) {
        if(entity.getShape() != null) {
            if (entity.getShape() instanceof AbstractCompoundShape) {
                for (Shape shape : (AbstractCompoundShape) entity.getShape()) {
                    shape.setOwningEntity(entity);
                    handleShapeRemoval(shape);
                }
            } else {
                entity.getShape().setOwningEntity(entity);
                handleShapeRemoval(entity.getShape());
            }
        }
    }

    private void handleShapeRemoval(Shape shape) {
        root.remove(shape);
    }

    private class Node {
        private Vector3f position;
        private float size;
        private Cuboid shape = new Cuboid(0, 0, 0, 0, 0, 0);
        private int depth;
        private Node[] children = new Node[8];
        private ArrayList<Shape> contents = new ArrayList<>();

        public Node(Vector3f position, float size, int depth) {
            this.position = position;
            this.size = size;
            this.depth = depth;

            shape.setPosition(new Vector3f().set(position).sub(new Vector3f(size)));
            shape.setSize(new Vector3f().set(size * 2));
        }

        public boolean hasChildNodes() {
            return children[0] != null;
        }

        public void subdivide() {
            children[0] = new Node(new Vector3f(position.x + size/2, position.y + size/2, position.z + size/2), size / 2, depth + 1);
            children[1] = new Node(new Vector3f(position.x + size/2, position.y + size/2, position.z - size/2), size / 2, depth + 1);
            children[2] = new Node(new Vector3f(position.x + size/2, position.y - size/2, position.z + size/2), size / 2, depth + 1);
            children[3] = new Node(new Vector3f(position.x + size/2, position.y - size/2, position.z - size/2), size / 2, depth + 1);
            children[4] = new Node(new Vector3f(position.x - size/2, position.y + size/2, position.z + size/2), size / 2, depth + 1);
            children[5] = new Node(new Vector3f(position.x - size/2, position.y + size/2, position.z - size/2), size / 2, depth + 1);
            children[6] = new Node(new Vector3f(position.x - size/2, position.y - size/2, position.z + size/2), size / 2, depth + 1);
            children[7] = new Node(new Vector3f(position.x - size/2, position.y - size/2, position.z - size/2), size / 2, depth + 1);
        }

        public void add(Shape shape) {
            if(!hasChildNodes()) {
                if(contents.size() + 1 > maxContents && depth < maxDepth) {
                    subdivide();

                    for(Shape existingShape : contents) {
                        addShapeToChildrenIfAble(existingShape);
                    }
                    contents.clear();
                    addShapeToChildrenIfAble(shape);
                }
                else {
                    contents.add(shape);
                }
            }
            else if(hasChildNodes()) {
                addShapeToChildrenIfAble(shape);
            }
        }

        public void remove(Shape shape) {
            if(!hasChildNodes()) {
                contents.remove(shape);
            }
            else {
                boolean childrenEmpty = true;
                for(Node child : children) {
                    if(child.shape.cuboidCollision(shape.getBoundingBox())) {
                        child.remove(shape);
                    }
                    if(child.contents.size() > 0 || child.hasChildNodes()) {
                        childrenEmpty = false;
                    }
                }
                if(childrenEmpty) {
                    for(int i = 0; i < children.length; i++) {
                        children[i] = null;
                    }
                }
            }
        }

        public void addShapeToChildrenIfAble(Shape shape) {
            for(Node child : children) {
                if(child.shape.cuboidCollision(shape.getBoundingBox())) {
                    child.add(shape);
                }
            }
        }

        public int countAllContents() {
            int result = 0;
            if(hasChildNodes()) {
                for(Node child : children) {
                    result += child.countAllContents();
                }
            }
            else {
                result += contents.size();
            }
            return result;
        }
    }
}
