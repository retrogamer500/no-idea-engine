package net.loganford.noideaengine.state.collisionSystem;

import net.loganford.noideaengine.shape.Line;
import net.loganford.noideaengine.shape.Rect;
import net.loganford.noideaengine.shape.Shape2D;
import net.loganford.noideaengine.state.entity.Entity2D;
import net.loganford.noideaengine.utils.MathUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpacialPartitionBroadphase implements CollisionSystem2D {

    private int cellSize;
    private int bucketCount;
    private List<List<Entity2D>> buckets;
    private Rect rect = new Rect(0, 0, 0, 0);

    public SpacialPartitionBroadphase(int cellSize, int bucketCount) {
        this.cellSize = cellSize;
        this.bucketCount = bucketCount;
    }

    @Override
    public void init() {
        buckets = new ArrayList<>(bucketCount);
        for(int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
    }

    @Override
    public void destroy() {
        buckets = null;
    }

    @Override
    public void collisionSystemAddEntity(Entity2D entity) {
        collisionSystemAfterMove(entity);
    }

    @Override
    public void collisionSystemBeforeMove(Entity2D entity) {
        if(entity.getShape() != null) {
            if(entity.getShape() instanceof Line) {
                doActionWithLine((Line) entity.getShape(), (bucket -> {
                    for (int i = bucket.size() - 1; i >= 0; i--) {
                        if (bucket.get(i).equals(entity)) {
                            bucket.remove(i);
                        }
                    }
                    return false;
                }));
            }
            else {
                entity.getShape().getBoundingBox(rect);
                int x1 = (int) (rect.getX() / cellSize);
                int y1 = (int) (rect.getY() / cellSize);
                int x2 = (int) ((rect.getX() + rect.getWidth()) / cellSize);
                int y2 = (int) ((rect.getY() + rect.getHeight()) / cellSize);

                for (int tx = x1; tx <= x2; tx++) {
                    for (int ty = y1; ty <= y2; ty++) {
                        int bucketNum = hash2d(tx, ty);
                        List<Entity2D> bucket = buckets.get(bucketNum);
                        for (int i = bucket.size() - 1; i >= 0; i--) {
                            if (bucket.get(i).equals(entity)) {
                                bucket.remove(i);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void collisionSystemAfterMove(Entity2D entity) {
        if(entity.getShape() != null) {
            if(entity.getShape() instanceof  Line) {
                doActionWithLine((Line) entity.getShape(), (bucket -> {
                    bucket.add(entity);
                    return false;
                }));
            }
            else {
                entity.getShape().getBoundingBox(rect);
                int x1 = (int) (rect.getX() / cellSize);
                int y1 = (int) (rect.getY() / cellSize);
                int x2 = (int) ((rect.getX() + rect.getWidth()) / cellSize);
                int y2 = (int) ((rect.getY() + rect.getHeight()) / cellSize);

                for (int tx = x1; tx <= x2; tx++) {
                    for (int ty = y1; ty <= y2; ty++) {
                        int bucketNum = hash2d(tx, ty);
                        buckets.get(bucketNum).add(entity);
                    }
                }
            }
        }
    }

    @Override
    public void collisionSystemRemoveEntity(Entity2D entity) {
        collisionSystemBeforeMove(entity);
    }

    @Override
    public boolean collidesWith(Shape2D shape, Class<? extends Entity2D> clazz) {
        if(shape instanceof Line) {
            return doActionWithLine((Line) shape, (bucket -> {
                for (int i = 0; i < bucket.size(); i++) {
                    Entity2D entity = bucket.get(i);
                    if (entity.getShape() != shape &&
                            clazz.isAssignableFrom(entity.getClass()) &&
                            entity.getShape().collidesWith(shape)) {
                        return true;
                    }
                }
                return false;
            }));
        }
        else {
            shape.getBoundingBox(rect);
            int x1 = (int) (rect.getX() / cellSize);
            int y1 = (int) (rect.getY() / cellSize);
            int x2 = (int) ((rect.getX() + rect.getWidth()) / cellSize);
            int y2 = (int) ((rect.getY() + rect.getHeight()) / cellSize);

            for (int tx = x1; tx <= x2; tx++) {
                for (int ty = y1; ty <= y2; ty++) {
                    int bucketNum = hash2d(tx, ty);
                    List<Entity2D> bucket = buckets.get(bucketNum);
                    for (int i = 0; i < bucket.size(); i++) {
                        Entity2D entity = bucket.get(i);
                        if (entity.getShape() != shape &&
                                clazz.isAssignableFrom(entity.getClass()) &&
                                entity.getShape().collidesWith(shape)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Entity2D getCollision(Shape2D shape, Class<? extends Entity2D> clazz) {
        if(shape instanceof Line) {
            Entity2D[] result = {null};
            doActionWithLine((Line) shape, (bucket -> {
                for (int i = 0; i < bucket.size(); i++) {
                    Entity2D entity = bucket.get(i);
                    if (entity.getShape() != shape &&
                            clazz.isAssignableFrom(entity.getClass()) &&
                            entity.getShape().collidesWith(shape)) {
                        result[0] = entity;
                        return true;
                    }
                }
                return false;
            }));
            return result[0];
        }
        else {
            shape.getBoundingBox(rect);
            int x1 = (int) (rect.getX() / cellSize);
            int y1 = (int) (rect.getY() / cellSize);
            int x2 = (int) ((rect.getX() + rect.getWidth()) / cellSize);
            int y2 = (int) ((rect.getY() + rect.getHeight()) / cellSize);

            for (int tx = x1; tx <= x2; tx++) {
                for (int ty = y1; ty <= y2; ty++) {
                    int bucketNum = hash2d(tx, ty);
                    List<Entity2D> bucket = buckets.get(bucketNum);
                    for (int i = 0; i < bucket.size(); i++) {
                        Entity2D entity = bucket.get(i);
                        if (entity.getShape() != shape &&
                                clazz.isAssignableFrom(entity.getClass()) &&
                                entity.getShape().collidesWith(shape)) {
                            return entity;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public <C extends Entity2D> List<C> getCollisions(Shape2D shape, Class<C> clazz) {
        Set<C> resultSet = new HashSet<>();
        if(shape instanceof Line) {
            doActionWithLine((Line) shape, (bucket -> {
                for (int i = 0; i < bucket.size(); i++) {
                    Entity2D entity = bucket.get(i);
                    if (entity.getShape() != shape &&
                            clazz.isAssignableFrom(entity.getClass()) &&
                            entity.getShape().collidesWith(shape)) {
                        resultSet.add((C)entity);
                    }
                }
                return false;
            }));
        }
        else {
            shape.getBoundingBox(rect);
            int x1 = (int) (rect.getX() / cellSize);
            int y1 = (int) (rect.getY() / cellSize);
            int x2 = (int) ((rect.getX() + rect.getWidth()) / cellSize);
            int y2 = (int) ((rect.getY() + rect.getHeight()) / cellSize);

            for (int tx = x1; tx <= x2; tx++) {
                for (int ty = y1; ty <= y2; ty++) {
                    int bucketNum = hash2d(tx, ty);
                    List<Entity2D> bucket = buckets.get(bucketNum);
                    for (int i = 0; i < bucket.size(); i++) {
                        Entity2D entity = bucket.get(i);
                        if (entity.getShape() != shape &&
                                clazz.isAssignableFrom(entity.getClass()) &&
                                entity.getShape().collidesWith(shape)) {
                            resultSet.add((C)entity);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(resultSet);
    }

    private int hash2d(int x, int y) {
        return Math.abs(((x * 89513) + (y * 15473)) % bucketCount);
    }

    private interface Action {
        /**
         * Lambda function
         * @param bucket
         * @return - true to exit voxel traversal, false to continue
         */
        boolean performActionWithBucket(List<Entity2D> bucket);
    }

    /**
     * Algorithm based on 'A Fast Voxel Traversal Algorithm for Ray Tracing' by John Amanatides and Andrew Woo
     * @param line
     * @param action
     * @return whether the action has been exited
     */
    private boolean doActionWithLine(Line line, Action action) {
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
            List<Entity2D> bucket = buckets.get(bucketNum);
            boolean exit = action.performActionWithBucket(bucket);
            if(exit) {
                return true;
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
        return false;
    }
}
