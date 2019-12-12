package net.loganford.noideaengine.utils.collections;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.shape.Cuboid;
import net.loganford.noideaengine.shape.Line;
import net.loganford.noideaengine.shape.Shape;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Octree<S extends Shape> {
    private static Vector3f V3F = new Vector3f();
    
    @Getter @Setter private int maxDepth = 12;
    @Getter @Setter private int maxContents = 8;
    @Getter @Setter private float initialSize = 1024f;

    @Getter private Node<S> root;

    public Octree() {
        this(12, 8, 1024f);
    }

    public Octree(int maxDepth, int maxContents, float initialSize) {
        root = new Node<S>(this, new Vector3f(), initialSize, 0);
    }

    public void add(S shape) {
        while(! root.shape.fullyContains(shape.getBoundingBox())) {
            Vector3f difference = V3F;
            difference.set(shape.getPosition()).sub(root.position);
            boolean positiveX = difference.x > 0;
            boolean positiveY = difference.y > 0;
            boolean positiveZ = difference.z > 0;
            float newPosX = root.position.x + (positiveX ? 1: -1) * root.size;
            float newPosY = root.position.y + (positiveY ? 1: -1) * root.size;
            float newPosZ = root.position.z + (positiveZ ? 1: -1) * root.size;
            Node newRootNode = new Node(this, new Vector3f(newPosX, newPosY, newPosZ), root.size * 2, root.depth - 1);
            newRootNode.subdivide();
            int index = (positiveX ? 4 : 0) + (positiveY ? 2 : 0) + (positiveZ ? 1 : 0);
            newRootNode.children[index] = root;
            root = newRootNode;
        }

        root.add(shape);
    }

    public void remove(S shape) {
        root.remove(shape);
    }

    public void performAction(Shape shape, OctreeAction action) {
        if (shape instanceof Line) {
            Line testLine = (Line) shape;
            performOctreeActionWithLine(testLine, action, root);
        }
        else {
            Cuboid testCuboid = shape.getBoundingBox();
            performOctreeActionWithCuboid(testCuboid, action, root);
        }
    }

    private ActionResult performOctreeActionWithLine(Line line, OctreeAction<S> action, Node<S> node) {
        if(node.shape.lineCollision(line)) {
            if(node.hasChildNodes()) {
                for(Node child : node.children) {
                    ActionResult result = performOctreeActionWithLine(line, action, child);
                    if(result == ActionResult.EXIT_EARLY) {
                        return ActionResult.EXIT_EARLY;
                    }
                }
            }
            else {
                return action.perform(node);
            }
        }

        return ActionResult.CONTINUE;
    }

    private ActionResult performOctreeActionWithCuboid(Cuboid cuboid, OctreeAction<S> action, Node<S> node) {
        if(node.shape.cuboidCollision(cuboid)) {
            if(node.hasChildNodes()) {
                for(Node child : node.children) {
                    ActionResult result = performOctreeActionWithCuboid(cuboid, action, child);
                    if(result == ActionResult.EXIT_EARLY) {
                        return ActionResult.EXIT_EARLY;
                    }
                }
            }
            else {
                return action.perform(node);
            }
        }

        return ActionResult.CONTINUE;
    }

    public enum ActionResult {
        EXIT_EARLY,
        CONTINUE
    }

    public interface OctreeAction<S extends Shape> {
        /**
         * Lambda function to perform on a node
         * @param node the current node to perform the action with
         * @return - OctreeActionResult.EXIT_EARLY to stop, or OctreeActionResult.CONTINUE to keep performing node actions
         */
        ActionResult perform(Node node);
    }

    public static class Node<S extends Shape> {
        private Octree octree;

        @Getter private Vector3f position;
        @Getter private float size;
        @Getter private Cuboid shape = new Cuboid(0, 0, 0, 0, 0, 0);
        @Getter private int depth;
        @Getter private Node<S>[] children = new Node[8];
        @Getter private ArrayList<S> contents = new ArrayList<>();

        private Node(Octree octree, Vector3f position, float size, int depth) {
            this.octree = octree;
            this.position = position;
            this.size = size;
            this.depth = depth;

            shape.setPosition(new Vector3f().set(position).sub(new Vector3f(size)));
            shape.setSize(new Vector3f().set(size * 2));
        }

        public boolean hasChildNodes() {
            return children[0] != null;
        }

        /**
         * Subdivide the node, creating 8 children and shifting the contents back down to the new child nodes
         */
        public void subdivide() {
            children[0] = new Node(octree, new Vector3f(position.x + size/2, position.y + size/2, position.z + size/2), size / 2, depth + 1);
            children[1] = new Node(octree, new Vector3f(position.x + size/2, position.y + size/2, position.z - size/2), size / 2, depth + 1);
            children[2] = new Node(octree, new Vector3f(position.x + size/2, position.y - size/2, position.z + size/2), size / 2, depth + 1);
            children[3] = new Node(octree, new Vector3f(position.x + size/2, position.y - size/2, position.z - size/2), size / 2, depth + 1);
            children[4] = new Node(octree, new Vector3f(position.x - size/2, position.y + size/2, position.z + size/2), size / 2, depth + 1);
            children[5] = new Node(octree, new Vector3f(position.x - size/2, position.y + size/2, position.z - size/2), size / 2, depth + 1);
            children[6] = new Node(octree, new Vector3f(position.x - size/2, position.y - size/2, position.z + size/2), size / 2, depth + 1);
            children[7] = new Node(octree, new Vector3f(position.x - size/2, position.y - size/2, position.z - size/2), size / 2, depth + 1);

            for(S existingShape : contents) {
                addShapeToChildrenIfAble(existingShape);
            }
            contents.clear();
        }

        public void add(S shape) {
            if(!hasChildNodes()) {
                if(contents.size() + 1 > octree.getMaxContents() && depth < octree.getMaxDepth()) {
                    subdivide();
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

        public void remove(S shape) {
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

        private void addShapeToChildrenIfAble(S shape) {
            for(Node child : children) {
                if(child.shape.cuboidCollision(shape.getBoundingBox())) {
                    child.add(shape);
                }
            }
        }
    }
}
