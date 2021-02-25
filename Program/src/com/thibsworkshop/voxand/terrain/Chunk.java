package com.thibsworkshop.voxand.terrain;

import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.models.RawModel;
import com.thibsworkshop.voxand.models.WireframeModel;
import com.thibsworkshop.voxand.toolbox.AABB;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;


public class Chunk{
	
	public static final int CHUNK_SIZE = 32;
	public static final float F_CHUNK_SIZE = 32f;
	public static final double D_CHUNK_SIZE = 32d;
	public static final int CHUNK_HEIGHT = 256;

	public static final Vector3f CHUNK_SCALE = new Vector3f(1);

	private Vector2i chunkPos;
	private Vector3f position;
	private Vector3f positionMax;

	private int sqr_distance;
	private long lastTickUpdate;

	public static AABB aabb = AABB.createMinMax(new Vector3f(0), new Vector3f(CHUNK_SIZE,CHUNK_HEIGHT,CHUNK_SIZE));
	public static WireframeModel wireModel;

	public boolean generated = false;
	
	private RawModel model;

	public byte[][][] grid;
	
	//Maybe later add an byte that leads to like the biome with premade infos.
	public Chunk(Vector2i chunkPos) {
		this.chunkPos = chunkPos;

		position = new Vector3f(chunkPos.x * CHUNK_SIZE,0, chunkPos.y * CHUNK_SIZE);
		positionMax = new Vector3f(position.x + CHUNK_SIZE, CHUNK_HEIGHT, position.z + Chunk.CHUNK_SIZE);

		this.grid = new byte[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
	}

	public void generateTerrain(float[] vertices, int[] indices, byte[] blocks, byte[] normals){
		model = Loader.loadToVAOColor(vertices,indices,blocks, normals);
		if(model == null) {
			System.err.println("ERROR COULDN'T CREATE TERRAIN MODEL: " + "\nvertices: " + vertices.length + "\n indices: " + indices.length + "\n normals: "+normals);
			return;
		}
		generated = true;
	}

	public void update(int sqr_distance){ //Each update we store the tick, so we can know if distance is up to date
		this.sqr_distance = sqr_distance;
		lastTickUpdate = Time.getTick();
	}

	public static void genWireframe(){
		if(wireModel == null)
			wireModel = new WireframeModel(aabb,Color.white);
	}

	public static void destroyWireframe(){
		wireModel = null;
	}

	public int getSqr_distance(){ return sqr_distance; }

	public long getLastTickUpdate(){ return lastTickUpdate; }

	public Vector2i getChunkPos() {
		return chunkPos;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getPositionMax(){ return positionMax; }

	public RawModel getModel() {
		return model;
	}

	public static Vector2i positionToChunkPos(float x, float z){
		return new Vector2i((int)Math.floor(x/F_CHUNK_SIZE),(int)Math.floor(z/F_CHUNK_SIZE));
	}

	public static Vector2i positionToChunkPos(Vector3f position){
		return positionToChunkPos(position.x,position.z);
	}

	public static void positionToChunkPos(Vector3f position, Vector2i chunkPos){
		chunkPos.x = (int)Math.floor(position.x/F_CHUNK_SIZE);
		chunkPos.y = (int)Math.floor(position.z/F_CHUNK_SIZE);
	}

	/**
	 * Returns chunkPos + floorDiv(pos,CHUNK_SIZE)
	 * @param chunkPos the chunk position to correct
	 * @param pos the relative position from this chunk position
	 * @return the corrected chunk position +- 1
	 */
	public static int chunkPosCorrect(int chunkPos, int pos){
		return chunkPos + Math.floorDiv(pos, CHUNK_SIZE);
	}

	/**
	 * Returns floorMod(pos, CHUNK_SIZE)
	 * @param pos the position to correct
	 * @return the corrected position
	 */
	public static int posCorrect(int pos){
		return Math.floorMod(pos, Chunk.CHUNK_SIZE);
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
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Chunk)) return false;
		Chunk te = (Chunk) o;
		return chunkPos == te.chunkPos;
	}

	@Override
	public int hashCode() {
		int result = chunkPos.x;
		result = 31 * result + chunkPos.y;
		return result;
	}
}
