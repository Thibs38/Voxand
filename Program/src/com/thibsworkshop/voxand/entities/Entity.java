package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.models.TexturedModel;
import com.thibsworkshop.voxand.physics.Collider;
import com.thibsworkshop.voxand.physics.Rigidbody;
import com.thibsworkshop.voxand.terrain.TerrainManager;


//An Entity is a moving object, with a collider and a TexuredModel.
//It can be a player, a mob, a droped item, something fix but destroyable etc...
//For example a piece of grass on the map is not an entity, it as a TexturedModel, a position etc, but not a Collider

//An entity's transform is updated every frame
public class Entity extends GameObject {

	protected Rigidbody rigidbody;

	public boolean grounded = false;

	public Entity(TexturedModel texturedModel, Transform transform, Rigidbody rigidbody) {
		super(transform, texturedModel);
		this.rigidbody = rigidbody;
	}

	@Override
	public void update(){
		transform.update();
		chunk = TerrainManager.getChunk(transform.chunkPos);
	}
	

}
