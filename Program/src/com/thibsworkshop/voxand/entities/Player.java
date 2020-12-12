package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.models.TexturedModel;
import com.thibsworkshop.voxand.physics.Collider;
import com.thibsworkshop.voxand.physics.Rigidbody;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector3f;


public class Player extends Entity {

	private final float speed = 20f;
	private final float rotationSpeed = 30f;

	private Camera camera;

	enum Mode { Survival, Spectator};

	private Mode mode = Mode.Spectator;
	
	public Player(TexturedModel texturedModel, Rigidbody rigidbody, Camera camera) {
		super(texturedModel, new Transform(), rigidbody);
		this.camera = camera;
		camera.transform.position = new Vector3f(transform.position);
		camera.transform.translate(0,3,0);
	}

	@Override
	public void update() { }

	public void move() {
		float realSpeed = speed * Time.getDeltaTime();
		float realRotationSpeed = rotationSpeed * Time.getDeltaTime();
		float dx = 0;
		float dy = 0;
		float dz = 0;

		boolean moved = false;
		Vector3f camRot = camera.transform.rotation;

		//Applying mouse rotation to the camera
		if(Input.hasMouseMoved()){
			camRot.y += Input.getMouseDelta().x * realRotationSpeed;
			camRot.x -= Input.getMouseDelta().y * realRotationSpeed;
			moved = true;
		}

		if(camRot.x > 90)
			camRot.x = 90;
		else if (camRot.x < -90)
			camRot.x = -90;

		camRot.y = camRot.y % 360;

		float rx = camRot.x - transform.rotation.x;
		float ry = camRot.y - transform.rotation.y;
		float rz = camRot.z - transform.rotation.z;
		transform.rotate(rx,ry,rz);

		//Applying Inputs
		switch (mode) {
			case Survival -> {
				if (Input.isKeyHold(GLFW.GLFW_KEY_Z)) {
					dx += Math.cos(Math.toRadians(transform.rotation.y - 90)) * realSpeed;
					dz += Math.sin(Math.toRadians(transform.rotation.y - 90)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_S)) {
					dx += Math.cos(Math.toRadians(transform.rotation.y + 90)) * realSpeed;
					dz += Math.sin(Math.toRadians(transform.rotation.y + 90)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_Q)) {
					dx -= Math.cos(Math.toRadians(transform.rotation.y)) * realSpeed;
					dz -= Math.sin(Math.toRadians(transform.rotation.y)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_D)) {
					dx += Math.cos(Math.toRadians(transform.rotation.y)) * realSpeed;
					dz += Math.sin(Math.toRadians(transform.rotation.y)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_SPACE)) {
					dy += realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_LEFT_SHIFT)) {
					dy -= realSpeed;
					moved = true;
				}
			}
			case Spectator -> {
				if (Input.isKeyHold(GLFW.GLFW_KEY_Z)) {
					dx += Math.sin(Math.toRadians(camRot.y)) * Math.cos(Math.toRadians(camRot.x)) * realSpeed;
					dz -= Math.cos(Math.toRadians(camRot.y)) * Math.cos(Math.toRadians(camRot.x)) * realSpeed;
					dy -= Math.sin(Math.toRadians(camRot.x)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_S)) {
					dx -= Math.sin(Math.toRadians(camRot.y)) * Math.cos(Math.toRadians(camRot.x)) * realSpeed;
					dz += Math.cos(Math.toRadians(camRot.y)) * Math.cos(Math.toRadians(camRot.x)) * realSpeed;
					dy += Math.sin(Math.toRadians(camRot.x)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_Q)) {
					dx -= Math.cos(Math.toRadians(camRot.y)) * realSpeed;
					dz -= Math.sin(Math.toRadians(camRot.y)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_D)) {
					dx += Math.cos(Math.toRadians(camRot.y)) * realSpeed;
					dz += Math.sin(Math.toRadians(camRot.y)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_SPACE)) {
					dy += realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_LEFT_SHIFT)) {
					dy -= realSpeed;
					moved = true;
				}
			}
		}
		

		if(moved){
			transform.translate(dx,dy,dz); //We move based on the inputs
			Vector3f correction = texturedModel.collider.detectCollision(transform.position); //We calculate the correction due to collisions
			transform.translate(correction);//We apply the correction
			correction.add(dx,dy,dz);//We calculate the final translation of the frame
			camera.transform.translate(correction);//We apply the final translation to the camera
			camera.updateMatrices();
			super.update();
		}
	}

	
}
