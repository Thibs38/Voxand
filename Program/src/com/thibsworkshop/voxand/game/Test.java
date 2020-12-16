package com.thibsworkshop.voxand.game;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.lighting.DirectionalLight;
import com.thibsworkshop.voxand.lighting.PointLight;
import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.models.TexturedModel;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.physics.Collider;
import com.thibsworkshop.voxand.rendering.MasterRenderer;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.textures.Material;
import com.thibsworkshop.voxand.terrain.Chunk.TerrainInfo;
import com.thibsworkshop.voxand.toolbox.AABB;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;

public class Test {


    private Window window;
    private static GLFWErrorCallback errorCallback;


    //TODO: Put rendering on another thread to allow multiple input polling per frame


    private void init() {

        if(!glfwInit()){
            System.err.println("GLFW Failed to initialize");
        }

        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        window = new Window(1600,800,false);
        Time.init();
        Loader.init();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private void loop() {

        Material mat = new Material(10,1);

        AABB aabb = AABB.createCenterSize(new Vector3f(0),1,2,1);
        Collider collider = new Collider(aabb);
        TexturedModel texturedModel = new TexturedModel("chr_knight","chr_knight", mat,collider);

        Camera camera = new Camera();
        Camera.main = camera;

        Player player = new Player(texturedModel,null,camera);

        Input input = new Input(window);

        DirectionalLight sun = new DirectionalLight(new Vector3f(1,1,1), 1, new Vector3f(1,0,0));
        sun.rotate(new Vector3f(0,3.14f/4f,0));
        sun.rotate(new Vector3f(3.14f/4f,0,0));

        PointLight[] lights = new PointLight[MasterRenderer.MAX_LIGHT];

        MasterRenderer renderer = new MasterRenderer(sun,lights);

        TerrainInfo terrainInfo = new TerrainInfo(0.01f,10,4,1,1);
        TerrainManager terrainManager = new TerrainManager(terrainInfo);

        GameObjectManager gameObjectManager = new GameObjectManager();

        Entity entity = new Entity(texturedModel, new Transform(new Vector3f(0,0,5)),null);
        Entity entity2 = new Entity(texturedModel,new Transform(new Vector3f(0,0,-5)),null);
        Entity entity3 = new Entity(texturedModel,new Transform(new Vector3f(-5,0,0)),null);
        Entity entity4 = new Entity(texturedModel,new Transform(new Vector3f(5,0,0)),null);

        gameObjectManager.processEntity(entity);
        gameObjectManager.processEntity(entity2);
        gameObjectManager.processEntity(entity3);
        gameObjectManager.processEntity(entity4);

        //Timing.enable(TerrainManager.debugName);
        //Timing.enable(MasterRenderer.debugName);

        while ( !window.shouldWindowClose() ) {
            Time.update();
            input.updateInput();
            player.move();


            //Game Logic

            if(Input.isKeyHold(GLFW_KEY_UP)){
                sun.rotate(new Vector3f(0.025f,0,0));
            }
            if(Input.isKeyHold(GLFW_KEY_DOWN))
                sun.rotate(new Vector3f(-0.025f,0,0));


            //if(Input.isKeyDown(GLFW_KEY_ENTER))
                //Timing.print(MasterRenderer.debugName,"Terrain",5);
                //Timing.print(TerrainManager.debugName,"Refreshing",5);

            gameObjectManager.update();
            terrainManager.refreshChunks(player);
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
