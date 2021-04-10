package com.thibsworkshop.voxand.data;

import org.joml.Vector3f;

public class Block {
	
	public static final int MAX_BLOCK = 256;
	
	private final byte id;
	
	private final Vector3f color;
	private final float transparency;
	private final float shineDamper;
	private final float reflectivity;
	
	private final boolean solid;
	
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
