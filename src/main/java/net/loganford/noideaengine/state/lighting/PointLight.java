package net.loganford.noideaengine.state.lighting;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.Camera;
import net.loganford.noideaengine.systems.LightingSystem;
import net.loganford.noideaengine.utils.math.MathUtils;
import org.joml.Vector3f;

public class PointLight extends Light {
    public static final float[] LIGHT_DISTANCE = {1, 7, 13, 20, 32, 50, 65, 100, 160, 200, 325, 600, 3250};
    public static final float[] LINEAR_VALUE = {10, 0.7f, 0.35f, 0.22f, 0.14f, 0.09f, 0.07f, 0.045f, 0.027f, 0.022f, 0.014f, 0.007f, 0.0014f};
    public static final float[] QUADRATIC_VALUE = {30, 1.8f, 0.44f, 0.20f, 0.07f, 0.032f, 0.017f, 0.0075f, 0.0028f, 0.0019f, 0.0007f, 0.0002f, 0.000007f};

    @Getter @Setter private Vector3f position = new Vector3f();
    @Getter @Setter private float radius = 10;

    @Override
    public float getProminence(Camera camera) {
        return 1f/MathUtils.distance(position, camera.getPosition()) * radius;
    }

    @Override
    public void buffer(LightingSystem.BufferedLight bufferedLight) {
        bufferedLight.getType().set(0);
        bufferedLight.getColor().get().set(getColor());
        bufferedLight.getPosition().get().set(position);
        bufferedLight.getLinear().set(MathUtils.interpolate(radius, LIGHT_DISTANCE, LINEAR_VALUE));
        bufferedLight.getQuadratic().set(MathUtils.interpolate(radius, LIGHT_DISTANCE, QUADRATIC_VALUE));
    }
}
