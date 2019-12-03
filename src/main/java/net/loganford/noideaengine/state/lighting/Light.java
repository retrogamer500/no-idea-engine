package net.loganford.noideaengine.state.lighting;

import net.loganford.noideaengine.state.Camera;

public abstract class Light {
    public abstract float getProminence(Camera camera);
    protected abstract void populate(LightingSystem.BufferedLight bufferedLight);
}
