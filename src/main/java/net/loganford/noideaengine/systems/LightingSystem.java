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
    private UniformBufferObjectUniform<Integer> lightCount = new UniformBufferObjectUniform<>(0);

    private BufferedLight[] bufferedLights = new BufferedLight[MAX_LIGHTS];
    private ArrayList<Light> lights = new ArrayList<>();

    public LightingSystem(Game game, Scene scene, Argument[] args) {
        super(game, scene, args);

        UniformBufferObjectBuilder builder = new UniformBufferObjectBuilder();
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
            builder.put(bufferedLight.direction);
            builder.endStruct();
            builder.endArrayElement();

            bufferedLights[i] = bufferedLight;
        }

        uniformBufferObject = builder.build();

        lightingComponentIndex = getComponentLocation(LightingComponent.class);

        setPriority(-Float.MAX_VALUE);
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
            lights.get(i).buffer(bufferedLights[i]);
        }

        uniformBufferObject.buffer();
        renderer.setLightingUbo(uniformBufferObject);

        super.render(game, scene, renderer);
    }

    @Override
    protected void processEntity(Entity entity, List<Component> components, Game game, Scene scene, float delta) {
        LightingComponent lightingComponent = (LightingComponent) components.get(lightingComponentIndex);

        if(lightingComponent.getLight() != null) {
            processLight(lightingComponent.getLight());
        }
    }

    @Override
    protected void renderEntity(Entity entity, List<Component> components, Game game, Scene scene, Renderer renderer) {

    }

    @Override
    public void freeMemory() {
        uniformBufferObject.freeMemory();
    }

    public void processLight(Light light) {
        lights.add(light);
    }

    public class BufferedLight {
        private BufferedLight() {}

        @Getter private UniformBufferObjectUniform<Integer> type = new UniformBufferObjectUniform<>(0);
        @Getter private UniformBufferObjectUniform<Vector3f> color = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
        @Getter private UniformBufferObjectUniform<Vector3f> position = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
        @Getter private UniformBufferObjectUniform<Float> linear = new UniformBufferObjectUniform<>(10f);
        @Getter private UniformBufferObjectUniform<Float> quadratic = new UniformBufferObjectUniform<>(10f);
        @Getter private UniformBufferObjectUniform<Vector3f> direction = new UniformBufferObjectUniform<>(new Vector3f(1f, 1f, 1f));
    }
}
