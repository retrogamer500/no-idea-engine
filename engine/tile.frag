#version 330

uniform sampler2D texDiffuse;
uniform sampler2D texTileLookup;

uniform vec4 uColor;
uniform vec2 tileUv0;
uniform vec2 tileUvSize;
uniform vec2 tileSize;

in vec4 vColor;
in vec2 texCoord;
in vec2 modelPosition;

layout(location=0) out vec4 fragColor;

void main(void) {
    ivec2 tileLookupPos = ivec2(modelPosition/tileSize);
    vec4 tileLookupResult =  texelFetch(texTileLookup, tileLookupPos, 0);
    vec2 tilePos = vec2(floor(tileLookupResult.a * 65536 + tileLookupResult.b * 256), floor(tileLookupResult.g * 65536 + tileLookupResult.r * 256));

    if(tilePos.x == 0 || tilePos.y == 0) {
        discard;
    }

    vec2 samplePos = tileUv0 + (tilePos - 1 + fract(modelPosition/tileSize)) * tileUvSize;
    fragColor = texture(texDiffuse, samplePos);
}