package com.thibsworkshop.voxand.io;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {

    private static int currentFps;
    private static float deltaTime;

    private static long lastFrameTime;

    private static long time;
    private static long milliTime;
    private static long microTime;

    public static void init(){
        lastFrameTime = milliTime;
    }

    public static void update(){
        double glfwtime = glfwGetTime();
        time = (long) (glfwtime);
        milliTime = (long) (glfwtime * 1000);
        microTime = (long) (glfwtime * 1000000);

        deltaTime = (float)(milliTime - lastFrameTime)/1000f;
        currentFps = Math.round(1f/deltaTime);
        lastFrameTime = milliTime;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static long getTime(){ return time; }

    public static long getMilliTime() { return milliTime; }

    public static long getMicroTime() {
        return microTime;
    }

    public static int getFps() {
        return currentFps;
    }
}
