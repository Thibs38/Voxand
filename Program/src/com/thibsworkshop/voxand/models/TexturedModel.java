package com.thibsworkshop.voxand.models;

import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.loaders.OBJLoader;
import com.thibsworkshop.voxand.physics.Collider;
import com.thibsworkshop.voxand.textures.Material;
import com.thibsworkshop.voxand.textures.Texture;

public class TexturedModel {

	private RawModel rawModel;
	private Texture texture;
	private Material material;
	public Collider collider;

	public TexturedModel(RawModel rawModel, Texture texture, Material material, Collider collider) {
		this.rawModel = rawModel;
		this.texture = texture;
		this.material = material;
		this.collider = collider;
	}

	public TexturedModel(String rawModel, String texture, Material material, Collider collider) {
		this(OBJLoader.loadObjModel(rawModel),new Texture(Loader.loadTexture(texture)),material,collider);
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public Texture getTexture() {
		return texture;
	}
	
	public Material getMaterial() {
		return material;
	}

}
