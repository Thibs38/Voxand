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

	private Transform transform; //Transform associated with the rigidbody
	private Vector3f min = new Vector3f(); //minimum final collider position in chunk space
	private Vector3f max = new Vector3f(); //maximum final collider position in chunk space
	private Vector3f minI = new Vector3f(); //minimum initial collider position in chunk space
	private Vector2i chunkPos = new Vector2i(); //chunk position of the min value
	private Vector2i chunkPosR = new Vector2i(); //chunk position

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
	
	public boolean detectCollision(Rigidbody rigidbody, Vector3f correction) {

		boolean collision = false; //flag
		Vector3f position = rigidbody.entity.transform.getPosition();
		float realX = position.x % Chunk.CHUNK_SIZE; //In chunk space
		float realY = position.y; 				     //In chunk space
		float realZ = position.z % Chunk.CHUNK_SIZE; //In chunk space

		/* Here we are in chunk space, meanings that the initial center position is somewhere on the local grid
		   the minimum can be on another chunk than the maximum

		 */
		minI.x = aabb.min.x + realX;
		minI.y = aabb.min.y + realY;
		minI.z = aabb.min.z + realZ;

		realX += rigidbody.velocity.x;
		realY += rigidbody.velocity.y;
		realZ += rigidbody.velocity.z;

		min.x = aabb.min.x + realX;
		min.y = aabb.min.y + realY;
		min.z = aabb.min.z + realY;

		max.x = aabb.max.x + realX;
		max.y = aabb.max.y + realY;
		max.z = aabb.max.z + realZ;

		chunkPos = Chunk.positionToChunkPos(min.x, min.z); //By default the real chunk pos is the minimum one
		for (int x = (int)Math.floor(min.x); x <= (int)Math.floor(max.x); x ++) {
			for (int z = (int)Math.floor(min.z); z <= (int)Math.floor(max.z); z ++) {
				int rx = x;
				int rz = z;
				chunkPosR.x = chunkPos.x;
				chunkPosR.y = chunkPos.y;

				if(x >= Chunk.CHUNK_SIZE){ //If we are outside of the chunk then we go to the next one
					chunkPosR.x += 1;
					rx -= Chunk.CHUNK_SIZE;
				}else if(x < 0){
					chunkPosR.x -= 1;
					rx += Chunk.CHUNK_SIZE;
				}
				if(z >= Chunk.CHUNK_SIZE){ //Same on the z axis
					chunkPosR.y += 1;
					rz -= Chunk.CHUNK_SIZE;
				}else if (z < 0){
					chunkPosR.y -= 1;
					rz += Chunk.CHUNK_SIZE;
				}

				for (int y = (int)Math.floor(min.y); y <= (int)Math.floor(max.y); y ++) {
					if (y < 0 || y > Chunk.CHUNK_HEIGHT)
						continue;

					if (TerrainManager.isTerrainSolid(rx, y, rz, chunkPosR)) {

						if ((min.x < x + 1 && max.x > x) &&
							(min.y < y + 1 && max.y > y) &&
							(min.z < z + 1 && max.z > z)) { //Collision !

							collision = true;

							boolean xtest = minI.x > x - aabb.size.x*2 && minI.x < x + 1;
							boolean ytest;
							boolean ztest = minI.z > z - aabb.size.z*2 && minI.z < z + 1;

							//TODO: optimization: test which one is generally true
							if (xtest && ztest){ //We are on the top/bottom of the cube
								if(y - min.y >= 0)
									correction.y = y - min.y - rigidbody.velocity.y;
								else
									correction.y = -y + min.y + rigidbody.velocity.y;
							}else{ //Else, we calculate the ytest
								ytest = minI.y > y - aabb.size.y*2 && minI.y < y + 1;
								if(xtest && ytest){ //We are on the front/back of the cube
									if(z - min.z >= 0)
										correction.z = z - min.z - rigidbody.velocity.z;
									else
										correction.z = -z + min.z + rigidbody.velocity.z;
								}else if (ztest && ytest){ //We are on the right/left of the cube
									if(x - min.x >= 0)
										correction.x = x - min.x - rigidbody.velocity.x;
									else
										correction.x = -x + min.x + rigidbody.velocity.x;
								}else{
									System.out.println("Collisions are broken");
								}
							}
						}
					}
				}
			}
		}
		return collision;
	}
}
