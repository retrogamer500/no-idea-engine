#version 330

uniform sampler2D texDiffuse;

uniform vec3 lightDirection;
uniform vec3 lightColor;
uniform vec3 ambientLightColor;

in vec2 texCoord;
in vec3 wNormal;

layout(location=0) out vec4 fragColor;

void main(void) {
    vec3 illumination = ambientLightColor + clamp((1 - ambientLightColor) * lightColor * dot(wNormal, -normalize(lightDirection)), 0, 1);
    vec4 color = vec4(illumination, 1) * texture(texDiffuse, texCoord);
    if(color.a < .5) {
        discard;
    }
	fragColor = color;
}