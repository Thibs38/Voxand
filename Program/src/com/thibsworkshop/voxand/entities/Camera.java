package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;


public class Camera {

	private Vector3f position = new Vector3f(0,50,0);
	private Vector3f rotation = new Vector3f(0,0,0);
	private final float speed = 40f;
	private final float rotationspeed = 20f;
	
	private boolean attachedToPlayer = false;

	private Matrix4f projectionMatrix;


	private Matrix4f viewMatrix;
	private Matrix4f projectionViewMatrix;

	public FrustumIntersection frustumIntersection;

	private float FOV = (float)Math.toRadians(70);
	private float NEAR_PLANE = 0.01f;
	private float FAR_PLANE = 1000;
	private float ASPECT_RATIO;

	public static Camera main;

	private Player player;

	public Vector2i currentChunk = new Vector2i(0,0);

	public Camera(Player player) {
		this.player = player;
		ASPECT_RATIO = Window.mainWindow.getAspectRatio();
		projectionMatrix = new Matrix4f().setPerspective(FOV,ASPECT_RATIO,NEAR_PLANE,FAR_PLANE);
		viewMatrix = Maths.createViewMatrix(this);
		projectionViewMatrix = new Matrix4f();
		projectionMatrix.mul(viewMatrix,projectionViewMatrix);


		frustumIntersection = new FrustumIntersection(projectionViewMatrix);
	}
	public void move() {
		float realSpeed = speed * Time.getDeltaTime();
		float realRotationSpeed = rotationspeed * Time.getDeltaTime();
		boolean moved = look(realRotationSpeed);

		if(attachedToPlayer) {
			position = new Vector3f(player.position.x,player.position.y + 2f,player.position.z);
		} else {
			moved = freeLook(realSpeed) || moved;
		}

		if(moved){
			viewMatrix = Maths.createViewMatrix(this);
			projectionMatrix.mul(viewMatrix,projectionViewMatrix);
			frustumIntersection.set(projectionViewMatrix);
		}

	}
	
	private boolean freeLook(float realSpeed) {
		boolean moved = false;
		if(Input.isKeyHold(GLFW.GLFW_KEY_Z)) {
			position.x += Math.sin(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))*realSpeed;
			position.z -= Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))*realSpeed;
		    position.y -= Math.sin(Math.toRadians(rotation.x)) * realSpeed;
		    moved = true;
		}
		
		if(Input.isKeyHold(GLFW.GLFW_KEY_S)) {
			position.x -= Math.sin(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))*realSpeed;
			position.z += Math.cos(Math.toRadians(rotation.y)) * Math.cos(Math.toRadians(rotation.x))*realSpeed;
		    position.y += Math.sin(Math.toRadians(rotation.x)) * realSpeed;
		    moved = true;
		}
		
		if(Input.isKeyHold(GLFW.GLFW_KEY_SPACE)) {
			position.y += realSpeed;
			moved = true;
		}
		if(Input.isKeyHold(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			position.y -= realSpeed;
			moved = true;
		}		
		return moved;
		
	}
	
	private boolean look(float realRotationSpeed) {
		boolean moved = false;
		if(Input.getMouseDelta().y != 0){
			rotation.y += Input.getMouseDelta().x * realRotationSpeed;
			moved = true;
		}
		if(Input.getMouseDelta().x != 0) {
			rotation.x -= Input.getMouseDelta().y * realRotationSpeed;
			moved = true;
		}
		//System.out.println(" x: " + Input.getMouseDelta().x + " y: " + Input.getMouseDelta().y); 
		//System.out.println("ROtation: " + rotation.x + " " + rotation.y);
		if(rotation.x > 90)
			rotation.x = 90;
		else if (rotation.x < -90)
			rotation.x = -90;
		
		rotation.y = rotation.y % 360;
		
		//player.rotation.y = rotation.y;
		return moved;
	}
	
	public Vector3f getPosition() { return position; }

	public Vector3f getRotation() { return rotation; }

	public Matrix4f getProjectionMatrix(){ return projectionMatrix; }

	public Matrix4f getViewMatrix(){ return viewMatrix; }

	public Matrix4f getProjectionViewMatrix(){ return projectionViewMatrix; }

	public float getSpeed() { return speed; }

}