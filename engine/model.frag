#version 330

struct Light {
    int type;
    vec3 color;
    vec3 position;
    float linear;
    float quadratic;
    vec3 direction;
};


uniform sampler2D texDiffuse;
layout (std140) uniform LightingUbo
{
    int lightCount;

    Light lights[32];
};


in vec2 texCoord;
in vec3 wNormal;
in vec3 wPosition;


layout(location=0) out vec4 fragColor;

void main(void) {

    vec3 illumination = vec3(0, 0, 0);
    for(int i = 0; i < lightCount; i++) {
        Light light = lights[i];
        if (light.type == 0) { //Point
            vec3 direction = normalize(light.position - wPosition);
            float distance = length(wPosition - light.position);
            float attenuation = 1 / (1 + light.linear * distance + light.quadratic * (distance * distance));
            illumination += clamp(light.color * attenuation * dot(wNormal, direction), 0, 1);
        }
        else if (light.type == 1) { //Ambient
            illumination += light.color;
        }
        else if (light.type == 2) { //Directional
            illumination += clamp(light.color * dot(wNormal, -normalize(light.direction)), 0, 1);
        }
    }

    vec4 color = vec4(illumination, 1) * texture(texDiffuse, texCoord);
    if(color.a == 0) {
        discard;
    }
	fragColor = color;
}