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
	private final float rotationSpeed = 20f;

	private Camera camera;

	enum Mode { Survival, Spectator};

	private Mode mode = Mode.Spectator;
	
	public Player(TexturedModel texturedModel, float mass, Camera camera) {
		super(texturedModel, new Transform(), mass);
		this.camera = camera;
		camera.transform.setPosition(transform.getPosition());
		camera.transform.translate(0,3,0);
		render = false;
	}

	@Override
	public void update() {
		move();
	}

	public boolean move() {
		float realSpeed = speed * Time.getDeltaTime();
		float realRotationSpeedx = rotationSpeed * Time.getDeltaTime() * Input.getAcceleration().x;
		float realRotationSpeedy = rotationSpeed * Time.getDeltaTime() * Input.getAcceleration().y;
		float dx = 0;
		float dy = 0;
		float dz = 0;

		boolean moved = false;
		Vector3f camRot = camera.transform.getRotation();

		//Applying mouse rotation to the camera
		if(Input.hasMouseMoved()){
			camRot.y += Input.getMouseDelta().x * realRotationSpeedx;
			camRot.x -= Input.getMouseDelta().y * realRotationSpeedy;
			moved = true;
		}

		if(camRot.x > 90)
			camRot.x = 90;
		else if (camRot.x < -90)
			camRot.x = -90;

		camRot.y = camRot.y % 360;

		Vector3f rot = transform.getRotation();
		float rx = camRot.x - rot.x;
		float ry = camRot.y - rot.y;
		float rz = camRot.z - rot.z;
		transform.rotate(rx,ry,rz);

		//Applying Inputs
		switch (mode) {
			case Survival -> {
				if (Input.isKeyHold(GLFW.GLFW_KEY_Z)) {
					dx += Math.cos(Math.toRadians(rot.y - 90)) * realSpeed;
					dz += Math.sin(Math.toRadians(rot.y - 90)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_S)) {
					dx += Math.cos(Math.toRadians(rot.y + 90)) * realSpeed;
					dz += Math.sin(Math.toRadians(rot.y + 90)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_Q)) {
					dx -= Math.cos(Math.toRadians(rot.y)) * realSpeed;
					dz -= Math.sin(Math.toRadians(rot.y)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_D)) {
					dx += Math.cos(Math.toRadians(rot.y)) * realSpeed;
					dz += Math.sin(Math.toRadians(rot.y)) * realSpeed;
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
			rigidbody.addVelocity(dx,dy,dz); //We set the velocity based on the inputs
			super.update(); //We update the entity, which will trigger physics calculation and calculate the final pos
			camera.transform.setPosition(transform.getPosition());//We apply the final translation to the camera
			camera.updateMatrices();
		}
		return moved;
	}

	
}
