package net.loganford.noideaengine.shape;

import java.util.HashMap;
import java.util.Map;

public class ShapeIntersectionEngine {
    private static int maxShapes = 8;
    private static ShapeIntersectionEngine singleton;

    private static int numberOfShapes;
    private static Map<Class<? extends Shape>, Integer> shapeIds = new HashMap<>();
    private static HandlerContainer[] handlerContainers = new HandlerContainer[maxShapes * maxShapes];

    public static int registerShape(Class<? extends Shape> clazz) {
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

    public static void doubleHandlerContainerSize() {
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

    @SuppressWarnings("unchecked")
    public static boolean collides(Shape a, Shape b) {
        HandlerContainer container = handlerContainers[a.getRegistration() + b.getRegistration() * maxShapes];
        if(container == null || container.intersectionHandler == null) {
            return false;
        }
        return container.intersectionHandler.intersects(a, b);
    }

    private static class HandlerContainer {
        private IntersectionHandler intersectionHandler;
    }

    public static <A extends Shape, B extends Shape> void addIntersectionHandler(Class<A> clazzA, Class<B> clazzB, IntersectionHandler<A, B> handler) {
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

    private static HandlerContainer getHandlerContainer(int x, int y) {
        HandlerContainer handlerContainer = handlerContainers[x + y * maxShapes];
        if(handlerContainer == null) {
            handlerContainer = new HandlerContainer();
            handlerContainers[x + y * maxShapes] = handlerContainer;
        }
        return handlerContainer;
    }

    private static class SwappedIntersectionHandler<A extends Shape, B extends Shape> implements IntersectionHandler<A, B> {
        private IntersectionHandler<B, A> other;
        public SwappedIntersectionHandler(IntersectionHandler<B, A> other) {
            this.other = other;
        }

        @Override
        public boolean intersects(A a, B b) {
            return other.intersects(b, a);
        }
    }
}
