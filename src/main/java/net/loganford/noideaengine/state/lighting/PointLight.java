package net.loganford.noideaengine.state.lighting;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.Camera;
import org.joml.Vector3f;

public class PointLight extends Light {
    @Getter @Setter private Vector3f color = new Vector3f(1f, 1f, 1f);
    @Getter @Setter private Vector3f position = new Vector3f();
    @Getter @Setter private float linear = 10;
    @Getter @Setter private float quadratic = 10;

    @Override
    public float getProminence(Camera camera) {
        return 0;
    }

    @Override
    protected void populate(LightingSystem.BufferedLight bufferedLight) {
        bufferedLight.type.set(0);
        bufferedLight.color.get().set(color);
        bufferedLight.position.get().set(position);
        bufferedLight.linear.set(linear);
        bufferedLight.quadratic.set(quadratic);
    }
}
