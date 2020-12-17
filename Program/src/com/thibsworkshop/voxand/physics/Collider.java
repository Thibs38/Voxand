package com.thibsworkshop.voxand.physics;

import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.models.WireframeModel;
import com.thibsworkshop.voxand.toolbox.AABB;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Collider {

	private AABB aabb;

	private WireframeModel wireModel;

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
	
	public Vector3f detectCollision(Vector3f position) { // X axis is on the left, but mathematically that doesn't change the way things are calculated

		/*Vector3f repeal = new Vector3f(0,0,0);
		float realX = center.x + entity.position.x;
		float realY = center.y + entity.position.y;
		float realZ = center.z + entity.position.z;


		int chunkPosX = (int)(realX/(float)Terrain.CHUNK_SIZE);
		int chunkPosZ = (int)(realZ/(float)Terrain.CHUNK_SIZE);
		Key currentChunk = new Key(chunkPosX,chunkPosZ);
		if(TerrainManager.terrains.get(currentChunk) != null) {
			
			float rminX = minX + realX;
			float rminY = minY + realY;
			float rminZ = minZ + realZ;
			float rmaxX = maxX + realX;
			float rmaxY = maxY + realY;
			float rmaxZ = maxZ + realZ;
			//int heightInt = (int)Math.ceil(height);

			for(float x = realX-0.5f;x<=realX+0.5f;x+=0.5f) {

				for(float z = realZ-0.5f;z<=realZ+0.5f;z+=0.5f) {


					for(float y = realY-0.5f;y<=realY+0.5f;y+=0.5f) {
						if(y < 0 || y > Terrain.CHUNK_HEIGHT)
							continue;

						if(TerrainManager.isTerrainSolid(x,y,z)) {

							float bminX = (float) (Math.floor(x*2f)/2f);
							float bminY = (float) (Math.floor(y*2f)/2f);
							float bminZ = (float) (Math.floor(z*2f)/2f);
							float bmaxX = bminX+0.5f;
							float bmaxY = bminY+0.5f;
							float bmaxZ = bminZ+0.5f;
							//System.out.println( 
							//rminX +" <= "+bmaxX+" && "+rmaxX+" >= "+bminX+ ") && ("
							//+ rminY+" <= "+bmaxY+" && "+rmaxY+" >= "+bminY+ ") && ("
							//+ rminZ+" <= "+bmaxZ+" && "+rmaxZ+" >= "+bminZ+")");
							if((rminX < bmaxX && rmaxX > bminX) &&
							         (rminY < bmaxY && rmaxY > bminY) &&
							         (rminZ < bmaxZ && rmaxZ > bminZ)) { //Collision !
								float xDepth = bminX-rmaxX;
								float yDepth = bminY-rmaxY;
								float zDepth = bminZ-rmaxZ;


								if(Math.abs(xDepth)>Math.abs(rminX-bmaxX)) {
									xDepth = bmaxX-rminX;
								}
								if(Math.abs(yDepth)>Math.abs(rminY-bmaxY)){
									yDepth = bmaxY-rminY;
								}
								if(Math.abs(zDepth)>Math.abs(rminZ-bmaxZ)) {
									zDepth = bmaxZ-rminZ;
								}
								float absxDepth = Math.abs(xDepth);
								float absyDepth = Math.abs(yDepth);
								float abszDepth = Math.abs(zDepth);
								if(absxDepth <= 0)
									absxDepth = 1000;
								if(absyDepth <= 0)
									absyDepth = 1000;
								if(abszDepth <= 0)
									abszDepth = 1000;
								
								if(absxDepth >= 1000 && absyDepth >= 1000 && abszDepth >= 1000)
									continue;
								if(absxDepth<absyDepth) {
									if(absxDepth<abszDepth) {
										entity.translate(xDepth,0,0);
										realX += xDepth;
										rminX += xDepth;
										rmaxX += xDepth;
										chunkPosX = (int)(realX/(float)Terrain.CHUNK_SIZE);
										currentChunk = new Key(chunkPosX,chunkPosZ);

									}else {
										entity.translate(0,0,zDepth);
										realZ += zDepth;
										rminZ += zDepth;
										rmaxZ += zDepth;
										chunkPosZ = (int)(realZ/(float)Terrain.CHUNK_SIZE);
										currentChunk = new Key(chunkPosX,chunkPosZ);

									}
								}else {
									if(absyDepth<abszDepth) {
										entity.translate(0,yDepth,0);
										realY += yDepth;
										rminY += yDepth;
										rmaxY += yDepth;
										if(yDepth > 0)
											entity.grounded = true;

									}else {
										entity.translate(0,0,zDepth);
										realZ += zDepth;
										rminZ += zDepth;
										rmaxZ += zDepth;
										chunkPosZ = (int)(realZ/(float)Terrain.CHUNK_SIZE);
										currentChunk = new Key(chunkPosX,chunkPosZ);
									}
								}


							}
						}
					}
				}
			}
		}
		return repeal;*/
		return new Vector3f(0,0,0);
	}
}
