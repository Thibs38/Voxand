package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.models.TexturedModel;
import com.thibsworkshop.voxand.physics.Collider;
import com.thibsworkshop.voxand.physics.Rigidbody;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;


public class Player extends Entity {

	private final float speed = 10;
	private final float rotationSpeed = 20f;
	private final float jumpSpeed = 20f;

	private Camera camera;

	public static Player player;

	enum Mode { Survival, Spectator};

	private Mode mode = Mode.Survival;
	
	public Player(TexturedModel texturedModel, float mass, Camera camera) {
		super(texturedModel, new Transform(), mass);
		player = this;
		this.camera = camera;
		camera.transform.setPosition(transform.getPosition());
		camera.transform.translate(0,10,0);
		render = false;
		enabled = true;
	}

	@Override
	public void update() {
		move();
	}

	Vector3f xVelocity = new Vector3f(0);
	Vector3f zVelocity = new Vector3f(0);
	public boolean move() {
		float realSpeed = speed; //TODO: speed not calculated correctly
		float realRotationSpeedx = rotationSpeed * Input.getAcceleration().x * Time.getDeltaTime();
		float realRotationSpeedy = rotationSpeed * Input.getAcceleration().y * Time.getDeltaTime();
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

		transform.setRotation(0, camRot.y, 0);
		//camera.transform.setRotation(camRot);

		Vector3f rot = transform.getRotation();


		//Applying Inputs
		switch (mode) { //TODO: can be optimized using matrices
			case Survival -> {
				zVelocity.set(transform.forward());
				zVelocity.mul(Input.getAxis(Input.AxisName.Vertical) * realSpeed);

				xVelocity.set(transform.right());
				xVelocity.mul(Input.getAxis(Input.AxisName.Horizontal) * realSpeed);
				/*if (Input.isKeyHold(GLFW.GLFW_KEY_W)) {
					dx += Math.cos(Math.toRadians(rot.y - 90)) * realSpeed;
					dz += Math.sin(Math.toRadians(rot.y - 90)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_S)) {
					dx += Math.cos(Math.toRadians(rot.y + 90)) * realSpeed;
					dz += Math.sin(Math.toRadians(rot.y + 90)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_A)) {
					dx -= Math.cos(Math.toRadians(rot.y)) * realSpeed;
					dz -= Math.sin(Math.toRadians(rot.y)) * realSpeed;
					moved = true;
				}
				if (Input.isKeyHold(GLFW.GLFW_KEY_D)) {
					dx += Math.cos(Math.toRadians(rot.y)) * realSpeed;
					dz += Math.sin(Math.toRadians(rot.y)) * realSpeed;
					moved = true;
				}*/
				if(Input.isKeyHold(GLFW.GLFW_KEY_SPACE) && rigidbody.grounded){
					dy += jumpSpeed;
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
		Vector3f currentVelocity = rigidbody.velocity;
		currentVelocity.x = dx;
		currentVelocity.z = dz;
		currentVelocity.y += dy;
		rigidbody.addVelocity(xVelocity);
		rigidbody.addVelocity(zVelocity);
		xVelocity.set(0);
		zVelocity.set(0);
		super.update(); //We update the entity, which will trigger physics calculation and calculate the final pos
		camera.transform.setPosition(transform.getPosition());//We apply the final translation to the camera
		camera.transform.translate(0,1.5f,0);
		camera.updateMatrices();

		return moved;
	}

	
}
