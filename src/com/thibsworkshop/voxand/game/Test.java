package com.thibsworkshop.voxand.game;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.lighting.DirectionalLight;
import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.models.Model;

import com.thibsworkshop.voxand.rendering.EntityRenderer;
import com.thibsworkshop.voxand.rendering.Input;
import com.thibsworkshop.voxand.rendering.Window;
import com.thibsworkshop.voxand.shaders.StaticShader;
import com.thibsworkshop.voxand.textures.Material;
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

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private void loop() {

        Material mat = new Material(10,1);
        Entity entity = new Entity(new Model("chr_knight","chr_knight", mat) ,new Vector3f(0,0,5));
        Entity entity2 = new Entity(new Model("chr_knight","chr_knight",mat) ,new Vector3f(0,0,-5));
        Entity entity3 = new Entity(new Model("chr_knight","chr_knight",mat) ,new Vector3f(-5,0,0));
        Entity entity4 = new Entity(new Model("chr_knight","chr_knight",mat) ,new Vector3f(5,0,0));


        Camera camera = new Camera();

        Input input = new Input(window);

        DirectionalLight sun = new DirectionalLight(new Vector3f(1,1,1), 1, new Vector3f(1,0,0));
        sun.rotate(new Vector3f(0,3.14f/4f,0));

        StaticShader staticShader = new StaticShader();
        EntityRenderer renderer = new EntityRenderer(staticShader,camera.getProjectionMatrix(),sun);

        renderer.processEntity(entity);
        renderer.processEntity(entity2);
        renderer.processEntity(entity3);
        renderer.processEntity(entity4);

        while ( !window.shouldWindowClose() ) {
            input.updateInput();
            camera.move();
            renderer.render(camera);
            window.updateDisplay();
        }

        staticShader.cleanUp();
    }

    public void end(){

        window.closeDisplay();
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
