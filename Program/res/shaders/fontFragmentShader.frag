#version 400 core

varying vec2 vUV;
varying vec4 vColor;

uniform sampler2D font;


float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

void main(void) {
    vec3 pick = texture2D(font, vUV).rgb;
    float sigDist = median(pick.r, pick.g, pick.b) - 0.5;
    float alpha = clamp(sigDist/fwidth(sigDist) + 0.5, 0.0, 1.0);
    gl_FragColor = vec4(vColor.xyz, alpha);
    if (gl_FragColor.a < 0.1) discard;
}