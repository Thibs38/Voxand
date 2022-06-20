package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Player;
import com.thibsworkshop.voxand.entities.ProjectionCamera;
import com.thibsworkshop.voxand.game.Config;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.shaders.TerrainShader;
import com.thibsworkshop.voxand.data.Block;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;


public class TerrainRenderer extends Renderer {



	private final TerrainShader terrainShader;
	public static TerrainRenderer terrainRenderer;

	private final ProjectionCamera projectionCamera;

	private final MasterRenderer masterR;

	public TerrainRenderer(TerrainShader terrainShader, ProjectionCamera projectionCamera, MasterRenderer masterRenderer) {
		super(terrainShader);
		this.terrainShader = terrainShader;
		this.projectionCamera = projectionCamera;
		this.masterR = masterRenderer;
		terrainShader.start();
		terrainShader.loadProjectionMatrix(projectionCamera.getProjectionMatrix());
		terrainShader.loadBlocks(Block.blocks);
		terrainShader.stop();
	}


	@Override
	public void render() {
		terrainShader.start();
		terrainShader.loadFogVariables(0.05f, masterR.getFogDistance(), MasterRenderer.SKY_COLOR);//0.0035f
		terrainShader.loadLights(masterR.getLights());
		terrainShader.loadViewMatrix(Camera.main.getViewMatrix());
		terrainShader.loadAmbientLight(masterR.getSun());

		for(Chunk chunk :TerrainManager.chunks.values()) {
			if(chunk != null && chunk.generated &&
				chunk.getSqr_distance() <= Config.sqr_chunkViewDist &&
				projectionCamera.frustumIntersection.testAab(chunk.getPosition(), chunk.getPositionMax())) {

				prepareTerrain(chunk);
				loadModelPosition(chunk);
				GL11.glDrawElements(GL11.GL_TRIANGLES, chunk.getModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
				unbindModel();
			}
		}

		terrainShader.stop();
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
		
		terrainShader.loadPosition(chunk.getPosition());
	}

	//TODO
	// When resizing window, or changing fov must update projection matrix here
	public void updateProjectionMatrix(Matrix4f projection) {
		terrainShader.start();
		terrainShader.loadProjectionMatrix(projection);
		terrainShader.stop();
	}

}
