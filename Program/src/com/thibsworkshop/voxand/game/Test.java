package com.thibsworkshop.voxand.game;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.io.Binary;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.rendering.gui.HUD;
import com.thibsworkshop.voxand.rendering.lighting.DirectionalLight;
import com.thibsworkshop.voxand.rendering.lighting.PointLight;
import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.physics.collisions.Collider;
import com.thibsworkshop.voxand.rendering.renderers.*;
import com.thibsworkshop.voxand.rendering.shaders.LineShader;
import com.thibsworkshop.voxand.rendering.shaders.StaticShader;
import com.thibsworkshop.voxand.rendering.shaders.TerrainShader;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.rendering.textures.Material;
import com.thibsworkshop.voxand.terrain.Chunk.TerrainInfo;
import com.thibsworkshop.voxand.physics.collisions.AABB;
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

        //window = new Window(1280,720,false);
        window = new Window(1920,1080,true);

        Time.init();
        Loader.init();
        HUD.init();
        Chunk.init();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private void loop() {

        Material mat = new Material(10,1);

        AABB playerAABB = AABB.createCenterSize(new Vector3f(0),0.9f,1.8f,0.9f);
        Collider playerCollider = new Collider(playerAABB);

        AABB chickAABB = AABB.createMinMax(new Vector3f(-0.3f,0f,-0.3f),new Vector3f(0.3f,0.9f,0.3f));
        Collider chickCollider = new Collider(chickAABB);

        TexturedModel texturedModel = new TexturedModel("chr_knight","chr_knight", mat,playerCollider);
        TexturedModel chickModel = new TexturedModel("chick","chick", mat,chickCollider);

        ProjectionCamera camera = new ProjectionCamera(70);
        Camera.main = camera;

        OrthographicCamera orthoCamera = new OrthographicCamera(1);

        Player player = new Player(texturedModel,50,camera);
        player.transform.setPosition(0,175,0);

        Input input = new Input(window);

        DirectionalLight sun = new DirectionalLight(new Vector3f(1,1,1), 1, new Vector3f(1,0,0));
        sun.rotate(new Vector3f(0,3.14f/4f,0));
        sun.rotate(new Vector3f(3.14f/4f+0.2f,0,0));

        PointLight[] lights = new PointLight[MasterRenderer.MAX_LIGHT];



        TerrainInfo terrainInfo = new TerrainInfo(0.01f,10,4,1,1);
        TerrainManager terrainManager = new TerrainManager(terrainInfo);

        GameObjectManager gameObjectManager = new GameObjectManager();

        MasterRenderer renderer = new MasterRenderer(sun,lights);
        TerrainShader terrainShader = new TerrainShader();
        StaticShader staticShader = new StaticShader();
        LineShader lineShader = new LineShader();
        TerrainRenderer terrainRenderer = new TerrainRenderer(terrainShader,camera,renderer);
        GameObjectRenderer gameObjectRenderer = new GameObjectRenderer(staticShader,camera,renderer);
        gameObjectRenderer.linkManager(gameObjectManager);
        LineRenderer lineRenderer = new LineRenderer(lineShader,camera,orthoCamera);
        lineRenderer.linkGameObjectManager(gameObjectManager);
        Renderer[] renderers = {terrainRenderer,gameObjectRenderer,lineRenderer};

       //GameEntity chick = new GameEntity(chickModel,1);
        //chick.transform.setPosition(0,200,0);
        /*GameEntity chick1 = new GameEntity(chickModel,1);
        chick1.transform.setPosition(6,205,5);
        GameEntity chick2 = new GameEntity(chickModel,1);
        chick2.transform.setPosition(7,210,5);
        GameEntity chick3 = new GameEntity(chickModel,1);
        chick3.transform.setPosition(8,215,5);
        GameEntity chick4 = new GameEntity(chickModel,1);
        chick4.transform.setPosition(9,220,5);*/

        //gameObjectManager.processEntity(chick);

        gameObjectManager.processEntity(player);
        //Timing.enable(TerrainManager.debugName);
        //Timing.enable(MasterRenderer.debugName);

        float wait = 0.5f;
        float time = Time.getTime() + wait;
        boolean done = false;
        player.enableGravity(false);
        while ( !window.shouldWindowClose()) {
            Time.update();
            input.updateInput();
            if(!done && Time.getTime() > time) {
                if(player.getMode() == Player.Mode.Survival)
                    player.enableGravity(true);
                Binary.readVoxFile("Program/res/data/vox/island.vox");
                done = true;
            }


            if(Input.isKeyDown(GLFW_KEY_ENTER)){
                Debug.printVector(player.transform.chunkPos);
            }

            player.move();
            //Debug.printVector(player.transform.getPosition());
            //Game Logic

            //Debug.printVector(player.transform.forward());

            if(Input.isKeyHold(GLFW_KEY_UP)){
                sun.rotate(new Vector3f(0.025f,0,0));
            }
            if(Input.isKeyHold(GLFW_KEY_DOWN))
                sun.rotate(new Vector3f(-0.025f,0,0));

            //System.out.println(camera.getViewMatrix());

            //if(Input.isKeyDown(GLFW_KEY_ENTER))
                //Timing.print(MasterRenderer.debugName,"Terrain",5);
                //Timing.print(TerrainManager.debugName,"Refreshing",5);

            gameObjectManager.update();
            if(player.getMode() == Player.Mode.Survival)
                camera.update(player.transform,1.5f);
            else if (player.getMode() == Player.Mode.Spectator)
                camera.update(camera.transform,0);
            terrainManager.refreshChunks();

            renderer.render(renderers);
            window.updateWindow();
        }

        for(Renderer r : renderers)
            r.cleanUp();
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
