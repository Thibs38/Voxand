package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.debugging.Timing;
import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.rendering.lighting.DirectionalLight;
import com.thibsworkshop.voxand.rendering.lighting.PointLight;
import com.thibsworkshop.voxand.rendering.shaders.LineShader;
import com.thibsworkshop.voxand.rendering.shaders.StaticShader;
import com.thibsworkshop.voxand.rendering.shaders.TerrainShader;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {

	public static final int MAX_LIGHT = 16;
	
	private static final Vector3f SKY_COLOR = new Vector3f(0.529f,0.808f,0.922f);

	private final StaticShader staticShader = new StaticShader();
	public static GameObjectRenderer gameObjectRenderer;

	private final TerrainShader terrainShader = new TerrainShader();
	public static TerrainRenderer terrainRenderer;

	private final LineShader lineShader = new LineShader();
	public static LineRenderer lineRenderer;

	FrustumIntersection frustumIntersection;

	DirectionalLight sun;
	PointLight[] lights;

	public static String debugName = "Rendering";

	float currentLayer = 0;
	float nextLayer = 0;

	public MasterRenderer(DirectionalLight sun, PointLight[] lights) {

		this.sun = sun;
		this.lights = lights;
		glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z,1);

		gameObjectRenderer = new GameObjectRenderer(staticShader);
		terrainRenderer = new TerrainRenderer(terrainShader);
		lineRenderer = new LineRenderer(lineShader);


		Timing.add(debugName, new String[]{
			"Entities",
			"Terrain"
		});
	}
	
	public void render(Camera camera) {
		nextLayer = TerrainManager.main.currentLayer;
		currentLayer = Maths.lerp(currentLayer, nextLayer, 0.01f); //We linearly interpolate between current and next
		float fogDistance = (currentLayer-4)* Chunk.F_CHUNK_SIZE;
		//float fogDistance = Config.chunkViewDist * Chunk.F_CHUNK_SIZE * 2;
		prepare();

		Timing.start(debugName,"Entities");

		staticShader.start();
		staticShader.loadFogVariables(0.05f, fogDistance, SKY_COLOR);//0.0035f
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(Camera.main.getViewMatrix());
		staticShader.loadAmbientLight(sun);
		gameObjectRenderer.render(camera);
		staticShader.stop();

		Timing.stop(debugName,"Entities");

		if(Debug.isDebugMode()){
			lineShader.start();
			lineShader.loadRenderingVariables(camera.getProjectionViewMatrix());
			lineRenderer.render(camera);
			lineShader.stop();
		}

		Timing.start(debugName,"Terrain");

		terrainShader.start();
		terrainShader.loadFogVariables(0.05f, fogDistance, SKY_COLOR);//0.0035f
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(Camera.main.getViewMatrix());
		terrainShader.loadAmbientLight(sun);
		terrainRenderer.render(camera);
		terrainShader.stop();

		Timing.stop(debugName,"Terrain");

		glClear(GL_DEPTH_BUFFER_BIT); //Clear the depth buffer, start com.thibsworkshop.voxand.rendering overlays

		if(Debug.isDebugMode()){
			lineShader.start();
			lineRenderer.renderXYZ(camera);
			lineShader.stop();
		}

	}

	public void prepare() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}
	
	public void cleanUp() {
		staticShader.cleanUp();
		terrainShader.cleanUp();
	}

}
