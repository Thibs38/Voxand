package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.debugging.Timing;
import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.game.Config;
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
	
	public static final Vector3f SKY_COLOR = new Vector3f(0.529f,0.808f,0.922f);

	private float fogDistance;

	private final DirectionalLight sun;
	private final PointLight[] lights;





	public static String debugName = "Rendering";

	float currentLayer = 0;
	float nextLayer = 0;

	public MasterRenderer(DirectionalLight sun, PointLight[] lights) {

		this.sun = sun;
		this.lights = lights;
		glClearColor(SKY_COLOR.x, SKY_COLOR.y, SKY_COLOR.z,1);


		Timing.add(debugName, new String[]{
			"Entities",
			"Terrain"
		});
	}

	//OPTIMIZE
	// flag the different variables like fog, sun and upload them only if they changed

	//FIXME
	// Add timing again
	
	public void render(Renderer[] renderers) {
		nextLayer = TerrainManager.main.currentLayer;
		currentLayer = Maths.lerp(currentLayer, nextLayer, 0.01f); //We linearly interpolate between current and next
		fogDistance = (currentLayer-4)* Chunk.F_CHUNK_SIZE;
		//float fogDistance = Config.chunkViewDist * Chunk.F_CHUNK_SIZE * 2;
		prepare();

		for(Renderer renderer : renderers){
			renderer.render();
		}


	}

	public void prepare() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
	}
	


	public float getFogDistance() {
		return fogDistance;
	}

	public DirectionalLight getSun() {
		return sun;
	}

	public PointLight[] getLights() {
		return lights;
	}

}
