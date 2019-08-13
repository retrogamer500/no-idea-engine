package net.loganford.noideaengine.shape;

import net.loganford.noideaengine.GameEngineException;

import java.util.HashMap;
import java.util.Map;

public class ShapeIntersectionEngine {
    public static final int MAX_SHAPES = 64;
    private static ShapeIntersectionEngine singleton;

    private static int numberOfShapes;
    private static Map<Class<? extends Shape>, Integer> shapeIds = new HashMap<>();
    private static IntersectionHandler[] handlers = new IntersectionHandler[MAX_SHAPES * MAX_SHAPES];

    public static int registerShape(Class<? extends Shape> clazz) {
        Integer registration = shapeIds.get(clazz);
        if(registration == null) {
            registration = numberOfShapes++;

            if(registration >= MAX_SHAPES) {
                throw new GameEngineException("Max number of Shape subclasses has been reached (" + MAX_SHAPES + ")");
            }

            shapeIds.put(clazz, registration);
        }
        return registration;
    }

    @SuppressWarnings("unchecked")
    public static boolean collides(Shape a, Shape b) {
        IntersectionHandler handler = handlers[a.getRegistration() + b.getRegistration() * MAX_SHAPES];
        if(handler == null) {
            return false;
        }
        return handler.intersects(a, b);
    }

    public static <A extends Shape, B extends Shape> void addHandler(Class<A> clazzA, Class<B> clazzB, IntersectionHandler<A, B> handler) {
        Integer registrationA = shapeIds.get(clazzA);
        Integer registrationB = shapeIds.get(clazzB);

        if(registrationA == null) {
            registrationA = registerShape(clazzA);
        }
        if(registrationB == null) {
            registrationB = registerShape(clazzB);
        }

        handlers[registrationA + registrationB * MAX_SHAPES] = handler;
        handlers[registrationB + registrationA * MAX_SHAPES] = handler;
    }
}
