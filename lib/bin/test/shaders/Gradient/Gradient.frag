#version 330 core

out vec4 FragColor;
in vec3 pos;
uniform float time;

float invLerp(float a, float b, float x) {
    return (x - a) / (b - a);
}

void main() {
    float t = invLerp(0.0, 1.0, pos.y); // use Y axis

    t = clamp(t, 0.0, 1.0); // prevent weird colors

    vec3 color = mix(vec3(0, 0, 0),
    mix(vec3(0, 1, 0), vec3(.5, .75, .85), clamp(t/sin(time), 0, 1)), t);

    FragColor = vec4(color, 1.0);
}