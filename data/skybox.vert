#version 330

uniform mat4 projection;

layout(location=0) in vec3 position;

out vec3 texCoord;

void main()
{
    texCoord = position;
    vec4 pos = projection * vec4(position, 1.0);
    gl_Position = pos.xyww;
}