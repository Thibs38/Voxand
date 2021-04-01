#version 400 core
const vec4 normals[] = vec4[6](vec4(1,0,0,0),vec4(0,1,0,0),vec4(0,0,1,0),vec4(-1,0,0,0),vec4(0,-1,0,0),vec4(0,0,-1,0));
const int MAX_LIGHTS = 16;
const int MAX_BLOCKS = 256;

struct Block
{
    vec4 color;
    float shineDamper;
    float reflectivity;
};

in vec3 position;
in int block_id;
in int normal;


out vec4 color_frag;
out float shineDamper_frag;
out float reflectivity_frag;

out vec3 unitNormal;
out vec3 lightVector[MAX_LIGHTS];
out vec3 directionalLightFinalColour;
out vec3 directionalLightReflected;
out vec3 toCameraVector;
out float visibility;

uniform Block blocks[MAX_BLOCKS];

uniform vec3 chunkPosition;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[MAX_LIGHTS];
uniform vec3 directionalLight;
uniform vec3 directionalLightColor;

uniform int lightCount;

uniform float fogDensity;
uniform float fogDistance;

void main(void){

	vec4 worldPosition = vec4(position + chunkPosition,1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	
	color_frag = blocks[block_id].color;
	shineDamper_frag = blocks[block_id].shineDamper;
	reflectivity_frag = blocks[block_id].reflectivity;
	
	unitNormal = normalize(normals[normal].xyz);
	
	for(int i = 0; i < lightCount;i++){
		lightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	directionalLightFinalColour = max(dot(unitNormal, directionalLight),0)*directionalLightColor;
	directionalLightReflected = reflect(-directionalLight,unitNormal);
	
	toCameraVector = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;

	visibility = clamp(exp((fogDistance - length(toCameraVector.xz)-0.2*toCameraVector.y)*fogDensity),0.0,1.0);
}