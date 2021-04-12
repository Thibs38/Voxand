package com.thibsworkshop.voxand.rendering.renderers;


import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.shaders.StaticShader;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.rendering.textures.Texture;
import com.thibsworkshop.voxand.terrain.Chunk;
import org.joml.FrustumIntersection;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.joml.Matrix4f;
import org.lwjgl.system.CallbackI;

import java.util.List;


public class GameObjectRenderer extends Renderer {

	private final StaticShader shader;

	GameObjectManager gameObjectManager;
	private final FrustumIntersection frustumIntersection;

	private final Vector2i playerChunkPos;

	public GameObjectRenderer(StaticShader shader){
		super(shader);
		this.shader = shader;
		playerChunkPos = Player.player.transform.chunkPos;
		updateProjectionMatrix(Camera.main.getProjectionMatrix());

		frustumIntersection = Camera.main.frustumIntersection;
	}

	public void linkManager(GameObjectManager gameObjectManager){
		this.gameObjectManager = gameObjectManager;
	}

	private final Vector3f worldMin = new Vector3f();
	private final Vector3f worldMax = new Vector3f();

	@Override
	public void render(Camera camera) {

		for(TexturedModel texturedModel : gameObjectManager.getEntitiesToRender().keySet()) {
			prepareTexturedModel(texturedModel);
			List<GameEntity> batch = gameObjectManager.getEntitiesToRender().get(texturedModel);
			for(GameEntity gameEntity :batch) { //OPTIMIZE: to much calculation bellow, maybe cache some stuff

				gameEntity.transform.localToWorldPositionUnrotated(gameEntity.getModel().collider.getAabb().min, worldMin);
				Chunk.shiftPositionFromPlayer(worldMin, gameEntity.transform.chunkPos, playerChunkPos);
				gameEntity.transform.localToWorldPositionUnrotated(gameEntity.getModel().collider.getAabb().max, worldMax);
				Chunk.shiftPositionFromPlayer(worldMax, gameEntity.transform.chunkPos, playerChunkPos);

				if(frustumIntersection.testAab(worldMin, worldMax)){
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
				Chunk.shiftPositionFromPlayer(worldMin, tileEntity.transform.chunkPos, playerChunkPos);
				tileEntity.transform.localToWorldPositionUnrotated(tileEntity.getModel().collider.getAabb().max, worldMax);
				Chunk.shiftPositionFromPlayer(worldMax, tileEntity.transform.chunkPos, playerChunkPos);

				if(frustumIntersection.testAab(worldMin, worldMax)){
					loadTransformation(tileEntity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
				}
			}
			unbindModel();
		}
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
		shader.loadShineVariables(material.getShineDamper(), material.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
	
	private final Matrix4f tempMatrix = new Matrix4f();
	private void loadTransformation(GameObject object) {
		tempMatrix.set(object.transform.getTransformationMatrix());
		float x = Chunk.shiftChunkPosFromPlayer(object.transform.chunkPos.x, playerChunkPos.x);
		float z = Chunk.shiftChunkPosFromPlayer(object.transform.chunkPos.y, playerChunkPos.y);
		tempMatrix.translate(x,0,z);
		shader.loadTransformationMatrix(tempMatrix);
	}

	public void updateProjectionMatrix(Matrix4f projection) {
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
	}
}
