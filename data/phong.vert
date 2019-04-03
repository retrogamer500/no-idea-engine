#version 330

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout(location=0) in vec3 position;
layout(location=1) in vec3 normal;
layout(location=2) in vec2 uv;

out vec2 texCoord;
out vec3 wNormal;

void main(void) {
    texCoord = uv;
    wNormal = (model * vec4(normal, 0)).xyz;
	gl_Position = projection * view * model * vec4(position, 1.0);
}