package com.thibsworkshop.voxand.rendering.renderers;


import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.shaders.StaticShader;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.rendering.textures.Texture;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.joml.Matrix4f;

import java.util.List;


public class GameObjectRenderer extends Renderer {

	private StaticShader shader;

	GameObjectManager gameObjectManager;
	private FrustumIntersection frustumIntersection;


	public GameObjectRenderer(StaticShader shader){
		super(shader);
		this.shader = shader;
		updateProjectionMatrix(Camera.main.getProjectionMatrix());

		frustumIntersection = Camera.main.frustumIntersection;
	}

	public void linkManager(GameObjectManager gameObjectManager){
		this.gameObjectManager = gameObjectManager;
	}

	@Override
	public void render(Camera camera) {

		for(TexturedModel texturedModel : gameObjectManager.getEntitiesToRender().keySet()) {
			prepareTexturedModel(texturedModel);
			List<Entity> batch = gameObjectManager.getEntitiesToRender().get(texturedModel);
			for(Entity entity:batch) { //OPTIMIZE: to much calculation bellow, maybe cache some stuff
				Vector3f worldMin = entity.transform.localToWorldPositionUnrotated(entity.getModel().collider.getAabb().min);
				Vector3f worldMax = entity.transform.localToWorldPositionUnrotated(entity.getModel().collider.getAabb().max);
				if(frustumIntersection.testAab(worldMin, worldMax)){
					loadTransformation(entity);
					GL11.glDrawElements(GL11.GL_TRIANGLES, texturedModel.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
				}
			}
			unbindModel();
		}

		for(TexturedModel texturedModel :gameObjectManager.getTileEntitiesToRender().keySet()) {
			prepareTexturedModel(texturedModel);
			List<TileEntity> batch = gameObjectManager.getTileEntitiesToRender().get(texturedModel);
			for(TileEntity tileEntity:batch) {
				Vector3f worldMin = tileEntity.transform.localToWorldPositionUnrotated(tileEntity.getModel().collider.getAabb().min);
				Vector3f worldMax = tileEntity.transform.localToWorldPositionUnrotated(tileEntity.getModel().collider.getAabb().max);
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
	

	private void loadTransformation(GameObject object) {
		shader.loadTransformationMatrix(object.transform.getTransformationMatrix());
	}

	public void updateProjectionMatrix(Matrix4f projection) {
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
	}
}
