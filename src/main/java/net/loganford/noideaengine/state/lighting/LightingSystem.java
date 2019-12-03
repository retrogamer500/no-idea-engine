package net.loganford.noideaengine.state.lighting;

import lombok.Getter;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.uniformBufferObject.UniformBufferObject;
import net.loganford.noideaengine.graphics.uniformBufferObject.UniformBufferObjectBuilder;
import net.loganford.noideaengine.graphics.uniformBufferObject.UniformBufferObjectUniform;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;

public class LightingSystem implements UnsafeMemory {
    public static int MAX_LIGHTS = 32;

    @Getter private UniformBufferObject uniformBufferObject;

    private UniformBufferObjectUniform<Vector3f> lightDirection = new UniformBufferObjectUniform<>(new Vector3f(2, -10, 2).normalize());
    private UniformBufferObjectUniform<Vector3f> lightColor = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
    private UniformBufferObjectUniform<Vector3f> ambientLightColor = new UniformBufferObjectUniform<>(new Vector3f(.3f, .3f, .3f));
    private UniformBufferObjectUniform<Integer> lightCount = new UniformBufferObjectUniform<>(0);

    private BufferedLight[] bufferedLights = new BufferedLight[MAX_LIGHTS];
    private ArrayList<Light> lights = new ArrayList<>();

    public LightingSystem() {
        UniformBufferObjectBuilder builder = new UniformBufferObjectBuilder();
        builder.put(lightDirection);
        builder.put(lightColor);
        builder.put(ambientLightColor);
        builder.put(lightCount);

        for(int i = 0; i < MAX_LIGHTS; i++) {
            BufferedLight bufferedLight = new BufferedLight();

            builder.beginArrayElement();
            builder.beginStruct();
            builder.put(bufferedLight.type);
            builder.put(bufferedLight.lightColor);
            builder.put(bufferedLight.radius);
            builder.endStruct();
            builder.endArrayElement();

            bufferedLights[i] = bufferedLight;
        }

        uniformBufferObject = builder.build();
    }

    public void beforeStep() {
        lights.clear();
    }

    public void beforeRender(Renderer renderer) {
        lights.sort(Comparator.comparingDouble(l -> l.getProminence(renderer.getCamera())));

        lightCount.set(Math.min(MAX_LIGHTS, lights.size()));
        for(int i = 0; i < lightCount.get(); i++) {
            lights.get(i).populate(bufferedLights[i]);
        }

        uniformBufferObject.buffer();
    }

    @Override
    public void freeMemory() {
        uniformBufferObject.freeMemory();
    }

    public Vector3f getLightDirection() {
        return lightDirection.get();
    }

    public void setLightDirection(Vector3f lightDirection) {
        this.lightDirection.set(lightDirection);
    }

    public Vector3f getLightColor() {
        return lightColor.get();
    }

    public void setLightColor(Vector3f lightColor) {
        this.lightColor.set(lightColor);
    }

    public Vector3f getAmbientLightColor() {
        return ambientLightColor.get();
    }

    public void setAmbientLightColor(Vector3f ambientLightColor) {
        this.ambientLightColor.set(ambientLightColor);
    }

    public void addLight(Light light) {
        lights.add(light);

    }

    class BufferedLight {
        UniformBufferObjectUniform<Integer> type = new UniformBufferObjectUniform<>(0);
        UniformBufferObjectUniform<Vector3f> lightColor = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
        UniformBufferObjectUniform<Float> radius = new UniformBufferObjectUniform<>(10f);
    }
}
