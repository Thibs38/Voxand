package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.physics.collisions.CollisionEngine;
import com.thibsworkshop.voxand.physics.collisions.Ray;
import com.thibsworkshop.voxand.physics.collisions.RayHit;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector3f;


public class Player extends GameEntity {

	private final float speed = 2;
	private final float rotationSpeed = 0.25f;
	private final float jumpSpeed = 20f;

	public final Camera camera;

	public static Player player;

	public enum Mode { Survival, Spectator}

	public Mode mode = Mode.Survival;

	private Ray ray;


	public Player(TexturedModel texturedModel, float mass, Camera camera) {
		super(texturedModel, mass);
		player = this;
		this.camera = camera;
		setCamera();
		doRendering = false;
		doUpdate = true; //Player is disabled because it shouldn't be updated through the gameobject manager
		if(mode == Mode.Spectator) {
			doTerrainCollisions = false;
			rigidbody.verticalDrag = rigidbody.horizontalDrag;
		}
		ray = new Ray(new Vector3f(camera.transform.getPosition()), new Vector2i(transform.chunkPos),new Vector3f(camera.transform.forward()),5);
	}

	private final Vector3f xVelocity = new Vector3f(0);
	private final Vector3f zVelocity = new Vector3f(0);
	public void move() {
		float realSpeed = speed;
		float realRotationSpeedx = rotationSpeed /* * Input.getAcceleration().x * Time.getDeltaTime()*/;
		float realRotationSpeedy = rotationSpeed /* * Input.getAcceleration().y * Time.getDeltaTime()*/;
		float dy = 0;

		boolean moved = false;
		Vector3f camRot = camera.transform.getRotation();

		//Applying mouse rotation to the camera
		if(Input.hasMouseMoved()){
			camRot.y += Input.getMouseDelta().x * realRotationSpeedx /*Time.getDeltaTime()*/; // Do not multiply by dtime because
			camRot.x -= Input.getMouseDelta().y * realRotationSpeedy /*Time.getDeltaTime()*/; // mouseDelta is already proportional to time
			moved = true;
		}

		if(camRot.x > 90)
			camRot.x = 90;
		else if (camRot.x < -90)
			camRot.x = -90;

		camRot.y = camRot.y % 360;

		//camera.transform.setRotation(camRot);


		//Applying Inputs
		switch (mode) { //OPTIMIZE use matrices
			case Survival -> {
				transform.setRotation(0, camRot.y, 0);

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
				ray.position.set(camera.transform.getPosition());
				ray.direction.set(camera.transform.forward());
				ray.chunkPosition.set(camera.transform.chunkPos);
			}
			case Spectator -> {
				zVelocity.set(camera.transform.forward());
				zVelocity.mul(Input.getAxis(Input.AxisName.Vertical)*realSpeed* Time.getDeltaTime()*32);

				xVelocity.set(camera.transform.right());
				xVelocity.mul(Input.getAxis(Input.AxisName.Horizontal) * realSpeed * Time.getDeltaTime()*32);

				camera.transform.translate(xVelocity);
				camera.transform.translate(zVelocity);
			}
		}

		//System.out.println(camera.chunkPosition);

		RayHit hit = CollisionEngine.rayVSterrain(ray);
		if(hit.success){
			//System.out.println("Block hit: " + hit.blockPosition + " distance: " + hit.distance);
		}
	}

	public void setCamera(){
		camera.transform.setPosition(transform.getPosition());
		camera.transform.chunkPos.set(transform.chunkPos);
		camera.transform.translate(0,10,0);
		camera.transform.setRotation(transform.getRotation());
	}
}
