package net.loganford.noideaengine.components;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.utils.annotations.AnnotationUtil;
import net.loganford.noideaengine.utils.annotations.Argument;

public class ThirdPersonCameraComponent extends AbstractCameraComponent {
    @Getter @Setter private float distance = 10f;
    @Getter @Setter private boolean limitY = true;
    @Getter @Setter private float limitYAmount = -1;

    public ThirdPersonCameraComponent(Argument[] args) {
        super(args);

        AnnotationUtil.getArgumentOptional("distance", args).ifPresent((a) -> distance = a.floatValue());
        AnnotationUtil.getArgumentOptional("limitY", args).ifPresent((a) -> limitY = a.booleanValue());
        AnnotationUtil.getArgumentOptional("limitYAmount", args).ifPresent((a) -> limitYAmount = a.floatValue());
    }
}
