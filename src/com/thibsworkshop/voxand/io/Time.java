package com.thibsworkshop.voxand.io;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {

    private static int currentFps;
    private static float deltaTime;

    private static long lastFrameTime;

    public static void init(){
        lastFrameTime = getCurrentTime();
    }

    public static void update(){
        long currentFrameTime = getCurrentTime();
        deltaTime = (float)(currentFrameTime - lastFrameTime)/1000f;
        currentFps = Math.round(1f/deltaTime);
        lastFrameTime = currentFrameTime;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static long getCurrentTime() {
        return (long) (glfwGetTime() * 1000);
    }

    public static long getCurrentMicroTime() {
        return (long) (glfwGetTime() * 1000000);
    }

    public static int getFps() {
        return currentFps;
    }
}
