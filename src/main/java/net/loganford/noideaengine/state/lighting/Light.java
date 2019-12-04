package net.loganford.noideaengine.state.lighting;

import net.loganford.noideaengine.state.Camera;
import net.loganford.noideaengine.systems.LightingSystem;

public abstract class Light {
    public abstract float getProminence(Camera camera);
    public abstract void buffer(LightingSystem.BufferedLight bufferedLight);
}
