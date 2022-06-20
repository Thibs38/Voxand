package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;


public abstract class Camera {

	public Transform transform;

	public Vector3f forward = new Vector3f();

	protected final Matrix4f projectionMatrix;
	protected final Matrix4f viewMatrix = new Matrix4f();
	protected final Matrix4f projectionViewMatrix;

	public final float NEAR_PLANE = 0.01f;
	public final float FAR_PLANE = 1000;

	enum ProjectionType{
		PERSPECTIVE,
		ORTHOGRAPHIC
	}

	public ProjectionType projectionType;

	public static Camera main;


	public Camera() {
		this.transform = new Transform();
		this.projectionMatrix = new Matrix4f();
		this.projectionViewMatrix = new Matrix4f();
	}


	public abstract void updateViewMatrix();

	public abstract void updateProjectionMatrix();

	//<editor-fold desc="Getters">
	public Matrix4f getProjectionMatrix(){ return projectionMatrix; }

	public Matrix4f getViewMatrix(){ return viewMatrix; }

	public Matrix4f getProjectionViewMatrix(){ return projectionViewMatrix; }

	//</editor-fold>
}
