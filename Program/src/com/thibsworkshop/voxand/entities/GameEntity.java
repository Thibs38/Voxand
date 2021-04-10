package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import com.thibsworkshop.voxand.physics.Rigidbody;


//An Entity is a moving object, with a collider and a TexuredModel.
//It can be a player, a mob, a droped item, something fix but destroyable etc...
//For example a piece of grass on the map is not an entity, it as a TexturedModel, a position etc, but not a Collider

//An entity's transform is updated every frame
public class GameEntity extends GameObject {

	public Rigidbody rigidbody;

	/**
	 * Render the entity?
	 */
	public boolean doRendering = true;
	/**
	 * Do entity vs entity collisions?
	 */
	public boolean doEntityCollisions = true;
	/**
	 * Do update (collisions / movement)?
	 */
	public boolean doUpdate = true;
	/**
	 * Is entity enabled?
	 */
	private boolean enabled = true;

	public boolean entityCollisionsDone = false;

	public GameEntity(TexturedModel texturedModel, float mass) {
		super(texturedModel);
		this.rigidbody = new Rigidbody(mass,this);
	}
	public GameEntity(TexturedModel texturedModel, Rigidbody rigidbody) {
		super(texturedModel);
		this.rigidbody = rigidbody;
	}

	@Override
	public void update(){
		rigidbody.update(); //Physics simulation, will move the entity & resolve collisions
		//transform.update(); //Update is done in rigidbody class
	}

	@Override
	public void lateUpdate(){
		transform.lateUpdate();
	}

	public void enableGravity(boolean enabled){
		rigidbody.gravited = enabled;
	}

	/**
	 * Totally disables the entity (update / collisions / rendering)
	 */
	public void disableEntity(){
		enabled = false;
		doUpdate = false;
		doRendering = false;
		doEntityCollisions = false;
	}

	/**
	 * Totally enables the entity (update / collisions / rendering)
	 */
	public void enableEntity(){
		enabled = true;
		doUpdate = true;
		doRendering = true;
		doEntityCollisions = true;
	}
	

}
