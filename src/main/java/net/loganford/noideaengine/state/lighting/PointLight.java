package net.loganford.noideaengine.state.lighting;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.Camera;
import org.joml.Vector3f;

public class PointLight extends Light {
    @Getter @Setter private Vector3f lightColor = new Vector3f(1f, 1f, 1f);
    @Getter @Setter private float radius = 10;

    @Override
    public float getProminence(Camera camera) {
        return 0;
    }

    @Override
    protected void populate(LightingSystem.BufferedLight bufferedLight) {
        bufferedLight.type.set(0);
        bufferedLight.lightColor.get().set(lightColor);
        bufferedLight.radius.set(radius);
    }
}
