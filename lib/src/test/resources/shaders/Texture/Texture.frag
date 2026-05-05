#version 330 core

in vec2 texCoord;
out vec4 FragColor;

uniform sampler2D tex;
uniform vec2 size = vec2(1, 1);

void main() {
    FragColor = texture(tex, texCoord*size);
}