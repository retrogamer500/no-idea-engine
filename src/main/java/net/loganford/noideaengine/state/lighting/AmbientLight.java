package net.loganford.noideaengine.state.lighting;

import net.loganford.noideaengine.state.Camera;
import net.loganford.noideaengine.systems.LightingSystem;

public class AmbientLight extends Light {

    @Override
    public float getProminence(Camera camera) {
        return Float.MAX_VALUE;
    }

    @Override
    public void buffer(LightingSystem.BufferedLight bufferedLight) {

    }
}
