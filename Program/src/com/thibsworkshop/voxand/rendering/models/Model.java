package com.thibsworkshop.voxand.rendering.models;

import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.loaders.OBJLoader;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.rendering.textures.Texture;

public class Model {

	private RawModel rawModel;
	private Texture texture;
	private Material material;
	
	public Model(String rawModel, String texture, Material material) {
		this.rawModel = OBJLoader.loadObjModel(rawModel);
		this.texture = Loader.loadTexture(texture);
		this.material = material;
	}

	public Model(RawModel rawModel, String texture, Material material) {
		this.rawModel = rawModel;
		this.texture = Loader.loadTexture(texture);
		this.material = material;
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