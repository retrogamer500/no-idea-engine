#version 330

uniform samplerCube skybox;

in vec3 texCoord;

void main()
{
    gl_FragColor = texture(skybox, texCoord);
}