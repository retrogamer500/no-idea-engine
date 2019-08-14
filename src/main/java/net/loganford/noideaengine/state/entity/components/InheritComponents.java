package net.loganford.noideaengine.state.entity.components;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface InheritComponents {
    boolean value() default true;
}
