package com.thibsworkshop.voxand.io;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time {

    private static int fps;
    private static float deltaTime;

    private static long lastFrameTime;

    private static long time;
    private static long milliTime;
    private static long microTime;

    private static int frameCount = 0;

    private static long lastSecond = 0;

    public static void init(){
        lastFrameTime = microTime;
    }

    public static void update(){
        double glfwtime = glfwGetTime();
        time = (long) (glfwtime);
        milliTime = (long) (glfwtime * 1000);
        microTime = (long) (glfwtime * 1000000);

        if(lastSecond < time){
            fps = frameCount;
            lastSecond = time;
            frameCount = 0;
        }

        deltaTime = (float)(microTime - lastFrameTime)/1000000f;
        lastFrameTime = microTime;

        frameCount++;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static long getFrameTime(){ return time; }

    public static long getFrameMilliTime() { return milliTime; }

    public static long getFrameMicroTime() { return microTime; }

    public static long getTime(){ return (long) (glfwGetTime()); }

    public static long getMilliTime() { return (long) (glfwGetTime() * 1000); }

    public static long getMicroTime() { return (long) (glfwGetTime() * 1000000);}

    public static int getFps() {
        return fps;
    }
}
