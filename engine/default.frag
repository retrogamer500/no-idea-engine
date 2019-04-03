#version 330

uniform sampler2D texDiffuse;
uniform vec4 uColor;

in vec4 vColor;
in vec2 texCoord;

void main(void) {
	gl_FragColor = texture(texDiffuse, texCoord) * vColor * uColor;
}