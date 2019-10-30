package net.loganford.noideaengine.utils.annotations;

import net.loganford.noideaengine.state.entity.components.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(RegisterComponent.List.class)
public @interface RegisterComponent {
    Class<? extends Component> value();
    Argument[] arguments() default {};

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        RegisterComponent[] value();
    }
}
