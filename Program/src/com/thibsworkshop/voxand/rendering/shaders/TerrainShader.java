package com.thibsworkshop.voxand.rendering.shaders;

import com.thibsworkshop.voxand.data.Block;
import org.joml.Vector3f;
import org.joml.Vector4f;


public class TerrainShader extends BasicShader{
	
	private static final String VERTEX_FILE = "terrainVertexShader.vert";
	private static final String FRAGMENT_FILE = "terrainFragmentShader.frag";
	
	private int[] location_block_color;
	private int[] location_block_reflectivity;
	private int[] location_block_shineDamper;

	private int location_chunkPosition;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "block_id");
		super.bindAttribute(2, "normal");
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_block_color = new int[Block.MAX_BLOCK];
		location_block_shineDamper = new int[Block.MAX_BLOCK]; 
		location_block_reflectivity = new int[Block.MAX_BLOCK];

		//fragment
		for(int i = 0; i < Block.MAX_BLOCK; i++) {
			location_block_color[i] = super.getUniformLocation("blocks["+i+"].color");
			location_block_reflectivity[i] = super.getUniformLocation("blocks["+i+"].reflectivity");
			location_block_shineDamper[i] = super.getUniformLocation("blocks["+i+"].shineDamper");
		}

		location_chunkPosition = super.getUniformLocation("chunkPosition");
	}
	
	public void loadBlocks(Block[] blocks) {
		for(int i = 0; i < Block.MAX_BLOCK; i++) {
			if(blocks[i] != null) {
				Block b = blocks[i];
				super.loadVector(location_block_color[i], new Vector4f(b.getColor().x,b.getColor().y,b.getColor().z,0f));
				super.loadFloat(location_block_reflectivity[i], b.getReflectivity());
				super.loadFloat(location_block_shineDamper[i], b.getShineDamper());
			}
		}
	}

	public void loadPosition(Vector3f position){
		super.loadVector(location_chunkPosition,position);
	}
}
