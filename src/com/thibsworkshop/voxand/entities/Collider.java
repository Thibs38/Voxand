package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Collider {

	private Vector3f center; //Local center
	private float height;
	private float radius;
	private float minX;
	private float maxX;
	private float minY;
	private float maxY;
	private float minZ;
	private float maxZ;
	private Entity entity;
	boolean printed = false;
	public Collider(Vector3f center, float height, float radius) {
		this.center = center;
		this.height = height;
		this.radius = radius;
		this.minX = -radius;
		this.minY = 0;
		this.minZ = -radius;
		this.maxX = radius;
		this.maxY = height;
		this.maxZ = radius;
		this.entity = null;
	}
	
	public void LinkEntity(Entity entity) {
		this.entity = entity;
	}
	
	public Vector3f detectCollision() { // X axis is on the left, but mathematically that doesn't change the way things are calculated

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

	
	
	public static int sphereToPlaneCollision(Vector3f planeNorm, float planeOff, Vector3f spherePos, float sphereRad)
	{
	    float dot = planeNorm.dot(spherePos) - planeOff;
	 
	    if(dot > sphereRad)
	        return 1; // The sphere is in front of the plane
	    else if(dot < -sphereRad)
	        return 2; // The sphere is behind the plane
	 
	    return 3; // The sphere collides/straddles with the plane
	}

	
	
	/*public static int boxToPlaneCollision(Vector3f planeNorm, float planeOff, Vector3f boxMin, Vector3f boxMax)
	{
	    // Get the Extense vector
	    Vector3f E = Vector3f.sub(boxMax, boxMin,null);
	    
	    E.scale(0.5f);
	    
	    // Get the center of the Box
	    Vector3f center = Vector3f.add(boxMin, E, null);
	    
	 	 
	    // Dot Product between the plane normal and the center of the Axis Aligned Box
	    // using absolute values
	    float fRadius = Math.abs(planeNorm.x*E.x) + Math.abs(planeNorm.y*E.y) + Math.abs(planeNorm.z*E.z);
	 
	    return sphereToPlaneCollision( planeNorm,planeOff,center,fRadius );
	}
	
	public static boolean sphereFrontOfPlane(Vector3f planeNorm, float planeOff, Vector3f spherePos, float sphereRad)
	{
	    float dot = Vector3f.dot(planeNorm,spherePos) - planeOff;

	    return dot > -sphereRad;

	}
	
	public static boolean boxFrontOfPlane(Plane plane, Vector3f boxMin, Vector3f boxMax)
	{
		// Get the Extense vector
	    Vector3f E = Vector3f.sub(boxMax, boxMin,null);

	    E.scale(0.5f);
	    
	    // Get the center of the Box
	    Vector3f center = Vector3f.add(boxMin, E, null);
	    
	 	 
	    // Dot Product between the plane normal and the center of the Axis Aligned Box
	    // using absolute values
	    float fRadius = Math.abs(plane.normal.x*E.x) + Math.abs(plane.normal.y*E.y) + Math.abs(plane.normal.z*E.z);
	 
	    return sphereFrontOfPlane(plane.normal,plane.offset,center,fRadius );
	}
	
	public static boolean chunkFrontOfPlane(Plane plane, Vector3f chunkPos)
	{
	    // Get the Extense vector
	    Vector3f E = new Vector3f(Terrain.CHUNK_SIZE_HALF,Terrain.CHUNK_HEIGHT_HALF,Terrain.CHUNK_SIZE_HALF);
	    
	    // Get the center of the Box
	    Vector3f center = Vector3f.add(chunkPos, E, null);
	    
	 	 
	    // Dot Product between the plane normal and the center of the Axis Aligned Box
	    // using absolute values
	    float fRadius = Math.abs(plane.normal.x*E.x) + Math.abs(plane.normal.y*E.y) + Math.abs(plane.normal.z*E.z);
		 
	    return sphereFrontOfPlane(plane.normal,plane.offset,center,fRadius );
	}
	
	*/
	/*public static int boxToPlaneCollision(Plane plane, Vector3f min, Vector3f max) { 
		   Vector3f vmin = new Vector3f(); 
		   Vector3f vmax = new Vector3f(); 
		   
	      // X axis 
	      if(plane.normal.x > 0) { 
	         vmin.x = min.x; 
	         vmax.x = max.x; 
	      } else { 
	         vmin.x = max.x; 
	         vmax.x = min.x; 
	      } 
	      // Y axis 
	      if(plane.normal.y > 0) { 
	         vmin.y = min.y; 
	         vmax.y = max.y; 
	      } else { 
	         vmin.y = max.y; 
	         vmax.y = min.y; 
	      } 
	      // Z axis 
	      if(plane.normal.z > 0) { 
	         vmin.z = min.z; 
	         vmax.z = max.z; 
	      } else { 
	         vmin.z = max.z; 
	         vmax.z = min.z; 
	      } 
	      if(Vector3f.dot(plane.normal, vmin) + plane.offset > 0) 
	         return 2; 
	      if(Vector3f.dot(plane.normal, vmax) + plane.offset >= 0) 
	         return 3; 
		   
		  return 1;
	}
	
	public static boolean boxFrontOfPlane(Plane plane, Vector3f min, Vector3f max) { 
		   Vector3f vmin = new Vector3f(); 

	      // X axis 
	      if(plane.normal.x > 0) { 
	         vmin.x = min.x; 
	      } else { 
	         vmin.x = max.x; 
	      } 
	      // Y axis 
	      if(plane.normal.y > 0) { 
	         vmin.y = min.y; 
	      } else { 
	         vmin.y = max.y; 
	      } 
	      // Z axis 
	      if(plane.normal.z > 0) { 
	         vmin.z = min.z; 
	      } else { 
	         vmin.z = max.z; 
	      } 
	      if(Vector3f.dot(plane.normal, vmin) + plane.offset >= 0) 
	         return true; 
		   
		  return false;
	}*/

	public static int boxToPlaneCollision(Maths.Plane plane, Vector3f[] minMax)
	{
		int result = 2; //Inside
		
	                // planes have unit-length normal, offset = -dot(normal, point on plane)
			int nx = plane.normal.x > 0?1:0;
			int ny = plane.normal.y > 0?1:0;
			int nz = plane.normal.z > 0?1:0;
			
			// getMinMax(): 0 = return min coordinate. 1 = return max.
			float dot = (plane.normal.x*minMax[nx].x) + (plane.normal.y*minMax[nx].y) + (plane.normal.z*minMax[nx].z);
			
			if ( dot < -plane.offset )
				return 0; //Outside
			
			float dot2 = (plane.normal.x*minMax[1-nx].x) + (plane.normal.y*minMax[1-nx].y) + (plane.normal.z*minMax[1-nx].z);
			
			if ( dot2 <= -plane.offset )
				result = 1; //Intersect
		
		return result;
	}
}
