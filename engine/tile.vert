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
out vec2 modelPosition;

void main(void) {
    texCoord = uv;
    vColor = color;
    modelPosition = (model * vec4(position, 1.0)).xy;
	gl_Position = projection * view * model * vec4(position, 1.0);
}