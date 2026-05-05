#version 330 core

out vec4 FragColor;
in vec3 pColor;

vec3 adjustSaturation(vec3 rgb, float adjustment) {
    // Luminance coefficients for sRGB color space
    const vec3 W = vec3(0.2125, 0.7154, 0.0721);
    vec3 intensity = vec3(dot(rgb, W));
    // Linearly interpolate between the grayscale intensity and the original color
    return mix(intensity, rgb, adjustment);
}

void main() {
    FragColor = vec4(adjustSaturation(pColor, 1.8), 1);
}