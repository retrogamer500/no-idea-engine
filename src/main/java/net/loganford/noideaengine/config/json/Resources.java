package net.loganford.noideaengine.config.json;

import lombok.Data;

import java.util.List;

@Data
public class Resources {
    private List<ImageConfig> images;
    private List<TextureConfig> textures;
    private List<ShaderConfig> shaders;
    private List<ModelConfig> models;
    private List<SpriteConfig> sprites;
    private List<FontConfig> fonts;
    private List<AudioConfig> audio;
    private List<ScriptConfig> scripts;
    private List<EntityConfig> entities;
    private List<CubeMapConfig> cubeMaps;
}
