package net.loganford.noideaengine.shape;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.Map;

public class ShapeIntersectionEngine {
    //Singleton Boilerplate
    private static ShapeIntersectionEngine instance;
    public static ShapeIntersectionEngine getInstance() {
        if(instance == null) {
            instance = new ShapeIntersectionEngine();
        }

        return instance;
    }
    //End singleton boilerplate

    private static Vector3f V3F = new Vector3f();

    private int maxShapes = 8;
    private int numberOfShapes;
    private Map<Class<? extends Shape>, Integer> shapeIds = new HashMap<>();
    private HandlerContainer[] handlerContainers = new HandlerContainer[maxShapes * maxShapes];

    public int registerShape(Class<? extends Shape> clazz) {
        Integer registration = shapeIds.get(clazz);
        if(registration == null) {
            registration = numberOfShapes++;

            if(registration >= maxShapes) {
                doubleHandlerContainerSize();
            }

            shapeIds.put(clazz, registration);
        }
        return registration;
    }

    @SuppressWarnings("unchecked")
    public boolean collides(Shape a, Shape b) {
        HandlerContainer container = handlerContainers[a.getRegistration() + b.getRegistration() * maxShapes];
        if(container == null || container.intersectionHandler == null) {
            return false;
        }
        return container.intersectionHandler.intersects(a, b);
    }

    @SuppressWarnings("unchecked")
    public void sweep(SweepResult result, Shape a, Vector3fc velocity, Shape b) {
        result.clear();

        HandlerContainer container = handlerContainers[a.getRegistration() + b.getRegistration() * maxShapes];
        if(container == null || container.sweepHandler == null) {
            return;
        }
        container.sweepHandler.sweep(result, a, velocity, b);
    }

    public <A extends Shape, B extends Shape> void addIntersectionHandler(Class<A> clazzA, Class<B> clazzB, IntersectionHandler<A, B> handler) {
        Integer registrationA = shapeIds.get(clazzA);
        Integer registrationB = shapeIds.get(clazzB);

        if(registrationA == null) {
            registrationA = registerShape(clazzA);
        }
        if(registrationB == null) {
            registrationB = registerShape(clazzB);
        }

        getHandlerContainer(registrationB, registrationA).intersectionHandler = (new SwappedIntersectionHandler<>(handler));
        getHandlerContainer(registrationA,registrationB).intersectionHandler = (handler);
    }

    public <A extends Shape, B extends Shape> void addSweepHandler(Class<A> clazzA, Class<B> clazzB, SweepHandler<A, B> handler) {
        Integer registrationA = shapeIds.get(clazzA);
        Integer registrationB = shapeIds.get(clazzB);

        if(registrationA == null) {
            registrationA = registerShape(clazzA);
        }
        if(registrationB == null) {
            registrationB = registerShape(clazzB);
        }

        getHandlerContainer(registrationB, registrationA).sweepHandler = (new SwappedSweepHandler<>(handler));
        getHandlerContainer(registrationA,registrationB).sweepHandler = (handler);
    }

    private class HandlerContainer {
        private IntersectionHandler intersectionHandler;
        private SweepHandler sweepHandler;
    }
    
    private void doubleHandlerContainerSize() {
        HandlerContainer[] newHandlerContainer = new HandlerContainer[maxShapes * maxShapes * 4];

        for(int x = 0; x < maxShapes; x++) {
            for(int y = 0; y < maxShapes; y++) {
                HandlerContainer oldHandlerContainer = handlerContainers[x + y * maxShapes];
                newHandlerContainer[x + y * maxShapes * 2] = oldHandlerContainer;
            }
        }

        handlerContainers = newHandlerContainer;
        maxShapes *= 2;
    }

    private HandlerContainer getHandlerContainer(int x, int y) {
        HandlerContainer handlerContainer = handlerContainers[x + y * maxShapes];
        if(handlerContainer == null) {
            handlerContainer = new HandlerContainer();
            handlerContainers[x + y * maxShapes] = handlerContainer;
        }
        return handlerContainer;
    }

    private class SwappedIntersectionHandler<A extends Shape, B extends Shape> implements IntersectionHandler<A, B> {
        private IntersectionHandler<B, A> other;
        public SwappedIntersectionHandler(IntersectionHandler<B, A> other) {
            this.other = other;
        }

        @Override
        public boolean intersects(A a, B b) {
            return other.intersects(b, a);
        }
    }

    private class SwappedSweepHandler<A extends Shape, B extends Shape> implements SweepHandler<A, B> {
        private SweepHandler<B, A> other;

        public SwappedSweepHandler(SweepHandler<B, A> other) {
            this.other = other;
        }

        @Override
        public void sweep(SweepResult result, A a, Vector3fc velocity, B b) {
            other.sweep(result, b, V3F.set(velocity).mul(-1), a);
            result.setNormal3(V3F.set(result.getNormal3()).mul(-1));
            if(result.getShape() != null) {
                result.setShape(a);
            }
        }
    }
}
