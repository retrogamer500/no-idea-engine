package net.loganford.noideaengine.state.entity.systems.collision;

import net.loganford.noideaengine.shape.*;
import net.loganford.noideaengine.state.entity.Entity;
import net.loganford.noideaengine.state.entity.signals.AfterMotionSignal;
import net.loganford.noideaengine.state.entity.signals.BeforeMotionSignal;
import net.loganford.noideaengine.utils.math.MathUtils;
import net.loganford.noideaengine.utils.messaging.Signal;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class SpacialPartitionCollisionSystem extends CollisionSystem {

    private static Set SET = new HashSet<>();
    private static List LIST = new ArrayList<>();
    private static Line LINE = new Line(0, 0, 0, 0);
    private static Rect RECT = new Rect(0, 0, 1, 1);
    private static Rect RECT_2 = new Rect(0, 0, 0, 0);
    private static Cuboid CUBE = new Cuboid(0, 0, 0, 1, 1, 1);
    private static Cuboid CUBE_2 = new Cuboid(0, 0, 0, 1, 1, 1);

    private int cellSize;
    private int bucketCount;
    private List<List<Shape>> buckets;

    public SpacialPartitionCollisionSystem() {
        this(32, 1024);
    }

    public SpacialPartitionCollisionSystem(int cellSize, int bucketCount) {
        this.cellSize = cellSize;
        this.bucketCount = bucketCount;

        buckets = new ArrayList<>(bucketCount);
        for(int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
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
    public boolean collidesWith(Shape shape, Class<? extends Entity> clazz) {
        boolean[] result = {false};

        performBucketAction(shape, (bucket) -> {
            for (int i = 0; i < bucket.size(); i++) {
                Entity entity = bucket.get(i).getOwningEntity();
                if (entity.getShape() != shape &&
                        clazz.isAssignableFrom(entity.getClass()) &&
                        entity.getShape().collidesWith(shape)) {
                    result[0] = true;
                    return BucketActionResult.EXIT_EARLY;
                }
            }

            return BucketActionResult.CONTINUE;
        });

        return result[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Entity> C getCollision(Shape shape, Class<C> clazz) {
        Entity[] result = {null};

        performBucketAction(shape, (bucket) -> {
            for (int i = 0; i < bucket.size(); i++) {
                Entity entity = bucket.get(i).getOwningEntity();
                if (entity.getShape() != shape &&
                        clazz.isAssignableFrom(entity.getClass()) &&
                        entity.getShape().collidesWith(shape)) {
                    result[0] = entity;
                    return BucketActionResult.EXIT_EARLY;
                }
            }

            return BucketActionResult.CONTINUE;
        });

        return (C) result[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Entity> List<C> getCollisions(Shape shape, Class<C> clazz) {
        LIST.clear();
        getCollisions(LIST, shape, clazz);
        return LIST;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Entity> void getCollisions(List<C> list, Shape shape, Class<C> clazz) {
        SET.clear();
        Set<C> resultSet = (Set<C>) SET;

        performBucketAction(shape, (bucket) -> {
            for (int i = 0; i < bucket.size(); i++) {
                Entity entity = bucket.get(i).getOwningEntity();
                if (entity.getShape() != shape &&
                        clazz.isAssignableFrom(entity.getClass()) &&
                        entity.getShape().collidesWith(shape)) {
                    resultSet.add((C)entity);
                }
            }

            return BucketActionResult.CONTINUE;
        });

        list.clear();
        list.addAll(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends Entity> void sweep(SweepResult result, Shape shape, Vector3fc velocity, Class<E> clazz) {
        result.clear();
        result.getPosition().set(shape.getPosition());
        result.getVelocity().set(velocity);

        Shape sweepMask = getSweepMask(shape, velocity);
        boolean exitEarly = sweepMask instanceof Line;

        performBucketAction(sweepMask, (bucket) -> {
            for (int i = 0; i < bucket.size(); i++) {
                Entity entity = bucket.get(i).getOwningEntity();
                if (entity.getShape() != shape &&
                        clazz.isAssignableFrom(entity.getClass())) {

                    SweepResult otherResult = shape.sweep(velocity, entity.getShape());
                    otherResult.setEntity(entity);

                    if(otherResult.getDistance() < result.getDistance()) {
                        result.set(otherResult);
                    }
                }
            }

            if(exitEarly) {
                return BucketActionResult.EXIT_EARLY;
            }
            return BucketActionResult.CONTINUE;
        });
    }

    private Shape getSweepMask(Shape shape, Vector3fc velocity) {
        if(shape instanceof Point) {
            Point point = (Point) shape;
            LINE.setX1(point.getX());
            LINE.setY1(point.getY());
            LINE.setZ1(point.getZ());
            LINE.setX2(point.getX() + velocity.x());
            LINE.setY2(point.getY() + velocity.y());
            LINE.setZ2(point.getZ() + velocity.z());
            return LINE;
        }
        else {
            shape.getBoundingBox(CUBE);

            if(velocity.x() < 0) {
                CUBE.setX(CUBE.getX() + velocity.x());
            }
            CUBE.setWidth(CUBE.getWidth() + Math.abs(velocity.x()));

            if(velocity.y() < 0) {
                CUBE.setY(CUBE.getY() + velocity.y());
            }
            CUBE.setHeight(CUBE.getHeight() + Math.abs(velocity.y()));

            if(velocity.z() < 0) {
                CUBE.setZ(CUBE.getZ() + velocity.z());
            }
            CUBE.setDepth(CUBE.getDepth() + Math.abs(velocity.y()));

            return CUBE;
        }
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
            if(entity.getShape() instanceof AbstractCompoundShape) {
                for(Shape shape : ((AbstractCompoundShape) entity.getShape()).getShapes()) {
                    performBucketAction(shape, (bucket) -> {
                        bucket.add(shape);
                        return BucketActionResult.CONTINUE;
                    });
                }
            }
            else {
                performBucketAction(entity.getShape(), (bucket) -> {
                    bucket.add(entity.getShape());
                    return BucketActionResult.CONTINUE;
                });
            }
        }
    }

    private void handleEntityRemoval(Entity entity) {
        if(entity.getShape() != null) {
            if(entity.getShape() instanceof AbstractCompoundShape) {
                for(Shape shape : ((AbstractCompoundShape) entity.getShape()).getShapes()) {
                    performBucketAction(shape, (bucket) -> {
                        for (int i = bucket.size() - 1; i >= 0; i--) {
                            if (bucket.get(i).equals(shape)) {
                                bucket.remove(i);
                                break;
                            }
                        }

                        return BucketActionResult.CONTINUE;
                    });
                }
            }
            else {
                performBucketAction(entity.getShape(), (bucket) -> {
                    for (int i = bucket.size() - 1; i >= 0; i--) {
                        if (bucket.get(i).equals(entity.getShape())) {
                            bucket.remove(i);
                            break;
                        }
                    }

                    return BucketActionResult.CONTINUE;
                });
            }
        }
    }

    private int hash3d(int x, int y, int z) {
        return Math.abs(((x * 89513) + (y * 15473) + (z * 39829)) % bucketCount);
    }


    /**
     * Performs a specific action (a lambda) for every bucket that intersects a certain shape.
     * @param shape the shape
     * @param bucketAction the action to perform
     */
    private void performBucketAction(Shape shape, BucketAction bucketAction) {
        if(shape instanceof Line) {
            performBucketActionWithLine((Line) shape, bucketAction);
        }
        else {
            shape.getBoundingBox(CUBE_2);
            performBucketActionWithRect(CUBE_2, bucketAction);
        }
    }

    private void performBucketActionWithRect(Cuboid cube, BucketAction bucketAction) {
        int x1 = (int) (cube.getX() / cellSize);
        int y1 = (int) (cube.getY() / cellSize);
        int z1 = (int) (cube.getZ() / cellSize);
        int x2 = (int) ((cube.getX() + cube.getWidth()) / cellSize);
        int y2 = (int) ((cube.getY() + cube.getHeight()) / cellSize);
        int z2 = (int) ((cube.getZ() + cube.getDepth()) / cellSize);

        for (int tx = x1; tx <= x2; tx++) {
            for (int ty = y1; ty <= y2; ty++) {
                for (int tz = z1; tz <= z2; tz++) {
                    int bucketNum = hash3d(tx, ty, tz);
                    List<Shape> bucket = buckets.get(bucketNum);
                    BucketActionResult result = bucketAction.perform(bucket);
                    if (result == BucketActionResult.EXIT_EARLY) {
                        return;
                    }
                }
            }
        }
    }

    /**
     * Algorithm based on 'A Fast Voxel Traversal Algorithm for Ray Tracing' by John Amanatides and Andrew Woo
     * @param line
     * @param bucketAction
     */
    private void performBucketActionWithLine(Line line, BucketAction bucketAction) {
        int i = (int) (line.getX1() / cellSize);
        int j = (int) (line.getY1() / cellSize);
        int k = (int) (line.getZ1() / cellSize);

        int iEnd = (int) (line.getX2() / cellSize);
        int jEnd = (int) (line.getY2() / cellSize);
        int kEnd = (int) (line.getZ2() / cellSize);

        int di = MathUtils.sign(line.getX2() - line.getX1());
        int dj = MathUtils.sign(line.getY2() - line.getY1());
        int dk = MathUtils.sign(line.getZ2() - line.getZ1());

        float minx = cellSize * ((int) (line.getX1() / cellSize));
        float maxx = minx + cellSize;
        float tx = ((line.getX1() > line.getX2()) ? (line.getX1() - minx) : (maxx - line.getX1())) / Math.abs(line.getX2() - line.getX1());

        float miny = cellSize * ((int) (line.getY1() / cellSize));
        float maxy = miny + cellSize;
        float ty = ((line.getY1() > line.getY2()) ? (line.getY1() - miny) : (maxy - line.getY1())) / Math.abs(line.getY2() - line.getY1());

        float minz = cellSize * ((int) (line.getZ1() / cellSize));
        float maxz = minz + cellSize;
        float tz = ((line.getZ1() > line.getZ2()) ? (line.getZ1() - minz) : (maxz - line.getZ1())) / Math.abs(line.getZ2() - line.getZ1());

        float deltaX = ((float) cellSize) / Math.abs(line.getX2() - line.getX1());
        float deltaY = ((float) cellSize) / Math.abs(line.getY2() - line.getY1());
        float deltaZ = ((float) cellSize) / Math.abs(line.getZ2() - line.getZ1());

        while(true) {
            int bucketNum = hash3d(i, j, k);
            List<Shape> bucket = buckets.get(bucketNum);
            BucketActionResult result = bucketAction.perform(bucket);
            if(result == BucketActionResult.EXIT_EARLY) {
                break;
            }
            if(tx<=ty) {
                if(tx < tz) {
                    if (i == iEnd) {
                        break;
                    }
                    tx += deltaX;
                    i += di;
                }
                else {
                    if (k == kEnd) {
                        break;
                    }
                    tz += deltaZ;
                    k += dk;
                }
            }
            else {
                if(ty < tz) {
                    if (j == jEnd) {
                        break;
                    }
                    ty += deltaY;
                    j += dj;
                }
                else {
                    if (k == kEnd) {
                        break;
                    }
                    tz += deltaZ;
                    k += dk;
                }
            }
        }
    }

    private enum BucketActionResult {
        EXIT_EARLY,
        CONTINUE
    }

    private interface BucketAction {
        /**
         * Lambda function to perform on a bucket
         * @param bucket the current bucket to perform the action with
         * @return - BucketActionResult.EXIT_EARLY to stop, or BucketActionResult.CONTINUE to keep performing bucket actions
         */
        BucketActionResult perform(List<Shape> bucket);
    }
}
