#version 400 core

precision highp float;

// Attributes
attribute vec3 position;
attribute vec2 uv;
attribute vec4 customColor;
attribute vec4 fontUv;

// Uniforms
uniform mat4 viewProjection;
uniform mat4 transformation;

// Varying
varying vec2 vUV;
varying vec4 vColor;


void main(void) {

    gl_Position = viewProjection * transformation * vec4(position, 1.0);
    vUV = vec2(fontUv.x + uv.x * fontUv.z, fontUv.y + uv.y * fontUv.w);
    vColor = customColor;
}