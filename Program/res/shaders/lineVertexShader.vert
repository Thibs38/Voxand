#version 400 core

in vec3 position;

uniform vec3 color;
uniform mat4 projectionViewMatrix;
uniform mat4 transformationMatrix;

out vec3 lineColor;

void main(void)
{
  vec4 worldPosition = transformationMatrix * vec4(position,1.0);

  gl_Position = projectionViewMatrix * worldPosition;
  lineColor = color;
}