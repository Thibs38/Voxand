package com.thibsworkshop.voxand.rendering.lighting;

import org.joml.Vector3f;

public class Light {
	
	public static final int MAX_LIGHT = 16;

	public Vector3f colour;

	public float intensity;
	
	public Light(Vector3f colour, float intensity) {
		this.colour = colour;
		this.intensity = intensity;
	}

}
