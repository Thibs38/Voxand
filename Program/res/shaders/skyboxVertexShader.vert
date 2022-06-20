#version 400

in vec3 position;

out vec3 pos;

uniform mat4 projectionMatrix;
uniform float scale;

void main() {
    vec4 worldPosition = vec4(position,1.0);
    vec4 pos4 = projectionMatrix * worldPosition;
    pos = pos4.xyz;
    gl_Position = pos;
}
