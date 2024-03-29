package com.thibsworkshop.voxand.terrain;

import com.thibsworkshop.voxand.data.Block;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.physics.collisions.AABB;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Direction;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Vector;


public class Chunk{
	
	public static final int CHUNK_SIZE = 32;
	public static final float F_CHUNK_SIZE = 32f;
	public static final double D_CHUNK_SIZE = 32d;
	public static final int CHUNK_HEIGHT = 256;

	public static final Vector3f CHUNK_SCALE = new Vector3f(1);

	private final Vector2i chunkPos;
	private final Vector3f position;
	private final Vector3f positionMax;

	private int sqr_distance;
	private long lastTickUpdate;

	public static AABB aabb = AABB.createMinMax(new Vector3f(0), new Vector3f(CHUNK_SIZE,CHUNK_HEIGHT,CHUNK_SIZE));
	public static WireframeModel wireModel;

	public static WireframeModel blockWireframeModel;

	public boolean generated = false;

	/** Chunk model has to be refreshed */
	public boolean dirty = false;
	/** Neighbouring chunks to refresh */
	public ArrayList<Vector2i> sidesToRefresh = new ArrayList<Vector2i>();

	private RawModel model;

	private byte[][][] grid;
	
	//Maybe later add an byte that leads to like the biome with premade infos.
	public Chunk(Vector2i chunkPos, Vector2i playerChunkPos) {
		this.chunkPos = chunkPos;

		position = new Vector3f((chunkPos.x - playerChunkPos.x) * CHUNK_SIZE,0, (chunkPos.y - playerChunkPos.y) * CHUNK_SIZE);
		positionMax = new Vector3f(position.x + CHUNK_SIZE, CHUNK_HEIGHT, position.z + Chunk.CHUNK_SIZE);

		this.grid = new byte[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
	}

	public static void init(){
		blockWireframeModel = new WireframeModel(AABB.moreThanOne(),Color.light_grey);
	}

	public void generateTerrain(float[] vertices, int[] indices, byte[] blocks, byte[] normals){
		model = Loader.loadToVAOColor(vertices,indices,blocks, normals);

		generated = true;
	}

	public void update(int sqr_distance, Vector2i cameraChunkPos){ //Each update we store the tick, so we can know if distance is up to date
		this.sqr_distance = sqr_distance;
		this.position.set((chunkPos.x - cameraChunkPos.x) * CHUNK_SIZE,0, (chunkPos.y - cameraChunkPos.y) * CHUNK_SIZE);
		this.positionMax.set(position.x + CHUNK_SIZE, CHUNK_HEIGHT, position.z + Chunk.CHUNK_SIZE);
		lastTickUpdate = Time.getTick();
	}
	//TODO: move to Debug class
	public static void genWireframe(){
		if(wireModel == null)
			wireModel = new WireframeModel(aabb,Color.white);
	}

	public static void destroyWireframe(){
		wireModel = null;
	}

	//<editor-fold desc="Getters">

	public byte[][][] getGrid(){ return grid; }
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
	//</editor-fold>

	//<editor-fold desc="Setters">

	/**
	 * Modify the grid and checks if neighbouring chunks should be refreshed
	 * @param x
	 * @param y
	 * @param z
	 * @param value
	 */
	public void setGrid(int x, int y, int z, byte value){
		grid[x][y][z] = value;
		dirty = true;
		if(Block.blocks[value].getTransparency() == 1)
			return; //If we placed a solid block, everything is fine

		if(x == 0){
			Vector2i left = new Vector2i(chunkPos);
			left.x -= 1;
			sidesToRefresh.add(left);
		}
		if(x == CHUNK_SIZE - 1){
			Vector2i right = new Vector2i(chunkPos);
			right.x += 1;
			sidesToRefresh.add(right);
		}
		if(z == 0){
			Vector2i back = new Vector2i(chunkPos);
			back.y -= 1;
			sidesToRefresh.add(back);
		}
		if(z == CHUNK_SIZE-1){
			Vector2i front = new Vector2i(chunkPos);
			front.y += 1;
			sidesToRefresh.add(front);
		}

	}
	//</editor-fold>

	//<editor-fold desc="Static methods">

	/**
	 * Calculates the chunkPosition given x and z coordinates
	 * @param x x coordinate
	 * @param z z coordinate
	 * @return a new Vector2i
	 */
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
	public static int correctChunkPosition(int chunkPos, int pos){
		return chunkPos + Math.floorDiv(pos, CHUNK_SIZE);
	}

	/**
	 * Corrects the chunk position given a position. chunkPos += floor(pos,CHUNK_SIZE)
	 * @param chunkPos the chunk position to correct
	 * @param pos the position
	 */
	public static void correctChunkPosition(Vector2i chunkPos, Vector3f pos){
		chunkPos.x += (int)Math.floor(pos.x/F_CHUNK_SIZE);
		chunkPos.y += (int)Math.floor(pos.z/F_CHUNK_SIZE);
	}

	/**
	 * Corrects the chunk position given a position. chunkPos += floor(pos,CHUNK_SIZE)
	 * @param chunkPos the chunk position to correct
	 * @param pos the position
	 */
	public static void correctChunkPosition(Vector2i chunkPos, Vector3i pos){
		chunkPos.x += (int)Math.floor(pos.x/F_CHUNK_SIZE);
		chunkPos.y += (int)Math.floor(pos.z/F_CHUNK_SIZE);
	}

	/**
	 * Returns floorMod(pos, CHUNK_SIZE)
	 * @param pos the position to correct
	 * @return the corrected position
	 */
	public static int correctPosition(int pos){
		return Math.floorMod(pos, CHUNK_SIZE);
	}

	/**
	 * Returns floatMod(pos, CHUNK_SIZE)
	 * @param pos the position to correct
	 * @return the corrected position
	 */
	public static float correctPosition(float pos){
		return Maths.floatMod(pos, F_CHUNK_SIZE);
	}

	/**
	 * Corrects the position in argument with a floatMod
	 * @param pos the position to correct
	 */
	public static void correctPosition(Vector3f pos){
		pos.x = Maths.floatMod(pos.x, F_CHUNK_SIZE);
		pos.z = Maths.floatMod(pos.z, F_CHUNK_SIZE);
	}

	/**
	 * Corrects the position in argument with a floatMod
	 * @param pos the position to correct
	 */
	public static void correctPosition(Vector3i pos){
		pos.x = Math.floorMod(pos.x, CHUNK_SIZE);
		pos.z = Math.floorMod(pos.z, CHUNK_SIZE);
	}

	/**
	 * Shifts the chunk position in argument from the player to place it correctly
	 * @param pos the chunk position to shift
	 * @return the new position
	 */
	public static float shiftChunkPosFromCamera(int pos, int camPos){
		return (pos - camPos) * Chunk.CHUNK_SIZE;
	}

	/**
	 * Shifts the given position from the player position
	 * @param position The position to shift
	 * @param playerPos The players' position
	 */
	public static void shiftPositionFromCamera(Vector3f position, Vector2i chunkPos, Vector2i playerPos){
		position.add((chunkPos.x - playerPos.x) * CHUNK_SIZE, 0,(chunkPos.y - playerPos.y) * CHUNK_SIZE);
	}

	//</editor-fold>

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
		if (!(o instanceof Chunk te)) return false;
		return chunkPos.equals(te.chunkPos);
	}

	@Override
	public int hashCode() {
		int result = chunkPos.x;
		result = 31 * result + chunkPos.y;
		return result;
	}
}
