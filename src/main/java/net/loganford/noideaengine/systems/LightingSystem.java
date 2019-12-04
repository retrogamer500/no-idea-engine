package net.loganford.noideaengine.systems;

import lombok.Getter;
import net.loganford.noideaengine.Game;
import net.loganford.noideaengine.components.Component;
import net.loganford.noideaengine.components.LightingComponent;
import net.loganford.noideaengine.entity.Entity;
import net.loganford.noideaengine.graphics.Renderer;
import net.loganford.noideaengine.graphics.uniformBufferObject.UniformBufferObject;
import net.loganford.noideaengine.graphics.uniformBufferObject.UniformBufferObjectBuilder;
import net.loganford.noideaengine.graphics.uniformBufferObject.UniformBufferObjectUniform;
import net.loganford.noideaengine.state.Scene;
import net.loganford.noideaengine.state.lighting.Light;
import net.loganford.noideaengine.utils.annotations.Argument;
import net.loganford.noideaengine.utils.annotations.RegisterComponent;
import net.loganford.noideaengine.utils.memory.UnsafeMemory;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RegisterComponent(LightingComponent.class)
public class LightingSystem extends ProcessEntitySystem implements UnsafeMemory {
    public static final int MAX_LIGHTS = 32;

    private int lightingComponentIndex;

    @Getter private UniformBufferObject uniformBufferObject;

    private UniformBufferObjectUniform<Vector3f> lightDirection = new UniformBufferObjectUniform<>(new Vector3f(2, -10, 2).normalize());
    private UniformBufferObjectUniform<Vector3f> lightColor = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
    private UniformBufferObjectUniform<Vector3f> ambientLightColor = new UniformBufferObjectUniform<>(new Vector3f(.3f, .3f, .3f));
    private UniformBufferObjectUniform<Integer> lightCount = new UniformBufferObjectUniform<>(0);

    private BufferedLight[] bufferedLights = new BufferedLight[MAX_LIGHTS];
    private ArrayList<Light> lights = new ArrayList<>();

    public LightingSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

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
            builder.put(bufferedLight.color);
            builder.put(bufferedLight.position);
            builder.put(bufferedLight.linear);
            builder.put(bufferedLight.quadratic);
            builder.endStruct();
            builder.endArrayElement();

            bufferedLights[i] = bufferedLight;
        }

        uniformBufferObject = builder.build();

        lightingComponentIndex = getComponentLocation(LightingComponent.class);

        setPriority(Integer.MIN_VALUE);
    }

    @Override
    public void step(Game game, Scene scene, float delta) {
        lights.clear();

        super.step(game, scene, delta);
    }

    @Override
    public void render(Game game, Scene scene, Renderer renderer) {
        lights.sort(Comparator.comparingDouble(l -> -l.getProminence(renderer.getCamera())));

        lightCount.set(Math.min(MAX_LIGHTS, lights.size()));
        for(int i = 0; i < lightCount.get(); i++) {
            lights.get(i).populate(bufferedLights[i]);
        }

        uniformBufferObject.buffer();
        renderer.setLightingUbo(uniformBufferObject);

        super.render(game, scene, renderer);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        LightingComponent lightingComponent = (LightingComponent) components.get(lightingComponentIndex);

        if(lightingComponent.getLight() != null) {
            bufferLight(lightingComponent.getLight());
        }
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

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

    public void bufferLight(Light light) {
        lights.add(light);

    }

    public class BufferedLight {
        private BufferedLight() {}

        @Getter private UniformBufferObjectUniform<Integer> type = new UniformBufferObjectUniform<>(0);
        @Getter private UniformBufferObjectUniform<Vector3f> color = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
        @Getter private UniformBufferObjectUniform<Vector3f> position = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
        @Getter private UniformBufferObjectUniform<Float> linear = new UniformBufferObjectUniform<>(10f);
        @Getter private UniformBufferObjectUniform<Float> quadratic = new UniformBufferObjectUniform<>(10f);
    }
}
