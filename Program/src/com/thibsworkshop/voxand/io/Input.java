package com.thibsworkshop.voxand.io;

import java.util.HashMap;
import java.util.Map;

import com.thibsworkshop.voxand.debugging.Debug;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

public class Input {


	public enum KeyState{
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

	//TODO: mouse input lag
	//TODO: current keyboard is american
	//TODO: sometimes key is hold where it is not
	public Input(Window window) {
		
		this.window = window;
		
		keys.put(GLFW.GLFW_KEY_Z,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_Q,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_S,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_D,KeyState.SLEEP);

		keys.put(GLFW.GLFW_KEY_UP,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_DOWN,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_LEFT,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_RIGHT,KeyState.SLEEP);

		keys.put(GLFW.GLFW_KEY_SPACE,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_LEFT_SHIFT,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_ESCAPE,KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_ENTER,KeyState.SLEEP);

		keys.put(GLFW.GLFW_KEY_F1, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F2, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F3, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F4, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F5, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F6, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F7, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F8, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F9, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F10, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F11, KeyState.SLEEP);
		keys.put(GLFW.GLFW_KEY_F12, KeyState.SLEEP);


		keys.put(GLFW.GLFW_MOUSE_BUTTON_LEFT,KeyState.SLEEP);
		keys.put(GLFW.GLFW_MOUSE_BUTTON_RIGHT,KeyState.SLEEP);
		keys.put(GLFW.GLFW_MOUSE_BUTTON_MIDDLE,KeyState.SLEEP);

		mousePosition = new Vector2d();
		lastMousePosition = new Vector2d();
		mouseDelta = new Vector2d();

		glfwSetInputMode(window.window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
		if (glfwRawMouseMotionSupported())
			glfwSetInputMode(window.window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
		
		setMouseCallback();
		setKeyBoardCallback();
	}
	
	public void updateInput() {

		glfwWaitEventsTimeout(0.007);

		mousePosition.sub(lastMousePosition,mouseDelta);
		lastMousePosition = new Vector2d(mousePosition);

		nextState.forEach((k,v) -> {
			keys.put(k,v);
		}); //Update the key state

		nextState.clear();

		keys.forEach((k,v) -> { //Set the next key state for next frame
			if(v == KeyState.DOWN)
				nextState.put(k, KeyState.HOLD);
			if(v == KeyState.UP)
				nextState.put(k, KeyState.SLEEP);
    	});

		//Debug inputs:

		if(isKeyDown(GLFW_KEY_F1)){
			Debug.setDebugMode(!Debug.isDebugMode());
		}
		if(isKeyDown(GLFW_KEY_F2)){
			Debug.setChunkAABB(!Debug.isChunkAABB());
		}
		if(isKeyDown(GLFW_KEY_F3)){
			Debug.setEntityAABB(!Debug.isEntityAABB());
		}
		if(isKeyDown(GLFW_KEY_F4)){
			Debug.setTileEntityAABB(!Debug.isTileEntityAABB());
		}

	}
	
	///----------------------------------------///
	///------------- Callbacks ----------------///
	///----------------------------------------///
	
	private void setMouseCallback() {
		glfwSetCursorPosCallback(window.window, (cursorPosCallback = new GLFWCursorPosCallback() {
		    @Override
		    public void invoke(long window, double xpos, double ypos) {
		    	mousePosition.x = xpos;
		    	mousePosition.y = Input.this.window.getHeight() - ypos;
		    }

		}));
		
		glfwSetMouseButtonCallback(window.window, (mouseButtonCallback = new GLFWMouseButtonCallback() {

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

		glfwSetKeyCallback(window.window, (keyCallback = new GLFWKeyCallback() {

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
