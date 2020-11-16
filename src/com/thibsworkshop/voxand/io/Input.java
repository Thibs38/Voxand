package com.thibsworkshop.voxand.io;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Input {

	public static enum KeyState{
		SLEEP, DOWN, HOLD, UP
	}
	private static Map<Integer,KeyState> keys = new HashMap<Integer, KeyState>();
	private Map<Integer,KeyState> nextState = new HashMap<Integer, KeyState>();
	
	private static Vector2d mousePosition;
	private static Vector2d lastMousePosition;

	private static Vector2d mouseDelta;

	private Window window;
	
	private static GLFWKeyCallback keyCallback;
	private static GLFWCursorPosCallback cursorPosCallback;
	private static GLFWMouseButtonCallback mouseButtonCallback;
	
	public Input(Window window) {
		
		this.window = window;
		
		keys.put(GLFW.GLFW_KEY_Z,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_Q,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_S,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_D,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_SPACE,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_LEFT_SHIFT,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_ESCAPE,KeyState.SLEEP);
		
		keys.put(GLFW.GLFW_MOUSE_BUTTON_LEFT,KeyState.SLEEP);
		keys.put(GLFW.GLFW_MOUSE_BUTTON_RIGHT,KeyState.SLEEP);
		keys.put(GLFW.GLFW_MOUSE_BUTTON_MIDDLE,KeyState.SLEEP);
		
		mousePosition = new Vector2d();
		lastMousePosition = new Vector2d();
		mouseDelta = new Vector2d();

		GLFW.glfwSetInputMode(window.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

		
		setMouseCallback();
		setKeyBoardCallback();
	}
	
	public void updateInput() {
		
		mousePosition.sub(lastMousePosition,mouseDelta);
		lastMousePosition = new Vector2d(mousePosition);
		
		//System.out.println("mouse position: " + mousePosition + "  " + mouseDelta);

		
		nextState.forEach((k,v) -> {keys.put(k,v);}); //Update the key state
		nextState.clear();
		
		keys.forEach((k,v) -> { //Set the next key state for next frame
			if(v == KeyState.DOWN)
				nextState.put(k, KeyState.HOLD);
			if(v == KeyState.UP)
				nextState.put(k, KeyState.SLEEP);
    	});
	}
	
	///----------------------------------------///
	///------------- Callbacks ----------------///
	///----------------------------------------///
	
	private void setMouseCallback() {
		GLFW.glfwSetCursorPosCallback(window.window, (cursorPosCallback = new GLFWCursorPosCallback() {
		    @Override
		    public void invoke(long window, double xpos, double ypos) {
		    	mousePosition.x = xpos;
		    	mousePosition.y = Input.this.window.getHeight() - ypos;
		    }

		}));
		
		GLFW.glfwSetMouseButtonCallback(window.window, (mouseButtonCallback = new GLFWMouseButtonCallback() {

		    @Override
		    public void invoke(long window, int button, int action, int mods) {
		        keys.forEach((k,v) -> {
		        	if(button == k) {
		    			if(action == GLFW.GLFW_PRESS)
		    				keys.put(k,KeyState.DOWN);
		    			else if (action == GLFW.GLFW_RELEASE)
		    				keys.put(k,KeyState.UP);
		    		}
		        });
		    }

		}));
	}

	
	private void setKeyBoardCallback() {

		GLFW.glfwSetKeyCallback(window.window, (keyCallback = new GLFWKeyCallback() {

		    @Override
		    public void invoke(long window, int key, int scancode, int action, int mods) {
		    	
		    	keys.forEach((k,v) -> {
		    		if(key == k) {
		    			if(action == GLFW.GLFW_PRESS)
		    				keys.put(k,KeyState.DOWN);
		    			else if (action == GLFW.GLFW_RELEASE)
		    				keys.put(k,KeyState.UP);
		    		}
		    	});
		    }
		    
		}));
	}

	///----------------------------------------///
	///--------------- Getters ----------------///
	///----------------------------------------///
	
	public static KeyState getKeyState(int key) {
		return keys.get(key);
	}
	
	public static boolean isKeyDown(int key) {
		return keys.get(key) == KeyState.DOWN;
	}
	
	public static boolean isKeyHold(int key) {
		KeyState k = keys.get(key);
		return k == KeyState.HOLD || k == KeyState.DOWN;
	}
	
	public static boolean isKeyUp(int key) {
		return keys.get(key) == KeyState.UP;
	}
	
	public static Vector2d getMouseDelta() {
		return mouseDelta;
	}
	
	public static Vector2d getMousePosition() {
		return mousePosition;
	}
}
