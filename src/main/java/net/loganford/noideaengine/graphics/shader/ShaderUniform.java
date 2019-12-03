package net.loganford.noideaengine.graphics.shader;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ShaderUniform {
    //Static values used to keep track of shader uniforms
    private static List<ShaderUniform> CACHED_UNIFORMS = new ArrayList<>();

    //Predefined list of shader uniforms used with the engine
    public static ShaderUniform TEX_DIFFUSE = new ShaderUniform("texDiffuse");
    public static ShaderUniform TEX_NORMAL = new ShaderUniform("texNormal");
    public static ShaderUniform TEX_SPECULAR = new ShaderUniform("texSpecular");
    public static ShaderUniform TEX_SKYBOX = new ShaderUniform("texSkybox");
    public static ShaderUniform TEX_TILE_LOOKUP = new ShaderUniform("texTileLookup");

    public static ShaderUniform MODEL = new ShaderUniform("model");
    public static ShaderUniform VIEW = new ShaderUniform("view");
    public static ShaderUniform PROJECTION = new ShaderUniform("projection");

    public static ShaderUniform COLOR = new ShaderUniform("uColor");

    public static ShaderUniform TILE_UV0 = new ShaderUniform("tileUv0");
    public static ShaderUniform TILE_UV_SIZE = new ShaderUniform("tileUvSize");
    public static ShaderUniform TILE_SIZE = new ShaderUniform("tileSize");

    public static ShaderUniform LIGHT_DIRECTION = new ShaderUniform("lightDirection");
    public static ShaderUniform LIGHT_COLOR = new ShaderUniform("lightColor");
    public static ShaderUniform AMBIENT_LIGHT_COLOR = new ShaderUniform("ambientLightColor");

    //Predefined list of UBOs
    public static ShaderUniform SCENE_UBO = new ShaderUniform("sceneUbo", true);

    /**Name of shader uniform in shader files*/
    @Getter private String name;
    /**Cache index of shader uniform*/
    @Getter private int index = -1;
    /**Whether this uniform is a UBO*/
    @Getter private boolean uniformBufferObject;

    public ShaderUniform(String name) {
        this(name, false);
    }

    public ShaderUniform(String name, boolean uniformBufferObject) {
        this.name = name;
        this.index = CACHED_UNIFORMS.size();
        CACHED_UNIFORMS.add(this);
    }

    public static List<ShaderUniform> getCachedUniforms() {
        return CACHED_UNIFORMS;
    }
}