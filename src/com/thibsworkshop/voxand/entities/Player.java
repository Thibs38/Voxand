package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.models.Model;
import org.lwjgl.glfw.GLFW;
import org.joml.Vector3f;


public class Player extends Entity {

	private final float speed = 20f;
	
	public Player(Model model, Collider collider) {
		super(model, new Vector3f(0,0,0),collider);
		// TODO Auto-generated constructor stub
	}
	public Player(Model model, Vector3f position,float scale,Collider collider) {
		super(model, position,scale,collider);
		// TODO Auto-generated constructor stub
	}
	
	public void move() {
		float realSpeed = speed * Time.getDeltaTime();
		float dx = 0;
		float dy = 0;
		float dz = 0;
		
		if(Input.isKeyHold(GLFW.GLFW_KEY_Z)) {
			dx += Math.cos(Math.toRadians(super.rotation.y - 90))*realSpeed;
			dz += Math.sin(Math.toRadians(super.rotation.y - 90))*realSpeed;
		}
		if(Input.isKeyHold(GLFW.GLFW_KEY_S)) {
			dx += Math.cos(Math.toRadians(super.rotation.y + 90))*realSpeed;
			dz += Math.sin(Math.toRadians(super.rotation.y + 90))*realSpeed;
		}
		if(Input.isKeyHold(GLFW.GLFW_KEY_Q)) {
			dx -= Math.cos(Math.toRadians(super.rotation.y))*realSpeed;
			dz -= Math.sin(Math.toRadians(super.rotation.y))*realSpeed;
		}
		if(Input.isKeyHold(GLFW.GLFW_KEY_D)) {
			dx += Math.cos(Math.toRadians(super.rotation.y))*realSpeed;
			dz += Math.sin(Math.toRadians(super.rotation.y))*realSpeed;
		}
		if(Input.isKeyHold(GLFW.GLFW_KEY_SPACE)) {
			dy+= realSpeed;
		}
		if(Input.isKeyHold(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			dy-= realSpeed;
		}
		
		super.translate(dx,dy,dz);
		//collider.detectCollision();


	}

	
}
