#version 330

uniform sampler2D texDiffuse;

uniform vec3 lightDirection;
uniform vec3 lightColor;
uniform vec3 ambientLightColor;

in vec2 texCoord;
in vec3 wNormal;

void main(void) {
    vec3 illumination = ambientLightColor + lightColor * clamp(dot(wNormal, -normalize(lightDirection)), .2, 1);
    vec4 color = texture(texDiffuse, texCoord);
    if(color.a < .5) {
        discard;
    }
	gl_FragColor = color;
}