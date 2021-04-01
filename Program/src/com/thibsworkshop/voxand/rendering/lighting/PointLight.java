package com.thibsworkshop.voxand.rendering.lighting;

import org.joml.Vector3f;

public class PointLight extends Light{

	private Vector3f position;
	private Vector3f attenuationFactor;
	public PointLight(Vector3f position, Vector3f colour, float intensity, float attB, float attC) {
		super(colour,intensity);
		this.position = position;
		this.attenuationFactor = new Vector3f(1f/intensity,attB,attC);
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	public Vector3f getAttenuationFactor() {
		return attenuationFactor;
	}
	public void setAttenuationFactor(float attB,float attC) {
		this.attenuationFactor = new Vector3f(1f/intensity,attB,attC);
	}
	
	
	
	
	
}
