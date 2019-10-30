package net.loganford.noideaengine.utils.annotations;

public @interface Argument {
    String name();
    String stringValue() default "";
    int intValue() default 0;
    float floatValue() default 0f;
}
