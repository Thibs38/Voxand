package com.thibsworkshop.voxand.terrain;

import org.joml.Vector3f;

public class Block {
	
	public static final int MAX_BLOCK = 256;
	
	private byte id;
	
	private Vector3f color;
	private float transparency;
	private float shineDamper;
	private float reflectivity;
	
	private boolean solid;
	
	public static Block[] blocks;
	
	public Block(byte id, Vector3f color, float transparency, float shineDamper, float reflectivity, boolean solid) {
		this.id = id;
		
		this.color = color;
		this.transparency = transparency;
		this.shineDamper = shineDamper;
		this.reflectivity = reflectivity;
		
		this.solid = solid;
	}
	public byte getId() {
		return id;
	}
	public Vector3f getColor() {
		return color;
	}
	
	public float getTransparency() {
		return transparency;
	}
	public float getShineDamper() {
		return shineDamper;
	}
	public float getReflectivity() {
		return reflectivity;
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	
}
