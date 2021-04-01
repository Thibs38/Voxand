package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import com.thibsworkshop.voxand.physics.Rigidbody;


//An Entity is a moving object, with a collider and a TexuredModel.
//It can be a player, a mob, a droped item, something fix but destroyable etc...
//For example a piece of grass on the map is not an entity, it as a TexturedModel, a position etc, but not a Collider

//An entity's transform is updated every frame
public class Entity extends GameObject {

	protected Rigidbody rigidbody;

	public boolean render = true;
	public boolean enabled = true;

	public Entity(TexturedModel texturedModel, Transform transform, float mass) {
		super(transform, texturedModel);
		this.rigidbody = new Rigidbody(mass,this);
	}
	public Entity(TexturedModel texturedModel, Transform transform, Rigidbody rigidbody) {
		super(transform, texturedModel);
		this.rigidbody = rigidbody;
	}

	@Override
	public void update(){
		rigidbody.update(); //Physics simulation, will move the entity & resolve collisions
		//transform.update(); //Update is done in rigidbody class
	}

	public void enableGravity(boolean enabled){
		rigidbody.gravited = enabled;
	}
	

}
