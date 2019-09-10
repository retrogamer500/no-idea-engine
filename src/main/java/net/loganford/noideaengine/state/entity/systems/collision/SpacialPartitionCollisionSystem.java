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

    private int cellSize;
    private int bucketCount;
    private List<List<Entity>> buckets;

    //Cached rectangle to use for obtaining bounding boxes of shapes
    private Rect rect = new Rect(0, 0, 0, 0);

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
                Entity entity = bucket.get(i);
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
                Entity entity = bucket.get(i);
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
                Entity entity = bucket.get(i);
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
    public <E extends Entity> void sweepImpl(SweepResult result, Shape shape, Vector3fc velocity, Class<E> clazz) {
        Shape sweepMask = getSweepMask(shape, velocity);
        boolean exitEarly = sweepMask instanceof Line;

        performBucketAction(sweepMask, (bucket) -> {
            for (int i = 0; i < bucket.size(); i++) {
                Entity entity = bucket.get(i);
                if (entity.getShape() != shape &&
                        clazz.isAssignableFrom(entity.getClass()) &&
                        entity.getShape().collidesWith(shape)) {

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

        result.clear();
    }

    private Shape getSweepMask(Shape shape, Vector3fc velocity) {
        if(shape instanceof Point) {
            Point point = (Point) shape;
            LINE.setX1(point.getX());
            LINE.setY1(point.getY());
            LINE.setX2(point.getX() + velocity.x());
            LINE.setY2(point.getY() + velocity.y());
            return LINE;
        }
        else {
            shape.getBoundingBox(RECT);

            if(velocity.x() < 0) {
                RECT.setX(RECT.getX() + velocity.x());
            }
            RECT.setWidth(RECT.getWidth() + Math.abs(velocity.x()));

            if(velocity.y() < 0) {
                RECT.setY(RECT.getY() + velocity.y());
            }
            RECT.setHeight(RECT.getHeight() + Math.abs(velocity.y()));

            return RECT;
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
            performBucketAction(entity.getShape(), (bucket) -> {
                bucket.add(entity);
                return BucketActionResult.CONTINUE;
            });
        }
    }

    private void handleEntityRemoval(Entity entity) {
        if(entity.getShape() != null) {
            performBucketAction(entity.getShape(), (bucket) -> {
                for (int i = bucket.size() - 1; i >= 0; i--) {
                    if (bucket.get(i).equals(entity)) {
                        bucket.remove(i);
                        break;
                    }
                }

                return BucketActionResult.CONTINUE;
            });
        }
    }

    private int hash2d(int x, int y) {
        return Math.abs(((x * 89513) + (y * 15473)) % bucketCount);
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
            shape.getBoundingBox(rect);
            performBucketActionWithRect(rect, bucketAction);
        }
    }

    private void performBucketActionWithRect(Rect rect, BucketAction bucketAction) {
        int x1 = (int) (rect.getX() / cellSize);
        int y1 = (int) (rect.getY() / cellSize);
        int x2 = (int) ((rect.getX() + rect.getWidth()) / cellSize);
        int y2 = (int) ((rect.getY() + rect.getHeight()) / cellSize);

        for (int tx = x1; tx <= x2; tx++) {
            for (int ty = y1; ty <= y2; ty++) {
                int bucketNum = hash2d(tx, ty);
                List<Entity> bucket = buckets.get(bucketNum);
                BucketActionResult result = bucketAction.perform(bucket);
                if(result == BucketActionResult.EXIT_EARLY) {
                    return;
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

        int iEnd = (int) (line.getX2() / cellSize);
        int jEnd = (int) (line.getY2() / cellSize);

        int di = MathUtils.sign(line.getX2() - line.getX1());
        int dj = MathUtils.sign(line.getY2() - line.getY1());

        float minx = cellSize * ((int) (line.getX1() / cellSize));
        float maxx = minx + cellSize;
        float tx = ((line.getX1() > line.getX2()) ? (line.getX1() - minx) : (maxx - line.getX1())) / Math.abs(line.getX2() - line.getX1());

        float miny = cellSize * ((int) (line.getY1() / cellSize));
        float maxy = miny + cellSize;
        float ty = ((line.getY1() > line.getY2()) ? (line.getY1() - miny) : (maxy - line.getY1())) / Math.abs(line.getY2() - line.getY1());

        float deltaX = ((float) cellSize) / Math.abs(line.getX2() - line.getX1());
        float deltaY = ((float) cellSize) / Math.abs(line.getY2() - line.getY1());

        while(true) {
            int bucketNum = hash2d(i, j);
            List<Entity> bucket = buckets.get(bucketNum);
            BucketActionResult result = bucketAction.perform(bucket);
            if(result == BucketActionResult.EXIT_EARLY) {
                break;
            }
            if(tx<=ty) {
                if(i==iEnd) {
                    break;
                }
                tx+=deltaX;
                i+=di;
            }
            else {
                if(j==jEnd) {
                    break;
                }
                ty+=deltaY;
                j+=dj;
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
        BucketActionResult perform(List<Entity> bucket);
    }
}
