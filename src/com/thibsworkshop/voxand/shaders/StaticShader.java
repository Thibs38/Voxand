package com.thibsworkshop.voxand.shaders;

public class StaticShader extends BasicShader{

	private static final String VERTEX_FILE = "res/shaders/vertexShader";
	private static final String FRAGMENT_FILE = "res/shaders/fragmentShader";

	private int location_shineDamper;
	private int location_reflectivity;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		
		// TODO Auto-generated constructor stub
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

		location_shineDamper = super.getUniformLocation("shineDamper");
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_reflectivity, reflectivity);
		super.loadFloat(location_shineDamper, damper);

	}
}
