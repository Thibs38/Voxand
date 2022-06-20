#version 400

in vec3 pos;
out vec4 outColor;

const vec4 skytop = vec4(0.0f,0.0f,1.0f,1.0f);
const vec4 horizon = vec4(0.3294f,0.92157f,1.0f,1.0f);
void main() {
    vec4 pointOnSphere = normalize(pos);
    float a = pointOnSphere.y;
    outColor = mix(horizon,skytop,a);
}
