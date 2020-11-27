package com.thibsworkshop.voxand.lighting;

import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;

//TODO rotation is not satifying
public class DirectionalLight extends Light {
	
	private Vector3f direction;
	private float multiplier;
	private Vector3f intensityColor;
	private float ambientLight;

	public DirectionalLight(Vector3f colour, float intensity, Vector3f direction) {
		super(colour, intensity);
		this.direction = direction;
	}
	
	public float getMultiplier() {
		return multiplier;
	}

	public Vector3f getDirection() {
		return direction;
	}
	
	public Vector3f getIntensityColor() {
		return intensityColor;
	}
	
	public float getAmbientLight() {
		return ambientLight;
	}

	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}
	
	public void rotate(Vector3f rotation) {
		direction.rotateZ(rotation.z).rotateY(rotation.y).rotateX(rotation.x);

		ambientLight = direction.y/2f;
		multiplier = Maths.sigmoid(direction.y,20);

		intensityColor = new Vector3f(colour.x*intensity*multiplier,colour.y*intensity*multiplier,colour.z*intensity*multiplier);
	}

	public void rotate(float x, float y, float z){
		if(x != 0) {
			float cosx = (float) Math.cos(x);
			float sinx = (float) Math.sin(x);
			direction.y = direction.y*cosx - direction.z*sinx;
			direction.z = direction.y*cosx + direction.z*sinx;
		}

		if(y != 0) {
			float cosy = (float) Math.cos(y);
			float siny = (float) Math.sin(y);
			direction.x = direction.x*cosy + direction.z*siny;
			direction.z = direction.z*cosy - direction.x*siny;
		}

		if(z != 0) {
			float cosz = (float) Math.cos(z);
			float sinz = (float) Math.sin(z);
			direction.x = direction.x*cosz - direction.y*sinz;
			direction.y = direction.x*sinz + direction.y*cosz;
		}

		direction.normalize();

		ambientLight = direction.y/2f;
		multiplier = Maths.sigmoid(direction.y,20);

		intensityColor = new Vector3f(colour.x*intensity*multiplier,colour.y*intensity*multiplier,colour.z*intensity*multiplier);
	}
	
	
	
	
	
	
	

}
