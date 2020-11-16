package com.thibsworkshop.voxand.rendering;

import java.util.List;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.models.RawModel;
import com.thibsworkshop.voxand.shaders.TerrainShader;
import com.thibsworkshop.voxand.terrain.Block;
import com.thibsworkshop.voxand.terrain.Terrain;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.textures.Material;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.Matrix4f;


public class TerrainRenderer extends Renderer {

	private TerrainShader shader;
	
	private Material material;
	
	private MasterRenderer masterRenderer;

	private TerrainManager terrainManager;

	public TerrainRenderer(TerrainShader shader, MasterRenderer masterRenderer) {
		super(shader);
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(Camera.mainCamera.getProjectionMatrix());
		shader.loadBlocks(Block.blocks);
		shader.stop();
		material = new Material(100,0.01f);
		this.masterRenderer = masterRenderer;
	}

	public void linkManager(TerrainManager terrainManager){
		this.terrainManager = terrainManager;
	}

	@Override
	public void render(Camera camera) {
		int i = 0;
		for(Terrain terrain:terrainManager.getTerrainsToRender()) {
			if(terrain != null) {
				//if(masterRenderer.chunkInsideFrustum(terrain.getPosition())) {
					i++;
					prepareTerrain(terrain);
					loadModelMatrix(terrain);
					GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
					unbindModel();
				//}
			}
		}
		
		//System.out.println("rendering: "+i);
	}
	
	private void prepareTerrain(Terrain terrain) {
		
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());

	}
	
	private void loadModelMatrix(Terrain terrain) {
		
		shader.loadTransformationMatrix(terrain.getTransformationMatrix());
	}
	

}
