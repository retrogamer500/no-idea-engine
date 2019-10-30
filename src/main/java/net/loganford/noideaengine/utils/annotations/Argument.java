package net.loganford.noideaengine.utils.annotations;

public @interface Argument {
    String name();
    String stringValue() default "";
    int intValue() default 0;
    float floatValue() default 0f;
    boolean booleanValue() default false;
    Vector3fa vectorValue() default @Vector3fa;
    Class<?> classValue() default Void.class;
}
