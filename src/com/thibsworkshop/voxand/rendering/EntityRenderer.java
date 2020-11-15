package com.thibsworkshop.voxand.rendering;


import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.lighting.DirectionalLight;
import com.thibsworkshop.voxand.lighting.PointLight;
import com.thibsworkshop.voxand.models.Model;
import com.thibsworkshop.voxand.models.RawModel;
import com.thibsworkshop.voxand.shaders.StaticShader;
import com.thibsworkshop.voxand.textures.Material;
import com.thibsworkshop.voxand.textures.Texture;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL15;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;


public class EntityRenderer {

	public static final Vector3f SKY_COLOR = new Vector3f(0.529f,0.808f,0.922f);


	private StaticShader shader;

	DirectionalLight sun;

	private Map<Model,List<Entity>> entities = new HashMap<Model,List<Entity>>();

	//MasterRenderer masterRenderer;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix
						  //, MasterRenderer masterRenderer
						  , DirectionalLight sun

						  ) {

		this.sun = sun;
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z,1);

		//this.masterRenderer = masterRenderer;
	}

	
	public void render(Camera camera) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer


		shader.start();

		shader.loadFogVariables(0.0035f, 5f, SKY_COLOR);


		shader.loadLights(new ArrayList<PointLight>());
		shader.loadViewMatrix(camera.getViewMatrix());
		shader.loadAmbientLight(sun);
		for(Model model:entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
			}
			unbindTexturedModel();
		}

		shader.stop();

	}

	public void processEntity(Entity entity) {
		Model entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch == null) {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel,newBatch);
		}else {
			batch.add(entity);
		}
	}

	public void processEntities(List<Entity> entities) {
		for(Entity entity:entities) {
			processEntity(entity);
		}
	}
	
	private void prepareTexturedModel(Model model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());


		Texture texture = model.getTexture();
		Material material = model.getMaterial();
		shader.loadShineVariables(material.getShineDamper(), material.getReflectivity());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getID());
	}
	
	private void unbindTexturedModel() {
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,0);
		GL30.glBindVertexArray(0);
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.position, entity.rotation, entity.scale);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	public void updateProjectionMatrix(Matrix4f projection) {
		shader.start();
		shader.loadProjectionMatrix(projection);
		shader.stop();
	}
	
}
