#version 330

uniform sampler2D texDiffuse;

uniform vec3 lightDirection;
uniform vec3 lightColor;
uniform vec3 ambientLightColor;

in vec2 texCoord;
in vec3 wNormal;

void main(void) {
    vec3 illumination = ambientLightColor + lightColor * clamp(dot(wNormal, normalize(lightDirection)), .2, 1);
	gl_FragColor = vec4(illumination, 1.0) * texture(texDiffuse, texCoord);
}