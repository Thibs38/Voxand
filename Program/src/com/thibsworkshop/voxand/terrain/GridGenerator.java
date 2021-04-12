package com.thibsworkshop.voxand.terrain;

import com.thibsworkshop.voxand.entities.Player;
import com.thibsworkshop.voxand.terrain.Chunk.TerrainInfo;
import com.thibsworkshop.voxand.toolbox.SimplexNoise;
import org.joml.Vector2i;


public class GridGenerator {
	
	public static final float seed = (float) (Math.random()*1000);

	public static final int HEIGHT_OFFSET = 128;

	public static Chunk generate(Vector2i chunkPos, Vector2i playerChunkPos, TerrainInfo info) {
		Chunk chunk = new Chunk(chunkPos,playerChunkPos);
		
		int realx = chunkPos.x * Chunk.CHUNK_SIZE;
		int realz = chunkPos.y * Chunk.CHUNK_SIZE;

		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				
				double simplex = HEIGHT_OFFSET;
				float amplitude = 1;
				float frequency = 1;
				float fx = x + realx;
				float fz = z + realz;
				
				double simplex1 = (simplex(fx,fz,info.scale*frequency)+0.7f)/1.4f;
				double offx = SimplexNoise.noise(fx*0.0125f + seed, fz*0.0125f+ seed)*50*simplex1;
				double offz = SimplexNoise.noise(fz*0.0125f+ seed, fx*0.0125f+ seed)*50*simplex1;
				
				simplex += simplex(fx, fz,info.scale*frequency) * info.heightScale * amplitude;
				
				for(float i = 2; i < info.octaves;i++) {
					frequency *= info.lacunarity;
					amplitude *= info.persistance;
					simplex += simplex(fx,fz,info.scale*frequency)*info.heightScale*amplitude;
				}
				
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
								chunk.grid[x][y][z] = 3;
							else if(y <= simplex-1)
								chunk.grid[x][y][z] = 2;
							else if(y <= simplex)
								chunk.grid[x][y][z] = 1;
							else 
								chunk.grid[x][y][z] = 0;
						}else{
							if(y <= simplex-2)
								chunk.grid[x][y][z] = 3;
							else if(y <= simplex-1)
								chunk.grid[x][y][z] = 2;
							else if(y <= simplex)
								chunk.grid[x][y][z] = 1;
							else 
								chunk.grid[x][y][z] = 0;
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
