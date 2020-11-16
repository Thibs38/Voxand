package com.thibsworkshop.voxand.terrain;

import com.thibsworkshop.voxand.terrain.Terrain;
import com.thibsworkshop.voxand.terrain.Terrain.TerrainInfo;
import com.thibsworkshop.voxand.toolbox.SimplexNoise;


public class GridGenerator {
	
	public static final float seed = (float) (Math.random()*1000);

	public static final int HEIGHT_OFFSET = 128;

	public static Terrain generate(int chunkx, int chunkz, TerrainInfo info) {
		Terrain terrain = new Terrain(chunkx,chunkz);
		
		int realx = chunkx * Terrain.CHUNK_SIZE;
		int realz = chunkz * Terrain.CHUNK_SIZE;

		for(int x = 0; x < Terrain.CHUNK_SIZE; x++) {
			for (int z = 0; z < Terrain.CHUNK_SIZE; z++) {
				
				float simplex = HEIGHT_OFFSET;
				float amplitude = 1;
				float frequency = 1;
				float fx = x + realx;
				float fz = z + realz;
				
				float simplex1 = (CalculateSimplex(fx,fz,info.scale*frequency)+0.7f)/1.4f;
				float offx = (float) SimplexNoise.noise(fx*0.0125f + seed, fz*0.0125f+ seed)*50*simplex1;
				float offz = (float) SimplexNoise.noise(fz*0.0125f+ seed, fx*0.0125f+ seed)*50*simplex1;
				
				simplex += (CalculateSimplex(
						fx + offx,
						fz + offz,
						info.scale*frequency)
						)*info.heightScale*amplitude;
				
				for(float i = 2; i < info.octaves;i++) {
					frequency *= info.lacunarity;
					amplitude *= info.persistance;
					simplex += (CalculateSimplex(fx,fz,info.scale*frequency))*info.heightScale*amplitude;
				}
				
				for (int y = 0; y < Terrain.CHUNK_HEIGHT; y++) {
					/*float simplex3d = (float)SimplexNoise.noise(
							((double)x/2d+realx)*0.01d,
							((double)y/2d)*0.01d,
							((double)z/2d+realz)*0.01d);
					if(simplex3d <-1+((float)y/Terrain.CUBE_COUNT_HEIGHT)/2f)
						terrain.grid[x][y][z] = 0;
					else {*/
						if(simplex < 120) {
							if(y <= simplex-5)
								terrain.grid[x][y][z] = 3;
							else if(y <= simplex-1)
								terrain.grid[x][y][z] = 2;
							else if(y <= simplex)
								terrain.grid[x][y][z] = 1;
							else 
								terrain.grid[x][y][z] = 0;
						}else{
							if(y <= simplex-2)
								terrain.grid[x][y][z] = 3;
							else if(y <= simplex-1)
								terrain.grid[x][y][z] = 2;
							else if(y <= simplex)
								terrain.grid[x][y][z] = 1;
							else 
								terrain.grid[x][y][z] = 0;
						}
						
					//}
					
				}
			}
		}
				
		return terrain;
	}
	
	private static float CalculateSimplex(float x, float z,float scale) {
		return (float)SimplexNoise.noise(x * scale + seed,z * scale + seed);
	}
}
