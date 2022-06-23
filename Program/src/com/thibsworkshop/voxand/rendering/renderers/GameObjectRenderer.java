package com.thibsworkshop.voxand.rendering.renderers;


import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.shaders.StaticShader;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.rendering.textures.Texture;
import com.thibsworkshop.voxand.terrain.Chunk;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.joml.Matrix4f;

import java.util.List;


public class GameObjectRenderer extends Renderer {

	private final StaticShader staticShader;

	GameObjectManager gameObjectManager;

	private final Vector2i cameraChunkPos;

	private final MasterRenderer masterR;
	private final ProjectionCamera projectionCamera;

	private final Vector3f worldMin = new Vector3f();
	private final Vector3f worldMax = new Vector3f();

	public GameObjectRenderer(StaticShader staticShader, ProjectionCamera projectionCamera, MasterRenderer masterRenderer){
		super(staticShader);
		this.masterR = masterRenderer;
		this.projectionCamera = projectionCamera;
		this.staticShader = staticShader;
		cameraChunkPos = Player.player.camera.transform.chunkPos;
		updateProjectionMatrix(Camera.main.getProjectionMatrix());

	}

	public void linkManager(GameObjectManager gameObjectManager){
		this.gameObjectManager = gameObjectManager;
	}

	@Override
	public void render() {

		staticShader.start();
		staticShader.loadFogVariables(0.05f, masterR.getFogDistance(), MasterRenderer.SKY_COLOR);//0.0035f
		staticShader.loadLights(masterR.getLights());
		staticShader.loadViewMatrix(Camera.main.getViewMatrix());
		staticShader.loadAmbientLight(masterR.getSun());

		for(TexturedModel texturedModel : gameObjectManager.getEntitiesToRender().keySet()) {
			prepareTexturedModel(texturedModel);
			List<GameEntity> batch = gameObjectManager.getEntitiesToRender().get(texturedModel);
			for(GameEntity gameEntity :batch) { //OPTIMIZE: too much calculation bellow, maybe cache some stuff

				//Frustum culling:
				gameEntity.transform.localToWorldPositionUnrotated(gameEntity.getModel().collider.getAabb().min, worldMin);
				Chunk.shiftPositionFromCamera(worldMin, gameEntity.transform.chunkPos, cameraChunkPos);
				gameEntity.transform.localToWorldPositionUnrotated(gameEntity.getModel().collider.getAabb().max, worldMax);
				Chunk.shiftPositionFromCamera(worldMax, gameEntity.transform.chunkPos, cameraChunkPos);

				if(projectionCamera.frustumIntersection.testAab(worldMin, worldMax)){
					loadTransformation(gameEntity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
				}
			}
			unbindModel();
		}

		for(TexturedModel texturedModel :gameObjectManager.getTileEntitiesToRender().keySet()) {
			prepareTexturedModel(texturedModel);
			List<TileEntity> batch = gameObjectManager.getTileEntitiesToRender().get(texturedModel);
			for(TileEntity tileEntity:batch) {

				tileEntity.transform.localToWorldPositionUnrotated(tileEntity.getModel().collider.getAabb().min, worldMin);
				Chunk.shiftPositionFromCamera(worldMin, tileEntity.transform.chunkPos, cameraChunkPos);
				tileEntity.transform.localToWorldPositionUnrotated(tileEntity.getModel().collider.getAabb().max, worldMax);
				Chunk.shiftPositionFromCamera(worldMax, tileEntity.transform.chunkPos, cameraChunkPos);

				if(projectionCamera.frustumIntersection.testAab(worldMin, worldMax)){
					loadTransformation(tileEntity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
				}
			}
			unbindModel();
		}

		staticShader.stop();
	}

	
	private void prepareTexturedModel(TexturedModel texturedModel) {
		RawModel rawModel = texturedModel.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());

		Texture texture = texturedModel.getTexture();
		Material material = texturedModel.getMaterial();
		staticShader.loadShineVariables(material.getShineDamper(), material.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
	
	private final Matrix4f tempMatrix = new Matrix4f();
	private void loadTransformation(GameObject object) {
		tempMatrix.set(object.transform.getTransformationMatrix());
		float x = Chunk.shiftChunkPosFromCamera(object.transform.chunkPos.x, cameraChunkPos.x);
		float z = Chunk.shiftChunkPosFromCamera(object.transform.chunkPos.y, cameraChunkPos.y);
		tempMatrix.translate(x,0,z);
		staticShader.loadTransformationMatrix(tempMatrix);
	}

	public void updateProjectionMatrix(Matrix4f projection) {
		staticShader.start();
		staticShader.loadProjectionMatrix(projection);
		staticShader.stop();
	}
}
