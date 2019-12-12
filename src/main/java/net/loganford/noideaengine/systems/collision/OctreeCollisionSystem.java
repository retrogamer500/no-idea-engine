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
import net.loganford.noideaengine.utils.collections.Octree;
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

    private Octree<Shape> octree;

    @Getter @Setter private int maxDepth = 12;
    @Getter @Setter private int maxContents = 8;
    @Getter @Setter private float initialSize = 1024f;

    public OctreeCollisionSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

        AnnotationUtil.getArgumentOptional("maxDepth", args).ifPresent((a) -> maxDepth = a.intValue());
        AnnotationUtil.getArgumentOptional("maxContents", args).ifPresent((a) -> maxContents = a.intValue());
        AnnotationUtil.getArgumentOptional("initialSize", args).ifPresent((a) -> initialSize = a.floatValue());

        octree = new Octree<>(maxDepth, maxContents, initialSize);
    }

    @Override
    public boolean collidesWith(Shape shape, Class<?> clazz) {
        boolean[] result = {false};

        octree.performAction(shape, (node) -> {
            List<Shape> shapes = node.getContents();

            for (int i = 0; i < shapes.size(); i++) {
                Shape otherShape = shapes.get(i);
                if (otherShape != shape &&
                        clazz.isAssignableFrom(otherShape.getOwningEntity().getClass()) &&
                        otherShape.collidesWith(shape)) {
                    result[0] = true;
                    return Octree.ActionResult.EXIT_EARLY;
                }
            }

            return Octree.ActionResult.CONTINUE;
        });

        return result[0];
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getCollision(Shape shape, Class<C> clazz) {
        Entity[] result = {null};

        octree.performAction(shape, (node) -> {
            List<Shape> shapes = node.getContents();

            for (int i = 0; i < shapes.size(); i++) {
                Shape otherShape = shapes.get(i);
                if (otherShape != shape &&
                        clazz.isAssignableFrom(otherShape.getOwningEntity().getClass()) &&
                        otherShape.collidesWith(shape)) {
                    result[0] = otherShape.getOwningEntity();
                    return Octree.ActionResult.EXIT_EARLY;
                }
            }

            return Octree.ActionResult.CONTINUE;
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

        octree.performAction(shape, (node) -> {
            List<Shape> shapes = node.getContents();
            for (int i = 0; i < shapes.size(); i++) {
                Shape otherShape = shapes.get(i);
                if (otherShape != shape &&
                        clazz.isAssignableFrom(otherShape.getOwningEntity().getClass()) &&
                        otherShape.collidesWith(shape)) {
                    resultSet.add((C)otherShape.getOwningEntity());
                }
            }

            return Octree.ActionResult.CONTINUE;
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

        octree.performAction(sweepMask, (node) -> {
            List<Shape> shapes = node.getContents();
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

            return Octree.ActionResult.CONTINUE;
        });
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
                    octree.add(shape);
                }
            } else {
                entity.getShape().setOwningEntity(entity);
                octree.add(entity.getShape());
            }
        }
    }

    private void handleEntityRemoval(Entity entity) {
        if(entity.getShape() != null) {
            if (entity.getShape() instanceof AbstractCompoundShape) {
                for (Shape shape : (AbstractCompoundShape) entity.getShape()) {
                    shape.setOwningEntity(entity);
                    octree.remove(shape);
                }
            } else {
                entity.getShape().setOwningEntity(entity);
                octree.remove(entity.getShape());
            }
        }
    }
}
