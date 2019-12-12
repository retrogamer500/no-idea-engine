package net.loganford.noideaengine.state.lighting;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.Camera;
import net.loganford.noideaengine.systems.LightingSystem;
import org.joml.Vector3f;

public class DirectionalLight extends Light {
    @Getter @Setter private Vector3f direction = new Vector3f();

    @Override
    public float getProminence(Camera camera) {
        return Float.MAX_VALUE;
    }

    @Override
    public void buffer(LightingSystem.BufferedLight bufferedLight) {
        bufferedLight.getType().set(2);
        bufferedLight.getColor().get().set(getColor());
        bufferedLight.getDirection().get().set(direction);
    }
}
