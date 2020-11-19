package com.thibsworkshop.voxand.terrain;

import com.thibsworkshop.voxand.io.Time;

public class TerrainGenerator {
	
	public static IndiceVerticeNormal generate(Terrain terrain) {

		int verticesCountEstimate = 0;
		
		boolean[][] backOv = new boolean[Terrain.CHUNK_SIZE][Terrain.CHUNK_HEIGHT];
		boolean[][] frontOv = new boolean[Terrain.CHUNK_SIZE][Terrain.CHUNK_HEIGHT];
		boolean[][] rightOv = new boolean[Terrain.CHUNK_SIZE][Terrain.CHUNK_HEIGHT];
		boolean[][] leftOv = new boolean[Terrain.CHUNK_SIZE][Terrain.CHUNK_HEIGHT];

		int chunkx = terrain.getChunkPos().x;
		int chunkz = terrain.getChunkPos().y;

		for(int x = 0; x < Terrain.CHUNK_SIZE;x++) {
			for(int y = 0; y < Terrain.CHUNK_HEIGHT;y++) {
				backOv[x][y] = TerrainManager.isTerrainTransparent(x, y, Terrain.CHUNK_SIZE-1, chunkx, chunkz-1);
			}
		}
		
		for(int x = 0; x < Terrain.CHUNK_SIZE;x++) {
			for(int y = 0; y < Terrain.CHUNK_HEIGHT;y++) {
				frontOv[x][y] = TerrainManager.isTerrainTransparent(x, y, 0, chunkx, chunkz+1);
			}
		}
		
		for(int z = 0; z < Terrain.CHUNK_SIZE;z++) {
			for(int y = 0; y < Terrain.CHUNK_HEIGHT;y++) {
				leftOv[z][y] = TerrainManager.isTerrainTransparent(Terrain.CHUNK_SIZE-1, y, z, chunkx-1, chunkz);
			}
		}
		
		for(int z = 0; z < Terrain.CHUNK_SIZE;z++) {
			for(int y = 0; y < Terrain.CHUNK_HEIGHT;y++) {
				rightOv[z][y] = TerrainManager.isTerrainTransparent(0, y, z, chunkx+1, chunkz);
			}
		}
		
		for(int x = 0; x < Terrain.CHUNK_SIZE; x++) { //Here we estimate the number of vertices.
			for (int z = 0; z < Terrain.CHUNK_SIZE; z++) {
				for (int y = 0; y < Terrain.CHUNK_HEIGHT; y++) {
					if(terrain.grid[x][y][z] > 0) { //If the cube is solid, We'll check for every face the adjacent cube if it solid or not
						
						if(y > 0 && terrain.grid[x][y - 1][z] == 0) { // Checking bottom face
							verticesCountEstimate+=12;
						}
						if(y < Terrain.CHUNK_HEIGHT - 1 && terrain.grid[x][y + 1][z] == 0) { // Checking top face
							verticesCountEstimate+=12;
						}
						
						
						if((z == 0 && backOv[x][y]) || (z > 0 && terrain.grid[x][y][z - 1] == 0)) { // Checking back face
							verticesCountEstimate+=12;
						}
						if((z == Terrain.CHUNK_SIZE - 1 && frontOv[x][y]) || (z < Terrain.CHUNK_SIZE - 1 && terrain.grid[x][y][z + 1] == 0)) { // Checking front face
							verticesCountEstimate+=12;
						}
						
						
						if((x  == 0 && leftOv[z][y]) || (x > 0 && terrain.grid[x - 1][y][z] == 0)) { // Checking left face
							verticesCountEstimate+=12;
						}
						if((x == Terrain.CHUNK_SIZE - 1 && rightOv[z][y]) || (x < Terrain.CHUNK_SIZE - 1 && terrain.grid[x + 1][y][z] == 0)) { // Checking right face
							verticesCountEstimate+=12;
						}
					}
				}
			}
		}
		
		float[] vertices = new float[verticesCountEstimate];
		int verticeI = 0;//counter for the vertices
		int verticesCount = 0;
		
		byte[] normals = new byte[verticesCountEstimate/3];
		int normalI = 0;//counter for the normals
		
		int[] indices = new int[(verticesCountEstimate/2) * 3];
		int indiceI = 0;//counter for the indices
		
		byte[] blocks = new byte[verticesCountEstimate/3];
		int blockI = 0;//counter for the colors
		
		for(int x = 0; x < Terrain.CHUNK_SIZE; x++) {
			for (int z = 0; z < Terrain.CHUNK_SIZE; z++) {
				for (int y = 0; y < Terrain.CHUNK_HEIGHT; y++) {
					if(terrain.grid[x][y][z] > 0) { //If the cube is solid, We'll check for every face the adjacent cube if it solid or not
						int faces = 0;
						
						if(y > 0 && terrain.grid[x][y - 1][z] == 0) { // Checking bottom face
							vertices[verticeI++] = x;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z + 1;
							
							vertices[verticeI++] = x;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z + 1;
							
							for(int i = 0; i < 4;i++)
								normals[normalI++] = 4;

							faces++;
						}
						
						if(y < Terrain.CHUNK_HEIGHT && terrain.grid[x][y + 1][z] == 0) { // Checking top face
							vertices[verticeI++] = x;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z + 1;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z + 1;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z;
							
							for(int i = 0; i < 4;i++)
								normals[normalI++] = 1;

							faces++;
						}
						
						if((z == 0 && backOv[x][y]) || (z > 0 && terrain.grid[x][y][z - 1] == 0)) { // Checking back face
							vertices[verticeI++] = x;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z;
							
							for(int i = 0; i < 4;i++)
								normals[normalI++] = 5;

							faces++;
						}
						
						if((z == Terrain.CHUNK_SIZE - 1 && frontOv[x][y]) || (z < Terrain.CHUNK_SIZE - 1 && terrain.grid[x][y][z + 1] == 0)) { // Checking front face
							vertices[verticeI++] = x;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z + 1;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z + 1;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z + 1;
							
							vertices[verticeI++] = x;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z + 1;
							
							for(int i = 0; i < 4;i++)
								normals[normalI++] = 2;
							
							faces++;
						}
						
						if((x  == 0 && leftOv[z][y]) || (x > 0 && terrain.grid[x - 1][y][z] == 0)) { // Checking left face
							vertices[verticeI++] = x;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z;

							vertices[verticeI++] = x;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z + 1;
							
							vertices[verticeI++] = x;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z + 1;

							vertices[verticeI++] = x;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z;
							
							for(int i = 0; i < 4;i++)
								normals[normalI++] = 3;

							faces++;
						}
						
						if((x == Terrain.CHUNK_SIZE - 1 && rightOv[z][y]) || (x < Terrain.CHUNK_SIZE - 1 && terrain.grid[x + 1][y][z] == 0)) { // Checking right face
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z;
							
							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y + 1;
							vertices[verticeI++] = z + 1;

							vertices[verticeI++] = x + 1;
							vertices[verticeI++] = y;
							vertices[verticeI++] = z + 1;
							
							for(int i = 0; i < 4;i++)
								normals[normalI++] = 0;
							
							faces++;
						}
						
						
						for(int i = 0; i < faces; i++) {
							
							indices[indiceI++]= verticesCount;
							indices[indiceI++]= verticesCount + 1;
							indices[indiceI++]= verticesCount + 2;
							indices[indiceI++]= verticesCount + 2;
							indices[indiceI++]= verticesCount + 3;
							indices[indiceI++]= verticesCount;
							
							for(int j = 0; j < 4;j++)
								blocks[blockI++]= terrain.grid[x][y][z];
							
							verticesCount += 4;
						}
						
					}
				}
			}
		}

		long verticeTimeE = Time.getMilliTime();

		/*System.out.println(String.format("estimation: %d + Init time: %d grid time: %d vertice time: %d total time: %d", 
				verticesCountEstimate,
				initializationTimeE-initializationTime,
				gridTimeE-offTimeE,
				verticeTimeE-gridTimeE,
				verticeTimeE-initializationTime));*/
		return new IndiceVerticeNormal(vertices, indices, normals,blocks);
	}
	
    public static class IndiceVerticeNormal{
    	public float[] vertices;
    	public int[] indices;
    	public byte[] normals;
    	public byte[] blocks;
    	
		public IndiceVerticeNormal(float[] vertices, int[] indices, byte[] normals,byte[] blocks) {
			this.vertices = vertices;
			this.indices = indices;
			this.normals = normals;
			this.blocks = blocks;
		}
    }
}
