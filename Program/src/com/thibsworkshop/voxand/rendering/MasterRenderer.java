package com.thibsworkshop.voxand.rendering;

import java.util.List;

import com.thibsworkshop.voxand.debugging.Timing;
import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.lighting.DirectionalLight;
import com.thibsworkshop.voxand.lighting.PointLight;
import com.thibsworkshop.voxand.models.RawModel;
import com.thibsworkshop.voxand.shaders.LineShader;
import com.thibsworkshop.voxand.shaders.StaticShader;
import com.thibsworkshop.voxand.shaders.TerrainShader;
import com.thibsworkshop.voxand.terrain.Terrain;
import org.joml.FrustumIntersection;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.joml.Vector3f;

import javax.sound.sampled.Line;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class MasterRenderer {

	public static final int MAX_LIGHT = 16;
	
	private static final Vector3f SKY_COLOR = new Vector3f(0.529f,0.808f,0.922f);

	private StaticShader staticShader = new StaticShader();
	public static EntityRenderer entityRenderer;

	private TerrainShader terrainShader = new TerrainShader();
	public static TerrainRenderer terrainRenderer;

	private LineShader lineShader = new LineShader();
	public static LineRenderer lineRenderer;

	FrustumIntersection frustumIntersection;

	DirectionalLight sun;
	PointLight[] lights;

	public static String debugName = "Rendering";

	//TODO: Make fog variables handle class
	//TODO: Make a function to calculate fog variables based on distance
	
	public MasterRenderer(DirectionalLight sun, PointLight[] lights) {

		this.sun = sun;
		this.lights = lights;
		glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z,1);

		entityRenderer = new EntityRenderer(staticShader);
		terrainRenderer = new TerrainRenderer(terrainShader);
		lineRenderer = new LineRenderer(lineShader);


		Timing.add(debugName, new String[]{
			"Entities",
			"Terrain"
		});
	}
	
	public void render(Camera camera) {
		prepare();

		Timing.start(debugName,"Entities");

		staticShader.start();
		staticShader.loadFogVariables(0.0035f, 5f, SKY_COLOR);
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(Camera.main.getViewMatrix());
		staticShader.loadAmbientLight(sun);
		entityRenderer.render(camera);
		staticShader.stop();

		Timing.stop(debugName,"Entities");

		lineShader.start();
		lineShader.loadRenderingVariables(camera.getProjectionViewMatrix());
		lineRenderer.render(camera);
		lineShader.stop();

		Timing.start(debugName,"Terrain");

		terrainShader.start();
		terrainShader.loadFogVariables(0.0035f, 5f, SKY_COLOR);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(Camera.main.getViewMatrix());
		terrainShader.loadAmbientLight(sun);
		terrainRenderer.render(camera);
		terrainShader.stop();

		Timing.stop(debugName,"Terrain");
	}

	public void prepare() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}
	
	public void cleanUp() {
		staticShader.cleanUp();
		terrainShader.cleanUp();
	}


    public void processEntity(Entity entity){
		entityRenderer.processEntity(entity);
	}

	public void processEntities(List<Entity> entities){
		entityRenderer.processEntities(entities);
	}

	public void processLine(RawModel model){ lineRenderer.setRawModel(model);}

}
