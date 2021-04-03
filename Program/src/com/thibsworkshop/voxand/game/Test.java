package com.thibsworkshop.voxand.game;

import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.rendering.lighting.DirectionalLight;
import com.thibsworkshop.voxand.rendering.lighting.PointLight;
import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.physics.Collider;
import com.thibsworkshop.voxand.physics.CollisionEngine;
import com.thibsworkshop.voxand.rendering.renderers.MasterRenderer;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.terrain.Chunk.TerrainInfo;
import com.thibsworkshop.voxand.toolbox.AABB;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.GLFW.*;

public class Test {


    private Window window;
    private static GLFWErrorCallback errorCallback;


    private void init() {

        if(!glfwInit()){
            System.err.println("GLFW Failed to initialize");
        }

        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        window = new Window(1280,720,false);
        //window = new Window(1920,1080,true);

        Time.init();
        Loader.init();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private void loop() {

        CollisionEngine collisionEngine = new CollisionEngine();

        Material mat = new Material(10,1);

        AABB aabb = AABB.createCenterSize(new Vector3f(0),0.9f,1.8f,0.9f);
        Collider collider = new Collider(aabb);
        TexturedModel texturedModel = new TexturedModel("chr_knight","chr_knight", mat,collider);

        Camera camera = new Camera();
        Camera.main = camera;

        Player player = new Player(texturedModel,1,camera);
        player.transform.setPosition(16,175,16);

        Input input = new Input(window);

        DirectionalLight sun = new DirectionalLight(new Vector3f(1,1,1), 1, new Vector3f(1,0,0));
        sun.rotate(new Vector3f(0,3.14f/4f,0));
        sun.rotate(new Vector3f(3.14f/4f+0.2f,0,0));

        PointLight[] lights = new PointLight[MasterRenderer.MAX_LIGHT];

        MasterRenderer renderer = new MasterRenderer(sun,lights);

        TerrainInfo terrainInfo = new TerrainInfo(0.01f,10,4,1,1);
        TerrainManager terrainManager = new TerrainManager(terrainInfo);

        GameObjectManager gameObjectManager = new GameObjectManager();

        Entity entity = new Entity(texturedModel,2);
        entity.transform.setPosition(5,200,5);
        //Entity entity2 = new Entity(texturedModel,new Transform(new Vector3f(0,0,-5)),1);
        //Entity entity3 = new Entity(texturedModel,new Transform(new Vector3f(-5,0,0)),1);
        //Entity entity4 = new Entity(texturedModel,new Transform(new Vector3f(5,0,0)),1);

        gameObjectManager.processEntity(entity);
        //gameObjectManager.processEntity(entity2);
        //gameObjectManager.processEntity(entity3);
        //gameObjectManager.processEntity(entity4);
            gameObjectManager.processEntity(player);
        //Timing.enable(TerrainManager.debugName);
        //Timing.enable(MasterRenderer.debugName);

        float wait = 3;
        float time = Time.getTime() + wait;
        player.enableGravity(false);
        while ( !window.shouldWindowClose() ) {
            Time.update();
            input.updateInput();
            if(player.mode == Player.Mode.Survival && Time.getTime() > time)
                player.enableGravity(true);

            player.move();
            //System.out.println(entity.transform.chunk == null);
            //Game Logic

            //Debug.printVector(player.transform.forward());

            if(Input.isKeyHold(GLFW_KEY_UP)){
                sun.rotate(new Vector3f(0.025f,0,0));
            }
            if(Input.isKeyHold(GLFW_KEY_DOWN))
                sun.rotate(new Vector3f(-0.025f,0,0));


            //if(Input.isKeyDown(GLFW_KEY_ENTER))
                //Timing.print(MasterRenderer.debugName,"Terrain",5);
                //Timing.print(TerrainManager.debugName,"Refreshing",5);

            gameObjectManager.update();
            terrainManager.refreshChunks();
            player.cameraUpdate();
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
