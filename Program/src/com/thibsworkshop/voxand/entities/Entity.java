package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.models.Model;
import org.joml.Vector3f;


public class Entity {
	
	protected Model model;
	public Vector3f position;
	public Vector3f rotation;
	public Vector3f scale;
	protected Collider collider;
	
	public boolean grounded;

	public Entity(Model model, Vector3f position, Vector3f rotation, Vector3f scale,Collider collider) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.collider = collider;
		if(collider != null)
			collider.LinkEntity(this);
	}
	
	public Entity(Model model, Vector3f position, Collider collider) {
		this.model = model;
		this.position = position;
		this.rotation = new Vector3f(0,0,0);
		this.scale = new Vector3f(1,1,1);
		this.collider = collider;
		if(collider != null)
			collider.LinkEntity(this);
	}
	public Entity(Model model, Vector3f position,float scale,Collider collider) {
		this.model = model;
		this.position = position;
		this.rotation = new Vector3f(0,0,0);
		this.scale = new Vector3f(scale,scale,scale);
		this.collider = collider;
		if(collider != null)
			collider.LinkEntity(this);
	}
	
	public void translate(Vector3f translation) {
		this.position.add(translation);
	}
	
	public void translate(float dx,float dy, float dz) {
		this.position.x += dx;
		this.position.y += dy;
		this.position.z += dz;
	}
	
	public void rotate(Vector3f rotation) {
		this.rotation.add(rotation);
	}
	
	public void rotate(float rx, float ry, float rz) {
		this.rotation.x += rx;
		this.rotation.y += ry;
		this.rotation.z += rz;
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	
}
