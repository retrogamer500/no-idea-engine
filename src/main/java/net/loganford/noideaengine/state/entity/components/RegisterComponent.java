package net.loganford.noideaengine.state.entity.components;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(RegisterComponent.List.class)
public @interface RegisterComponent {
    Class<? extends Component> value();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @interface List {
        RegisterComponent[] value();
    }
}