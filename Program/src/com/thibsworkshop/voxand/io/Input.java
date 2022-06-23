package com.thibsworkshop.voxand.io;

import java.util.HashMap;
import java.util.Map;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector2d;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class Input {


	public enum KeyState{
		SLEEP, DOWN, HOLD, UP
	}


	public enum AxisName{ Horizontal, Vertical }
	private static final Map<Integer,KeyState> keys = new HashMap<>();
	private final Map<Integer,KeyState> nextState = new HashMap<>();
	private static final Map<AxisName,Axis> axes = new HashMap<>();
	
	private static final Vector2d mousePosition = new Vector2d(0);
	private static final Vector2d lastMousePosition= new Vector2d(0);

	private static final Vector2d mouseDelta= new Vector2d(0);

	private static final Vector2f acceleration= new Vector2f(0);

	private static final Vector2d lastDelta= new Vector2d(0);

	private static  boolean mouseMoved;

	private final Window window;
	
	private static GLFWKeyCallback keyCallback;
	private static GLFWCursorPosCallback cursorPosCallback;
	private static GLFWMouseButtonCallback mouseButtonCallback;

	//FIXME: sometimes key is hold where it is not
	//FIXME: when first moving the mouse, the movement is really sharp
	public Input(Window window) {
		
		this.window = window;
		
		keys.put(GLFW_KEY_W,KeyState.SLEEP);
		keys.put(GLFW_KEY_A,KeyState.SLEEP);
		keys.put(GLFW_KEY_S,KeyState.SLEEP);
		keys.put(GLFW_KEY_D,KeyState.SLEEP);

		keys.put(GLFW_KEY_UP,KeyState.SLEEP);
		keys.put(GLFW_KEY_DOWN,KeyState.SLEEP);
		keys.put(GLFW_KEY_LEFT,KeyState.SLEEP);
		keys.put(GLFW_KEY_RIGHT,KeyState.SLEEP);

		keys.put(GLFW_KEY_SPACE,KeyState.SLEEP);
		keys.put(GLFW_KEY_LEFT_SHIFT,KeyState.SLEEP);
		keys.put(GLFW_KEY_ESCAPE,KeyState.SLEEP);
		keys.put(GLFW_KEY_ENTER,KeyState.SLEEP);

		keys.put(GLFW_KEY_F1, KeyState.SLEEP);
		keys.put(GLFW_KEY_F2, KeyState.SLEEP);
		keys.put(GLFW_KEY_F3, KeyState.SLEEP);
		keys.put(GLFW_KEY_F4, KeyState.SLEEP);
		keys.put(GLFW_KEY_F5, KeyState.SLEEP);
		keys.put(GLFW_KEY_F6, KeyState.SLEEP);
		keys.put(GLFW_KEY_F7, KeyState.SLEEP);
		keys.put(GLFW_KEY_F8, KeyState.SLEEP);
		keys.put(GLFW_KEY_F9, KeyState.SLEEP);
		keys.put(GLFW_KEY_F10, KeyState.SLEEP);
		keys.put(GLFW_KEY_F11, KeyState.SLEEP);
		keys.put(GLFW_KEY_F12, KeyState.SLEEP);


		keys.put(GLFW_MOUSE_BUTTON_LEFT,KeyState.SLEEP);
		keys.put(GLFW_MOUSE_BUTTON_RIGHT,KeyState.SLEEP);
		keys.put(GLFW_MOUSE_BUTTON_MIDDLE,KeyState.SLEEP);

		axes.put(AxisName.Horizontal, new Axis(AxisName.Horizontal, GLFW_KEY_D, GLFW_KEY_A));
		axes.put(AxisName.Vertical, new Axis(AxisName.Vertical, GLFW_KEY_W, GLFW_KEY_S));


		glfwSetInputMode(window.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		if (glfwRawMouseMotionSupported())
			glfwSetInputMode(window.window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
		
		setMouseCallback();
		setKeyBoardCallback();
	}

	//To be called before eventpolling
	public void preUpdate(){
		mouseMoved = false;
	}
	
	public void updateInput() {

		preUpdate();
		glfwWaitEventsTimeout(0.007);

		mouseDelta.x = mousePosition.x - lastMousePosition.x;
		mouseDelta.y = mousePosition.y - lastMousePosition.y;

		//acceleration.x = (float)Math.max(Math.abs(mouseDelta.x - lastDelta.x)/Time.getDeltaTime()*0.1f,1d);
		//acceleration.y = (float)Math.max(Math.abs(mouseDelta.y - lastDelta.y)/Time.getDeltaTime()*0.1f,1d);

		lastDelta.x = mouseDelta.x;
		lastDelta.y = mouseDelta.y;

		lastMousePosition.x = mousePosition.x;
		lastMousePosition.y = mousePosition.y;


		nextState.forEach(keys::put); //Update the key state

		nextState.clear();

		axes.forEach((k,v)->{
			boolean pos = isKeyHold(v.positiveKey);
			boolean neg = isKeyHold(v.negativeKey);
			float rSensitivity = Math.min(v.sensitivity * Time.getDeltaTime(),1);
			if((pos && neg) || (!pos && !neg)){
				v.value = Maths.lerp(0,v.value,rSensitivity);
				if(v.value <= v.dead) v.value = 0;
			}
			if(pos)
				v.value = Maths.lerp(v.value,1,rSensitivity);
			if(neg)
				v.value = Maths.lerp(v.value,-1,rSensitivity);
		});

		//System.out.println(getAxis(AxisName.Horizontal) + " " + getAxis(AxisName.Vertical));

		keys.forEach((k,v) -> { //Set the next key state for next frame
			if(v == KeyState.DOWN)
				nextState.put(k, KeyState.HOLD);
			if(v == KeyState.UP)
				nextState.put(k, KeyState.SLEEP);
    	});

		debugInputs();

	}

	private void debugInputs(){
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
		if(isKeyDown(GLFW_KEY_F5))
			Debug.setRays(!Debug.isRays());
		if(isKeyDown(GLFW_KEY_F6))
			Debug.setCameraFree(!Debug.isCameraFree());
		if(isKeyDown(GLFW_KEY_F12)){
			Debug.clear();
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
		    	mousePosition.y = Input.this.window.getDHEIGHT() - ypos;
				mouseMoved = true;
		    }

		}));
		
		glfwSetMouseButtonCallback(window.window, (mouseButtonCallback = new GLFWMouseButtonCallback() {

		    @Override
		    public void invoke(long window, int button, int action, int mods) {
		        keys.forEach((k,v) -> {
		        	if(button == k) {
		    			if(action == GLFW_PRESS)
		    				keys.put(k,KeyState.DOWN);
		    			else if (action == GLFW_RELEASE)
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
		    			if(action == GLFW_PRESS)
		    				keys.put(k,KeyState.DOWN);
		    			else if (action == GLFW_RELEASE)
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

	public static float getAxis(AxisName axis){ return axes.get(axis).value; }
	
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

	public static Vector2f getAcceleration(){ return acceleration; }
	
	public static Vector2d getMousePosition() {
		return mousePosition;
	}

	public static boolean hasMouseMoved(){ return mouseMoved; }

	public static class Axis{
		public AxisName name;
		public int positiveKey;
		public int negativeKey;
		public float dead;
		public float sensitivity;
		public float value;

		public Axis(AxisName name, int positiveKey, int negativeKey, float dead, float sensitivity) {
			this.name = name;
			this.positiveKey = positiveKey;
			this.negativeKey = negativeKey;
			this.dead = dead;
			this.sensitivity = sensitivity;
			this.value = 0f;
		}
		
		public Axis(AxisName name, int positiveKey, int negativeKey){
			this(name,positiveKey,negativeKey,0.001f,3f);
		}
	}

}
