package com.thibsworkshop.voxand.terrain;

import com.thibsworkshop.voxand.data.Biome;
import com.thibsworkshop.voxand.entities.Player;
import com.thibsworkshop.voxand.terrain.Chunk.TerrainInfo;
import com.thibsworkshop.voxand.toolbox.SimplexNoise;
import org.joml.Vector2i;


public class GridGenerator {
	
	public static final float seed = (float) (Math.random()*1000);

	public static final int HEIGHT_OFFSET = 128;

	public static Chunk generate(Vector2i chunkPos, Vector2i playerChunkPos, TerrainInfo info) {
		Chunk chunk = new Chunk(chunkPos,playerChunkPos);
		byte[][][] grid = chunk.getGrid();
		long realx = (long) chunkPos.x * Chunk.CHUNK_SIZE;
		long realz = (long) chunkPos.y * Chunk.CHUNK_SIZE;

		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {

				double xd = realx + x;
				double zd = realz + z;
				float freq = 1f;
				float amp = info.heightScale;
				float s = 128f;
				double xoff = simplex(zd,xd,0.1f);
				double zoff = simplex(xd,zd,0.1f);
				for(int i = 0; i < 4; i++){
					s += simplex(xd + xoff, zd + zoff, info.scale * freq) * 1f * amp;
					freq *=0.8f;
					amp *= 0.9f;
				}
				float simplex = s; //Biome.biomes[0].generate_xz(x,z);
				
				for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
					/*float simplex3d = (float)SimplexNoise.noise(
							((double)x/2d+realx)*0.01d,
							((double)y/2d)*0.01d,
							((double)z/2d+realz)*0.01d);
					if(simplex3d <-1+((float)y/Terrain.CUBE_COUNT_HEIGHT)/2f)
						terrain.grid[x][y][z] = 0;
					else {*/
						if(simplex < 120) {
							if(y <= simplex-5)
								grid[x][y][z] = 3;
							else if(y <= simplex-1)
								grid[x][y][z] = 2;
							else if(y <= simplex)
								grid[x][y][z] = 1;
							else 
								grid[x][y][z] = 0;
						}else{
							if(y <= simplex-2)
								grid[x][y][z] = 3;
							else if(y <= simplex-1)
								grid[x][y][z] = 2;
							else if(y <= simplex)
								grid[x][y][z] = 1;
							else 
								grid[x][y][z] = 0;
						}
						
					//}
					
				}
			}
		}
				
		return chunk;
	}
	
	public static double simplex(double x, double z, double scale) {
		return SimplexNoise.noise(x * scale + seed,z * scale + seed);
	}

	public static double simplex(double x, double y, double z, double scale) {
		return SimplexNoise.noise(x * scale + seed,y * scale + seed, z * scale + seed);
	}

}
