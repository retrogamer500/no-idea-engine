#version 330

uniform sampler2D texDiffuse;
uniform vec4 uColor;

in vec4 vColor;
in vec2 texCoord;

layout(location=0) out vec4 fragColor;

void main(void) {
	fragColor = texture(texDiffuse, texCoord) * vColor * uColor;
}