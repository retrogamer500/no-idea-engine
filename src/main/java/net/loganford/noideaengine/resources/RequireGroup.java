package net.loganford.noideaengine.resources;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(RequireGroup.List.class)
public @interface RequireGroup {

    int value() default 0;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        RequireGroup[] value();
    }
}
