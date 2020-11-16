package com.thibsworkshop.voxand.game;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.entities.Player;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.lighting.DirectionalLight;
import com.thibsworkshop.voxand.lighting.PointLight;
import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.models.Model;

import com.thibsworkshop.voxand.rendering.EntityRenderer;
import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.rendering.MasterRenderer;
import com.thibsworkshop.voxand.shaders.StaticShader;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.textures.Material;
import com.thibsworkshop.voxand.terrain.Terrain.TerrainInfo;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Test {

    private Window window;
    private static GLFWErrorCallback errorCallback;


    private void init() {

        if(!glfwInit()){
            System.err.println("GLFW Failed to initialize");
        }

        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        window = new Window(1280,720,false);
        Time.init();
        Loader.init();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private void loop() {

        Material mat = new Material(10,1);
        Model model = new Model("chr_knight","chr_knight", mat);

        Player player = new Player(model,null);
        Camera camera = new Camera(player);
        Camera.mainCamera = camera;

        Input input = new Input(window);

        DirectionalLight sun = new DirectionalLight(new Vector3f(1,1,1), 1, new Vector3f(1,0,0));
        sun.rotate(new Vector3f(0,3.14f/4f,0));

        PointLight[] lights = new PointLight[MasterRenderer.MAX_LIGHT];

        MasterRenderer renderer = new MasterRenderer(sun,lights);

        TerrainInfo terrainInfo = new TerrainInfo(1,1,1,1,1);
        TerrainManager terrainManager = new TerrainManager(terrainInfo);


        Entity entity = new Entity(model ,new Vector3f(0,0,5),null);
        Entity entity2 = new Entity(model ,new Vector3f(0,0,-5),null);
        Entity entity3 = new Entity(model ,new Vector3f(-5,0,0),null);
        Entity entity4 = new Entity(model ,new Vector3f(5,0,0),null);

        renderer.processEntity(entity);
        renderer.processEntity(entity2);
        renderer.processEntity(entity3);
        renderer.processEntity(entity4);

        while ( !window.shouldWindowClose() ) {
            Time.update();
            input.updateInput();
            camera.move();
            terrainManager.refreshChunks();
            renderer.render(camera);
            window.updateWindow();
        }

        renderer.cleanUp();
        terrainManager.cleanUp();
    }

    public void end(){

        window.closeWindow();
        Loader.cleanUp();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void run() {

        init();
        loop();
        end();

    }

    public static void main(String[] args) {
        new Test().run();
    }
}
