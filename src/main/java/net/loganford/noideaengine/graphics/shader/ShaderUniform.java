package net.loganford.noideaengine.graphics.shader;

import lombok.Getter;

public enum ShaderUniform {

    TEX_DIFFUSE("texDiffuse"),
    TEX_NORMAL("texNormal"),
    TEX_SPECULAR("texSpecular"),
    TEX_TILE_LOOKUP("texTileLookup"),

    MODEL("model"),
    VIEW("view"),
    PROJECTION("projection"),

    COLOR("uColor"),

    TILE_UV0("tileUv0"),
    TILE_UV_SIZE("tileUvSize"),
    TILE_SIZE("tileSize"),

    LIGHT_DIRECTION("lightDirection"),
    LIGHT_COLOR("lightColor"),
    AMBIENT_LIGHT_COLOR("ambientLightColor"),
    ;


    @Getter private final String uniformName;

    ShaderUniform(String uniformName) {
        this.uniformName = uniformName;
    }
}
