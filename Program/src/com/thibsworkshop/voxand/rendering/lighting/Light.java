package com.thibsworkshop.voxand.rendering.lighting;

import org.joml.Vector3f;

public class Light {
	
	public static final int MAX_LIGHT = 16;

	protected Vector3f colour;

	protected float intensity;	
	
	public Light(Vector3f colour, float intensity) {
		this.colour = colour;
		this.intensity = intensity;
	}
	public Vector3f getColour() {
		return colour;
	}
	public void setColour(Vector3f colour) {
		this.colour = colour;
	}
	
	public float getIntensity() {
		return intensity;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
