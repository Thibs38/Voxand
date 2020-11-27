package com.thibsworkshop.voxand.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.models.RawModel;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;


public class Terrain {
	
	public static final int CHUNK_SIZE = 32;
	public static final int CHUNK_HEIGHT = 256;
	
	public static final int CHUNK_SIZE_HALF = 16;
	public static final int CHUNK_HEIGHT_HALF = 16;
	
	private Vector2i chunkPos;

	private Vector3f position;

	private Vector3f positionMax;


	public boolean generated = false;
	
	private RawModel model;
	
	private Matrix4f transformationMatrix;
	
	public byte[][][] grid;
	
	//Maybe later add an byte that leads to like the biome with premade infos.
	public Terrain(Vector2i chunkPos) {
		this.chunkPos = chunkPos;

		position = new Vector3f(chunkPos.x * CHUNK_SIZE,0, chunkPos.y * CHUNK_SIZE);
		positionMax = new Vector3f(position.x + CHUNK_SIZE, CHUNK_HEIGHT, position.z + Terrain.CHUNK_SIZE);

		this.transformationMatrix = Maths.createTransformationMatrix(position);

		this.grid = new byte[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
	}
	
	

	public void generateTerrain(float[] vertices, int[] indices, byte[] blocks, byte[] normals){
		model = Loader.loadToVAOColor(vertices,indices,blocks, normals);
		if(model == null) {
			System.err.println("ERROR COULDNT CREATE TERRAIN MODEL: " + "\nvertices: " + vertices.length + "\n indices: " + indices.length + "\n normals: "+normals);
			return;
		}
		generated = true;
	}
	

	public Vector2i getChunkPos() {
		return chunkPos;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getPositionMax(){ return positionMax; }
	
	public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}

	public RawModel getModel() {
		return model;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Terrain)) return false;
        Terrain te = (Terrain) o;
        return chunkPos == te.chunkPos;
    }

    @Override
    public int hashCode() {
        int result = chunkPos.x;
        result = 31 * result + chunkPos.y;
        return result;
    }


	

	public static class TerrainInfo{
		
		public float scale;
		public float heightScale;
		public float octaves;
		public float persistance; // 0 - 1 
		public float lacunarity; // > 1
		
		public TerrainInfo(float scale, float heightScale, float octaves, float persistance, float lacunarity) {
			this.scale = scale;
			this.heightScale = heightScale;
			this.octaves = octaves;
			this.persistance = persistance;
			this.lacunarity = lacunarity;
		}
		
		@Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof TerrainInfo)) return false;
	        TerrainInfo info = (TerrainInfo) o;
	        return scale == info.scale && heightScale == info.heightScale;
	    }

	    @Override
	    public int hashCode() {
	        int result = (int)(scale*10);
	        result = 31 * result + (int)(heightScale*10);
	        return result;
	    }
		
	}
}
