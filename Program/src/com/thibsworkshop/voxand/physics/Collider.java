package com.thibsworkshop.voxand.physics;

import com.thibsworkshop.voxand.entities.Transform;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.toolbox.AABB;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Collider {

	private final AABB aabb;

	private final Vector3i aura; // Represents on each axis the maximum number of blocks the collider can touch

	private WireframeModel wireModel;

	private static final CollisionEngine collisionEngine = CollisionEngine.engine;

	public Collider(AABB aabb) {
		this.aabb = aabb;
		this.aura = new Vector3i(
				Maths.ceil(aabb.size.x) + 1,
				Maths.ceil(aabb.size.y) + 1,
				Maths.ceil(aabb.size.z) + 1);
	}

	public void detectCollision(Transform transform, Vector3f movement){
		collisionEngine.entityVSterrain(transform, movement, aabb, aura);
	}

	public boolean isGrounded(Transform transform){
		return collisionEngine.isGrounded(transform,aabb);
	}
	public void createWireframe(){
		if(wireModel == null)
			wireModel = new WireframeModel(aabb, Color.green);
	}

	public void destroyWireframe(){
		wireModel = null;
	}

	public WireframeModel getWireframeModel(){ return wireModel; }

	public AABB getAabb(){ return aabb; }

}
