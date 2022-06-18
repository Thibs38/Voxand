package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector3f;


public class Player extends GameEntity {

	private final float speed = 2;
	private final float rotationSpeed = 20f;
	private final float jumpSpeed = 20f;

	private final Camera camera;

	public static Player player;

	public enum Mode { Survival, Spectator}

	public Mode mode = Mode.Spectator;
	
	public Player(TexturedModel texturedModel, float mass, Camera camera) {
		super(texturedModel, mass);
		player = this;
		this.camera = camera;
		camera.transform.setPosition(transform.getPosition());
		camera.transform.translate(0,10,0);
		doRendering = false;
		doUpdate = true; //Player is disabled because it shouldn't be updated through the gameobject manager
		if(mode == Mode.Spectator) {
			doTerrainCollisions = false;
			rigidbody.verticalDrag = rigidbody.horizontalDrag;
		}
	}

	Vector3f xVelocity = new Vector3f(0);
	Vector3f zVelocity = new Vector3f(0);
	public void move() {
		float realSpeed = speed;
		float realRotationSpeedx = rotationSpeed * Input.getAcceleration().x * Time.getDeltaTime();
		float realRotationSpeedy = rotationSpeed * Input.getAcceleration().y * Time.getDeltaTime();
		float dy = 0;

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


		//Applying Inputs
		switch (mode) { //OPTIMIZE: can be optimized using matrices
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
				zVelocity.set(camera.transform.forward());
				zVelocity.mul(Input.getAxis(Input.AxisName.Vertical)*realSpeed);

				xVelocity.set(transform.right());
				xVelocity.mul(Input.getAxis(Input.AxisName.Horizontal) * realSpeed);

				if(Input.isKeyHold(GLFW.GLFW_KEY_SPACE))
					dy += jumpSpeed/4f;
				if(Input.isKeyHold(GLFW.GLFW_KEY_LEFT_SHIFT))
					dy -= jumpSpeed/4f;
			}
		}
		Vector3f currentVelocity = rigidbody.velocity;
		currentVelocity.y += dy;
		rigidbody.addVelocity(xVelocity);
		rigidbody.addVelocity(zVelocity);
		if(mode == Mode.Spectator){
			//float rDrag = Math.max(1- rigidbody.drag*Time.getDeltaTime(),0);
			//currentVelocity.mul(rDrag,1,rDrag);
		}
		xVelocity.set(0);
		zVelocity.set(0);
	}

	
}
