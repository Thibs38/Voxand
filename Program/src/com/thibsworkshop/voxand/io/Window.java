package com.thibsworkshop.voxand.io;


import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;



//Multiple windows can be instantiated, for example when switching to fullscreen, changing resolution etc...
public class Window {
	
	private int WIDTH;
	private int HEIGHT;

	private double DWIDTH;
	private double DHEIGHT;

	private Vector2f viewport;

	private float ASPECT_RATIO;

	private int FPS_CAP = 120;
	private boolean FULLSCREEN;
	
	private static GLFWFramebufferSizeCallback framebufferSizeCallback;
	
	public boolean closeWindow;
		
	public long window;

	public static Window mainWindow;

	//TODO: Put debug code to Debug class and add control over what messages should be shown etc...
	
	public Window(int width, int height, boolean fullscreen) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.FULLSCREEN = fullscreen;
		this.ASPECT_RATIO = (float)width/(float)height;

		createWindow();
	}

	public void createWindow() {

		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		//glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE); //For debugging
		
		int windowWidth = WIDTH;
		int windowHeight = HEIGHT;
		long monitor = 0;
		if(FULLSCREEN) {
		    monitor = glfwGetPrimaryMonitor();
			PointerBuffer p = glfwGetMonitors();
			monitor = p.get(0);
		    //Retrieve the desktop resolution
		    GLFWVidMode vidMode = glfwGetVideoMode(monitor);
		    windowWidth = vidMode.width();
		    windowHeight = vidMode.height();
		}
		window = glfwCreateWindow(windowWidth, windowHeight, "LWJGL3", monitor, 0);
		
		IntBuffer framebufferWidth = BufferUtils.createIntBuffer(1), 
                framebufferHeight = BufferUtils.createIntBuffer(1);
		glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);
		//onResize(framebufferWidth.get(), framebufferHeight.get());
		
		if(window == 0) {
		    throw new RuntimeException("Failed to create window");
		}

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		GL.createCapabilities();

		glfwSwapInterval(0);

		//GLUtil.setupDebugMessageCallback();//Debugging

		glfwSetFramebufferSizeCallback(window, (framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
		    @Override
		    public void invoke(long window, int width, int height) {
		        onResize(width, height);
		    }
		}));
		
		GL11.glViewport(0, 0, WIDTH, HEIGHT);

		viewport = new Vector2f(WIDTH,HEIGHT);

		mainWindow = this; //After initializing everything, this display becomes the main one
	}

	///----------------------------------------///
	///----------- Display events -------------///
	///----------------------------------------///
	
	private void onResize(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		//MasterRenderer.createProjectionMatrix(false);
	}
	
	public void updateWindow() {
		
		//Display.sync(FPS);
		glfwSwapBuffers(window);




		if(Input.isKeyDown(GLFW_KEY_ESCAPE))
			closeWindow = true;
	}
	
	public void closeWindow() {
		glfwFreeCallbacks(window);

		glfwDestroyWindow(window);

	}
	
	public boolean shouldWindowClose() {
		return glfwWindowShouldClose(window) || closeWindow;
	}

	///----------------------------------------///
	///---------------- Getters ---------------///
	///----------------------------------------///

	public int getWidth(){return WIDTH;}

	public int getHeight(){return HEIGHT;}

	public double getDHEIGHT(){return DHEIGHT;}

	public double getDWIDTH(){return DWIDTH;}

	public float getAspectRatio(){return ASPECT_RATIO;}

	public Vector2f getViewport(){ return viewport; }
}
