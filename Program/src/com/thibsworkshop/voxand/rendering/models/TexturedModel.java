package com.thibsworkshop.voxand.rendering.models;

import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.loaders.OBJLoader;
import com.thibsworkshop.voxand.physics.collisions.Collider;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.rendering.textures.Texture;

public class TexturedModel {

	private final RawModel rawModel;
	private final Texture texture;
	private final Material material;
	public Collider collider;

	public TexturedModel(RawModel rawModel, Texture texture, Material material, Collider collider) {
		this.rawModel = rawModel;
		this.texture = texture;
		this.material = material;
		this.collider = collider;
	}

	public TexturedModel(String rawModel, String texture, Material material, Collider collider) {
		this(OBJLoader.loadObjModel(rawModel),Loader.loadTexture(texture),material,collider);
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
