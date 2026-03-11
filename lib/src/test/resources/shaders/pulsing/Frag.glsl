#version 330 core

in vec2 v_uv;
uniform float u_scale;

out vec4 FragColor; // Modern output

void main() {
    vec2 scaled_uv = v_uv * u_scale;
    vec2 pos = floor(scaled_uv);
    float checker_value = mod(pos.x + pos.y, 2.0);

    vec3 color1 = vec3(0.0); // Black
    vec3 color2 = vec3(1.0); // White

    FragColor = vec4(mix(color1, color2, checker_value), 1.0);
}