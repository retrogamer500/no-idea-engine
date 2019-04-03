#version 330

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout(location=0) in vec3 position;
layout(location=1) in vec3 normal;
layout(location=2) in vec2 uv;
layout(location=3) in vec4 color;

out vec2 texCoord;
out vec4 vColor;

void main(void) {
    texCoord = uv;
    vColor = color;
	gl_Position = projection * view * model * vec4(position, 1.0);
}