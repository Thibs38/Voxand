#version 400 core

in vec3 position;

uniform vec3 color;
uniform mat4 projectionViewMatrix;
uniform vec3 linePosition;
uniform vec3 lineScale;

out vec3 lineColor;

void main(void)
{
  gl_Position = projectionViewMatrix * vec4(position*lineScale + linePosition,1.0);
  lineColor = color;
}