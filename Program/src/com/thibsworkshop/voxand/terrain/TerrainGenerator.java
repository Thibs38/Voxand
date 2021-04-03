package com.thibsworkshop.voxand.terrain;

public class TerrainGenerator {

	public static IndicesVerticesNormals generate(Chunk chunk, byte[][][] back, byte[][][] front, byte[][][] right, byte[][][] left ) {
		int verticesCountEstimate = 0;

		//Estimating the vertices count
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) { //Here we estimate the number of vertices.
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
					if(chunk.grid[x][y][z] > 0) { //If the cube is solid, We'll check for every face the adjacent cube if is solid or not
						
						if(y > 0 && chunk.grid[x][y - 1][z] == 0) { // Checking bottom face
							verticesCountEstimate+=12;
						}
						if(y < Chunk.CHUNK_HEIGHT - 1 && chunk.grid[x][y + 1][z] == 0) { // Checking top face
							verticesCountEstimate+=12;
						}

						
						if((z == 0 && Block.blocks[back[x][y][Chunk.CHUNK_SIZE - 1]].getTransparency() < 1)
								|| (z > 0 && chunk.grid[x][y][z - 1] == 0)) { // Checking back face
							verticesCountEstimate+=12;
						}
						if((z == Chunk.CHUNK_SIZE - 1 && Block.blocks[front[x][y][0]].getTransparency() < 1)
								|| (z < Chunk.CHUNK_SIZE - 1 && chunk.grid[x][y][z + 1] == 0)) { // Checking front face
							verticesCountEstimate+=12;
						}
						
						
						if((x  == 0 && Block.blocks[left[Chunk.CHUNK_SIZE - 1][y][z]].getTransparency() < 1)
								|| (x > 0 && chunk.grid[x - 1][y][z] == 0)) { // Checking left face
							verticesCountEstimate+=12;
						}
						if((x == Chunk.CHUNK_SIZE - 1 && Block.blocks[right[0][y][z]].getTransparency() < 1)
								|| (x < Chunk.CHUNK_SIZE - 1 && chunk.grid[x + 1][y][z] == 0)) { // Checking right face
							verticesCountEstimate+=12;
						}
					}
				}
			}
		}
		
		final float[] vertices = new float[verticesCountEstimate];
		int verticeI = 0;//counter for the vertices
		int verticesCount = 0;

		final byte[] normals = new byte[verticesCountEstimate/3];
		int normalI = 0;//counter for the normals

		final int[] indices = new int[(verticesCountEstimate/2) * 3];
		int indiceI = 0;//counter for the indices

		final byte[] blocks = new byte[verticesCountEstimate/3];
		int blockI = 0;//counter for the colors
		
		for(int x = 0; x < Chunk.CHUNK_SIZE; x++) {
			for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
				for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
					if(chunk.grid[x][y][z] > 0) { //If the cube is solid, We'll check for every face the adjacent cube if it solid or not
						int faces = 0;
						
						if(y > 0 && chunk.grid[x][y - 1][z] == 0) { // Checking bottom face
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
						
						if(y < Chunk.CHUNK_HEIGHT - 1 && chunk.grid[x][y + 1][z] == 0) { // Checking top face
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
						
						if((z == 0 && Block.blocks[back[x][y][Chunk.CHUNK_SIZE - 1]].getTransparency() < 1) ||
								(z > 0 && chunk.grid[x][y][z - 1] == 0)) { // Checking back face
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
						
						if((z == Chunk.CHUNK_SIZE - 1 && Block.blocks[front[x][y][0]].getTransparency() < 1) ||
								(z < Chunk.CHUNK_SIZE - 1 && chunk.grid[x][y][z + 1] == 0)) { // Checking front face
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
						
						if((x  == 0 && Block.blocks[left[Chunk.CHUNK_SIZE - 1][y][z]].getTransparency() < 1) ||
								(x > 0 && chunk.grid[x - 1][y][z] == 0)) { // Checking left face
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
						
						if((x == Chunk.CHUNK_SIZE - 1 && Block.blocks[right[0][y][z]].getTransparency() < 1) || (x < Chunk.CHUNK_SIZE - 1 && chunk.grid[x + 1][y][z] == 0)) { // Checking right face
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
								blocks[blockI++]= chunk.grid[x][y][z];
							
							verticesCount += 4;
						}
					}
				}
			}
		}
		//long verticeTimeE = Time.getFrameMilliTime();
		IndicesVerticesNormals ind = new IndicesVerticesNormals(vertices, indices, normals,blocks,chunk);
		/*System.out.println(String.format("estimation: %d + Init time: %d grid time: %d vertice time: %d total time: %d",
				verticesCountEstimate,
				initializationTimeE-initializationTime,
				gridTimeE-offTimeE,
				verticeTimeE-gridTimeE,
				verticeTimeE-initializationTime));*/

		return ind;
	}

    public static class IndicesVerticesNormals {
    	public float[] vertices;
    	public int[] indices;
    	public byte[] normals;
    	public byte[] blocks;
    	public Chunk chunk;
    	
		public IndicesVerticesNormals(float[] vertices, int[] indices, byte[] normals, byte[] blocks, Chunk chunk) {
			this.vertices = vertices;
			this.indices = indices;
			this.normals = normals;
			this.blocks = blocks;
			this.chunk = chunk;
		}
    }
}
