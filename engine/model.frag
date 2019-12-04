#version 330

struct Light {
    int type;
    vec3 color;
    vec3 position;
    float linear;
    float quadratic;
};


uniform sampler2D texDiffuse;
layout (std140) uniform LightingUbo
{
    vec3 lightDirection;
    vec3 lightColor;
    vec3 ambientLightColor;
    int lightCount;

    Light lights[32];
};


in vec2 texCoord;
in vec3 wNormal;
in vec3 wPosition;


layout(location=0) out vec4 fragColor;

void main(void) {
    vec3 directionalIllumination = (1 - ambientLightColor) * lightColor * dot(wNormal, -normalize(lightDirection));

    vec3 lightIllumination = vec3(0, 0, 0);
    for(int i = 0; i < 32; i++) {
        if(i < lightcount) {
            Light light = lights[i];
            if (light.type == 0) {
                vec3 direction = normalize(light.position - wPosition);
                float distance = length(wPosition - light.position);
                float attenuation = 1 / (1 + light.linear * distance + light.quadratic * (distance * distance));
                lightIllumination += (1 - ambientLightColor) * light.color * attenuation * dot(wNormal, -direction);
            }
        }
        else {
            break;
        }
    }

    vec4 totalIllumination = clamp(vec4(ambientLightColor + directionalIllumination + lightIllumination, 1), 0, 1);

    vec4 color = totalIllumination * texture(texDiffuse, texCoord);
    if(color.a < .5) {
        discard;
    }
	fragColor = color;
}