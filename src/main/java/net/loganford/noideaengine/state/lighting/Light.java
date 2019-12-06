package net.loganford.noideaengine.state.lighting;

import lombok.Getter;
import lombok.Setter;
import net.loganford.noideaengine.state.Camera;
import net.loganford.noideaengine.systems.LightingSystem;
import org.joml.Vector3f;

public abstract class Light {
    @Getter @Setter private Vector3f color = new Vector3f(1f, 1f, 1f);

    public abstract float getProminence(Camera camera);
    public abstract void buffer(LightingSystem.BufferedLight bufferedLight);
}
