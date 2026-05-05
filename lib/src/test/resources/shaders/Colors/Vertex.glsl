#version 330 core

layout(location = 0) in vec3 aPos;
uniform mat4 transform;

out vec3 pColor;

void main() {
    gl_Position = transform * vec4(aPos, 1.0);

    pColor = (aPos + 1) / 2;
}