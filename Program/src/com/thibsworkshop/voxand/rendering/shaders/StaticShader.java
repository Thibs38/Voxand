package com.thibsworkshop.voxand.rendering.shaders;

import org.joml.Matrix4f;

public class StaticShader extends BasicShader{

	private static final String VERTEX_FILE = "objectVertexShader.vert";
	private static final String FRAGMENT_FILE = "objectFragmentShader.frag";

	private int location_shineDamper;
	private int location_reflectivity;
	private int location_transformationMatrix;


	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		//Fragment
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");

		location_shineDamper = super.getUniformLocation("shineDamper");
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_reflectivity, reflectivity);
		super.loadFloat(location_shineDamper, damper);
	}

	public void loadTransformationMatrix(Matrix4f transformation) {
		super.loadMatrix(location_transformationMatrix, transformation);
	}
}
