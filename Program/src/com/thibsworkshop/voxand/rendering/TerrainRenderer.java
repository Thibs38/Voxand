package com.thibsworkshop.voxand.rendering;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.models.RawModel;
import com.thibsworkshop.voxand.shaders.TerrainShader;
import com.thibsworkshop.voxand.terrain.Block;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.textures.Material;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;


public class TerrainRenderer extends Renderer {

	private TerrainShader shader;
	
	private Material material;

	private TerrainManager terrainManager;

	private FrustumIntersection frustumIntersection;

	public TerrainRenderer(TerrainShader shader ) {
		super(shader);
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(Camera.main.getProjectionMatrix());
		shader.loadBlocks(Block.blocks);
		shader.stop();
		material = new Material(100,0.01f);

		frustumIntersection = Camera.main.frustumIntersection;
	}

	public void linkManager(TerrainManager terrainManager){
		this.terrainManager = terrainManager;
	}

	@Override
	public void render(Camera camera) {
		for(Chunk chunk :terrainManager.getTerrainsToRender().values()) {
			if(chunk != null) {
				if(frustumIntersection.testAab(chunk.getPosition(), chunk.getPositionMax())) {
					prepareTerrain(chunk);
					loadModelPosition(chunk);
					GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
					unbindModel();
				}
			}
		}
	}
	
	private void prepareTerrain(Chunk chunk) {
		
		RawModel rawModel = chunk.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());

	}
	
	private void loadModelPosition(Chunk chunk) {
		
		shader.loadPosition(chunk.getPosition());
	}

	public void updateProjectionMatrix(Matrix4f projection) {
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
	}

}
