package com.thibsworkshop.voxand.physics;

import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.entities.Transform;
import com.thibsworkshop.voxand.models.WireframeModel;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.AABB;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;

public class Collider {

	private AABB aabb;

	private WireframeModel wireModel;

	private Vector3f normal = new Vector3f();


	public Collider(AABB aabb) {
		this.aabb = aabb;
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
	
	public boolean detectCollision(Rigidbody rigidbody) {

		Transform transform = rigidbody.entity.transform;
		Vector3f velocity = rigidbody.velocity;

		float minTime = collisionLoop(transform,velocity);
		//System.out.println("vx: " + velocity.x + " vy: " + velocity.y + " vz: " + velocity.z);

		//System.out.println(minTime);
		transform.translate(velocity.x * minTime,velocity.y * minTime, velocity.z * minTime);
		float remainingTime = 1.0f - minTime;
		if(remainingTime <= 0) return false;

		/* We calculated the correct position of the object, now we need to apply a response: sliding.
		   We first checked the remaining time, or the "velocity left". If it is null, then we stop here.
		   Else, we need to project the remaining velocity on the plane on which we collided: this is the new
		   velocity.
		   Then we have to do another collision test, and move the entity accordingly.
		   However, there are 3 possible normals, so a common case would be: we are moving diagonally on the x and z
		   axis, and the gravity is pushing us down. First pass would certainly be pushing the entity up, so the remaining
		   velocity is projected on the x-z plane, but then if we are close to a wall, we might be colliding with it
		   when applying the new velocity, and there might remain some velocity that needs to be projected either on the x
		   axis or z axis depending on the collision normal.
		   So we need to apply the sliding algorithm twice, and update the remaining velocity accordingly.
		 */

		for(int i = 0; i < 2; i++){
			if(remainingTime > 0) {
				float dotProd = velocity.dot(normal);
				velocity.set(
						velocity.x * remainingTime - dotProd * normal.x,
						velocity.y * remainingTime - dotProd * normal.y,
						velocity.z * remainingTime - dotProd * normal.z);

				minTime = collisionLoop(transform, velocity);

				transform.translate(velocity.x * minTime, velocity.y * minTime, velocity.z * minTime);
				remainingTime -= minTime;
			}
		}

		return true;
	}

	private Vector3f real = new Vector3f(); //player position in chunk space
	private Vector3f min = new Vector3f(); //minimum final collider position in chunk space
	private Vector3f max = new Vector3f(); //maximum final collider position in chunk space
	private Vector3f minI = new Vector3f(); //minimum initial collider position in chunk space
	private Vector3f maxI = new Vector3f(); //maximum initial collider position in chunk space
	private Vector2i chunkPosR = new Vector2i(); //chunk position

	private float collisionLoop(Transform transform, Vector3f velocity){

		Vector3f positionI = transform.getPosition(); //initial global position

		/* Here we are in chunk space, meanings that the initial center position is somewhere on the local grid
		   the minimum can be on another chunk than the maximum.
		   We first translate the player, then check collisions, and if there is one we apply the correction and
		   continue calculating.
		 */
		real.set((float)Maths.floatMod(positionI.x,Chunk.D_CHUNK_SIZE),
				positionI.y,
				(float)Maths.floatMod(positionI.z, Chunk.D_CHUNK_SIZE));

		minI.set(aabb.min.x + real.x, aabb.min.y + real.y, aabb.min.z + real.z);
		maxI.set(aabb.max.x + real.x, aabb.max.y + real .y, aabb.max.z + real.z);

		real.add(velocity);

		min.set(aabb.min.x + real.x, aabb.min.y + real.y, aabb.min.z + real.z);
		max.set(aabb.max.x + real.x, aabb.max.y + real.y, aabb.max.z + real.z);

		float minTime = 1.0f;
		Vector2i chunkPos = transform.chunkPos; //By default the real chunk pos is the initial player one

		for (int x = (int)Math.floor(Math.min(min.x,minI.x)); x <= (int)Math.floor(Math.max(max.x,maxI.x)); x ++) {
			int rx = Math.floorMod(x, Chunk.CHUNK_SIZE);
			chunkPosR.x = chunkPos.x + Math.floorDiv(x, Chunk.CHUNK_SIZE);
			
			for (int z = (int) Math.floor(Math.min(min.z,minI.z)); z <= (int) Math.floor(Math.max(max.z,maxI.z)); z++) {
				int rz = Math.floorMod(z, Chunk.CHUNK_SIZE);
				chunkPosR.y = chunkPos.y + Math.floorDiv(z, Chunk.CHUNK_SIZE);

				for (int y = (int) Math.floor(Math.min(min.y,minI.y)); y <= (int) Math.floor(Math.max(max.y,maxI.y)); y++) {
					if (y >= 0 && y < Chunk.CHUNK_HEIGHT && TerrainManager.isTerrainSolid(rx, y, rz, chunkPosR)){
						//System.out.println("rx: " + rx + " ry: " + y + " rz: " + rz + " chunkx: " + chunkPosR.x + " chunky: " + chunkPosR.y);
						float collisionTime = sweptAABB(velocity,x,y,z,minTime);
						if(collisionTime < minTime)
							minTime = collisionTime;
					}
				}
			}
		}
		return minTime;
	}

	private Vector3f invEntry = new Vector3f();
	private Vector3f invExit = new Vector3f();
	private Vector3f entry = new Vector3f();
	private Vector3f exit = new Vector3f();

	private float sweptAABB(Vector3f velocity, float x, float y, float z, float minTime){
		// find the distance between the objects on the near and far sides for both x, y and z
		// and find time of collision and time of leaving for each axis
		if (velocity.x > 0.0f) {
			invEntry.x = x - maxI.x;
			entry.x = invEntry.x / velocity.x;
			invExit.x = x + 1 - minI.x;
			exit.x = invExit.x / velocity.x;
		} else if (velocity.x < 0.0f){
			invEntry.x = x + 1 - minI.x;
			entry.x = invEntry.x / velocity.x;
			invExit.x = x - maxI.x;
			exit.x = invExit.x / velocity.x;
		} else {
			invEntry.x = x + 1 - minI.x;
			invExit.x = x - maxI.x;
			entry.x = -Float.MAX_VALUE;
			exit.x = Float.MAX_VALUE;
		}

		if (velocity.y > 0.0f) {
			invEntry.y = y - maxI.y;
			entry.y = invEntry.y / velocity.y;
			invExit.y = y + 1 - minI.y;
			exit.y = invExit.y / velocity.y;
		} else if (velocity.y < 0.0f){
			invEntry.y = y + 1 - minI.y;
			entry.y = invEntry.y / velocity.y;
			invExit.y = y - maxI.y;
			exit.y = invExit.y / velocity.y;
		} else {
			invEntry.y = y + 1 - minI.y;
			invExit.y = y - maxI.y;
			entry.y = -Float.MAX_VALUE;
			exit.y = Float.MAX_VALUE;
		}

		if (velocity.z > 0.0f) {
			invEntry.z = z - maxI.z;
			entry.z = invEntry.z / velocity.z;
			invExit.z = z + 1 - minI.z;
			exit.z = invExit.z / velocity.z;
		} else if (velocity.z < 0.0f){
			invEntry.z = z + 1 - minI.z;
			entry.z = invEntry.z / velocity.z;
			invExit.z = z - maxI.z;
			exit.z = invExit.z / velocity.z;
		} else {
			invEntry.z = z + 1 - minI.z;
			invExit.z = z - maxI.z;
			entry.z = -Float.MAX_VALUE;
			exit.z = Float.MAX_VALUE;
		}
		// find the earliest/latest times of collision
		//if (entry.x > 1.0f) entry.x = -Float.MAX_VALUE;
		//if (entry.y > 1.0f) entry.y = -Float.MAX_VALUE;
		//if (entry.z > 1.0f) entry.z = -Float.MAX_VALUE;

		float entryTime = Math.max(Math.max(entry.x,entry.z),entry.y);

		System.out.println("Checking collisions!");
		if(entryTime >= minTime || entryTime < 0) return 1.0f;

		float exitTime = Math.min(Math.min(exit.x,exit.z),exit.y);

		if (entryTime > exitTime) return 1.0f;

		if(entry.x > 1.0f) {
			if(max.x < x || min.x > x + 1)
				return 1.0f;
		}

		if(entry.y > 1.0f){
			if(max.y < y || min.y > y + 1)
				return 1.0f;
		}

		if(entry.z > 1.0f){
			if(max.z < z || min.z > z + 1)
				return 1.0f;
		}


		if (entry.x > entry.y) {
			if(entry.x > entry.z){
				normal.x = -Maths.sign(velocity.x);
				normal.y = 0.0f;
				normal.z = 0.0f;
			}else{
				normal.x = 0.0f;
				normal.y = 0.0f;
				normal.z = -Maths.sign(velocity.z);
			}
		} else {
			if (entry.y > entry.z) {
				normal.x = 0.0f;
				normal.y = -Maths.sign(velocity.y);
				normal.z = 0.0f;
			} else {
				normal.x = 0.0f;
				normal.y = 0.0f;
				normal.z = -Maths.sign(velocity.z);
			}
		}
		return entryTime;
	}
}
