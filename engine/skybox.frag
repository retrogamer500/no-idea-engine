#version 330

uniform samplerCube texSkybox;

in vec3 texCoord;

layout(location=0) out vec4 fragColor;

void main()
{
    fragColor = texture(texSkybox, texCoord);
}