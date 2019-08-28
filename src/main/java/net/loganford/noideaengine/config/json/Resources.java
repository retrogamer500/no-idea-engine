package net.loganford.noideaengine.config.json;

import lombok.Data;

import java.util.List;

@Data
public class Resources {
    private List<ImageConfig> images;
    private List<ShaderConfig> shaders;
    private List<ModelConfig> models;
    private List<SpriteConfig> sprites;
    private List<FontConfig> fonts;
    private List<AudioConfig> audio;
    private List<ScriptConfig> scripts;
}
