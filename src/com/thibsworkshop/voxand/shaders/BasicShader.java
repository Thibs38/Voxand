package com.thibsworkshop.voxand.shaders;

import java.util.List;

import com.thibsworkshop.voxand.lighting.DirectionalLight;
import com.thibsworkshop.voxand.lighting.Light;
import com.thibsworkshop.voxand.lighting.PointLight;
import org.joml.Vector3f;
import org.joml.Matrix4f;



public class BasicShader extends ShaderProgram{

	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int[] location_lightPosition;
	private int location_fogDensity;
	private int location_fogGradient;
	
	private int location_lightCount;
	
	private int[] location_lightColour;
	private int[] location_attenuation;
	private int location_directionalLight;
	private int location_directionalLightColor;
	private int location_ambientLight;

	private int location_skyColor;
	
	public BasicShader(String vertexFile,String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	
	protected void getAllUniformLocations() {
		location_lightPosition = new int[Light.MAX_LIGHT];
		location_lightColour = new int[Light.MAX_LIGHT];
		location_attenuation = new int[Light.MAX_LIGHT];
		//Vertex
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
	
		location_fogDensity = super.getUniformLocation("fogDensity");
		location_fogGradient = super.getUniformLocation("fogGradient");
		
		//Both
		location_lightCount = super.getUniformLocation("lightCount");
		location_directionalLight = super.getUniformLocation("directionalLight");
		location_directionalLightColor = super.getUniformLocation("directionalLightColor");
		
		for(int i = 0; i < Light.MAX_LIGHT; i++) {
			
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+i+"]");//Vertex
			location_lightColour[i] = super.getUniformLocation("lightColour["+i+"]");//Fragment
			location_attenuation[i] = super.getUniformLocation("attenuation["+i+"]");//Fragment
		}
		
		//Fragment
		location_ambientLight = super.getUniformLocation("ambientLight");
		location_skyColor = super.getUniformLocation("skyColor");

	}
	
	public void loadTransformationMatrix(Matrix4f transformation) {
		super.loadMatrix(location_transformationMatrix, transformation);	
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Matrix4f view) {
		super.loadMatrix(location_viewMatrix, view);
	}
	
	public void loadLights(PointLight[] lights) {
		for(int i = 0; i < lights.length;i++) {
			if(lights[i] != null){
				super.loadVector(location_lightPosition[i], lights[i].getPosition());
				super.loadVector(location_lightColour[i], lights[i].getColour());
				super.loadVector(location_attenuation[i], lights[i].getAttenuationFactor());
			}

		}
		super.loadInteger(location_lightCount, lights.length);
	}
	
	public void loadAmbientLight(DirectionalLight sun) {
		super.loadFloat(location_ambientLight, sun.getAmbientLight());
		super.loadVector(location_directionalLight, sun.getDirection());
		super.loadVector(location_directionalLightColor, sun.getIntensityColor());
	}
	
	public void loadFogVariables(float density, float gradient, Vector3f color) {
		super.loadVector(location_skyColor, color);
		super.loadFloat(location_fogDensity, density);
		super.loadFloat(location_fogGradient, gradient);

	}
	


	@Override
	protected void bindAttributes() {
		// TODO Auto-generated method stub
		
	}
}
