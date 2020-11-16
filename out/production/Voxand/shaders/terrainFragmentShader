#version 400 core

const int MAX_LIGHTS = 16;

in vec4 color_frag;
in float shineDamper_frag;
in float reflectivity_frag;

in vec3 unitNormal;
in vec3 lightVector[MAX_LIGHTS];
in vec3 directionalLightFinalColour;
in vec3 directionalLightReflected;

in vec3 toCameraVector;

in float visibility;

out vec4 outColor;


uniform float ambientLight;
uniform vec3 skyColor;

uniform vec3 lightColour[MAX_LIGHTS];
uniform vec3 attenuation[MAX_LIGHTS];
uniform int lightCount;
uniform vec3 directionalLightColour;
uniform vec3 directionalLightDirection;

vec3 calculateDiffuse(vec3 unitNormal, vec3 unitLightVector, vec3 lightColour, float attFactor){
	float nDotl = dot(unitNormal,unitLightVector);
	float brightness = max(nDotl,0);
	return (brightness * lightColour) / attFactor;
}


vec3 calculateSpecular(vec3 unitNormal, vec3 unitLightVector, vec3 unitToCameraVector, float shineDamper, float reflectivity, vec3 lightColour, float attFactor){
	vec3 reflectedLightDirection = reflect(-unitLightVector,unitNormal);
	float specularFactor = max(dot(reflectedLightDirection, unitToCameraVector),0.0);
	float dampedFactor = pow(specularFactor,shineDamper);
	return (dampedFactor * reflectivity * lightColour) / attFactor;
}

void main(void){

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
		
	vec3 unitToCameraVector = normalize(toCameraVector);

	for(int i = 0; i < lightCount;i++){
		vec3 unitLightVector = normalize(lightVector[i]);
		float lightDistance = length(lightVector[i]);
		
		float attFactor = attenuation[i].x + attenuation[i].y*lightDistance + attenuation[i].z*lightDistance*lightDistance;
		
		totalSpecular += calculateSpecular(unitNormal, unitLightVector, unitToCameraVector, shineDamper_frag, reflectivity_frag, lightColour[i], attFactor);
		totalDiffuse += calculateDiffuse(unitNormal, unitLightVector, lightColour[i], attFactor);
	}
	
	totalDiffuse += directionalLightFinalColour;
	totalSpecular += pow(max(dot(directionalLightReflected,unitToCameraVector),0.0),shineDamper_frag)*reflectivity_frag*directionalLightColour;
	totalDiffuse = max(totalDiffuse,ambientLight);
	
	outColor = vec4(totalDiffuse,1.0) * color_frag + vec4(totalSpecular,1.0);
	outColor = mix(vec4(skyColor,1.0),outColor,visibility);
}