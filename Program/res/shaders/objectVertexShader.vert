#version 400 core
//OPTIMIZE Premultiply projection and view matrix
//OPTIMIZE Preupload position as vec4
const int MAX_LIGHTS = 16;

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 unitNormal;
out vec3 lightVector[MAX_LIGHTS];
out vec3 directionalLightFinalColour;
out vec3 directionalLightReflected;
out vec3 toCameraVector;
out float visibility;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];
uniform vec3 directionalLight;
uniform vec3 directionalLightColor;

uniform int lightCount;

uniform float fogDensity;
uniform float fogDistance;
void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position,1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	pass_textureCoords = textureCoords;
	unitNormal = normalize((transformationMatrix * vec4(normal,0.0)).xyz);
	
	for(int i = 0; i < lightCount;i++){
		lightVector[i] = lightPosition[i] - worldPosition.xyz;	
	}
	directionalLightFinalColour = max(dot(unitNormal, directionalLight),0)*directionalLightColor;
	directionalLightReflected = reflect(-directionalLight,unitNormal);
	
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;

	visibility = clamp(exp((fogDistance - length(toCameraVector.xz) - 0.2 * toCameraVector.y) * fogDensity),0.0,1.0);
}