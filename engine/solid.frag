#version 330

uniform vec4 uColor;

layout(location=0) out vec4 fragColor;

void main(void) {
	fragColor = uColor;
}