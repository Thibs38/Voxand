package com.thibsworkshop.voxand.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thibsworkshop.voxand.debugging.Timing;
import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Collider;
import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.lighting.DirectionalLight;
import com.thibsworkshop.voxand.lighting.PointLight;
import com.thibsworkshop.voxand.models.Model;
import com.thibsworkshop.voxand.shaders.StaticShader;
import com.thibsworkshop.voxand.shaders.TerrainShader;
import com.thibsworkshop.voxand.terrain.Terrain;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.glClearColor;

public class MasterRenderer {

	public static final int MAX_LIGHT = 16;
	
	private static final Vector3f SKY_COLOR = new Vector3f(0.529f,0.808f,0.922f);

	private StaticShader staticShader = new StaticShader();
	public static EntityRenderer entityRenderer;

	public static TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();
		

	DirectionalLight sun;
	PointLight[] lights;

	public static String debugName = "Rendering";
	
	public MasterRenderer(DirectionalLight sun, PointLight[] lights) {

		this.sun = sun;
		this.lights = lights;
		glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z,1);

		entityRenderer = new EntityRenderer(staticShader,this);
		terrainRenderer = new TerrainRenderer(terrainShader,this);

		Timing.add(debugName, new String[]{
			"Entities",
			"Terrain"
		});
	}
	
	public void render(Camera camera) {
		updateFrustum(camera);
		prepare();

		Timing.start(debugName,"Entities");

		staticShader.start();
		staticShader.loadFogVariables(0.0035f, 5f, SKY_COLOR);
		staticShader.loadLights(lights);
		staticShader.loadViewMatrix(Camera.mainCamera.getViewMatrix());
		staticShader.loadAmbientLight(sun);
		entityRenderer.render(camera);
		staticShader.stop();

		Timing.stop(debugName,"Entities");


		Timing.start(debugName,"Terrain");

		terrainShader.start();
		terrainShader.loadFogVariables(0.0035f, 5f, SKY_COLOR);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(Camera.mainCamera.getViewMatrix());
		terrainShader.loadAmbientLight(sun);
		terrainRenderer.render(camera);
		terrainShader.stop();

		Timing.stop(debugName,"Terrain");

	}

	public void prepare() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(SKY_COLOR.x,SKY_COLOR.y,SKY_COLOR.z, 1);
	}
	
	public void cleanUp() {
		staticShader.cleanUp();
		terrainShader.cleanUp();
	}
	
	private void updateFrustum(Camera camera) {
        // Calculate projection view matrix
        //Matrix4f.mul(fakeProjectionMatrix,viewMatrix,prjViewMatrix);
        // Get frustum planes
        //frustumPlanes = Maths.frustumPlanes(prjViewMatrix,true);
        
        //System.out.println("Camera: " + camera.getRotation() + " near: " + frustumPlanes[4].normal + " | far: " + frustumPlanes[4].normal);

    }

    public void processEntity(Entity entity){
		entityRenderer.processEntity(entity);
	}

	public void processEntities(List<Entity> entities){
		entityRenderer.processEntities(entities);
	}

	public void processTerrains(List<Terrain> terrains){

	}
	
	/*public boolean boxInsideFrustum(Vector3f boxMin, Vector3f boxMax) {
		
		for (int i = 0; i < 6; i++) {
	        if(Collider.boxFrontOfPlane(frustumPlanes[i], boxMin, boxMax))
	        	return false;
	    }
	    return true;
	}*/
	
	//Return true if the specified chunk is inside of the camera frustum, otherwise false.
	/*public boolean chunkInsideFrustum(Vector3f chunkPos) {
        Vector3f chunkPosMax = new Vector3f(chunkPos.x + Terrain.CHUNK_SIZE, Terrain.CHUNK_HEIGHT, chunkPos.z + Terrain.CHUNK_SIZE);
	    for (int i = 0; i < 6; i++) {

	        if(Collider.boxToPlaneCollision(frustumPlanes[i], new Vector3f[] {chunkPos,chunkPosMax}) == 0)
	        	return false;
	    }
	    return true;
	}*/

}
