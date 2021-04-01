package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;


public class Camera {

	public Transform transform;

	public Vector3f forward = new Vector3f();

	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f viewMatrix = new Matrix4f();
	private Matrix4f projectionViewMatrix = new Matrix4f();

	public FrustumIntersection frustumIntersection;

	private final float FOV = (float)Math.toRadians(70);
	private final float NEAR_PLANE = 0.01f;
	private final float FAR_PLANE = 1000;
	private final float ASPECT_RATIO;

	public static Camera main;

	public Vector2i currentChunk = new Vector2i(0,0);

	public Camera() {
		transform = new Transform();

		ASPECT_RATIO = Window.mainWindow.getAspectRatio();
		projectionMatrix = new Matrix4f().setPerspective(FOV,ASPECT_RATIO,NEAR_PLANE,FAR_PLANE);
		Maths.updateViewMatrix(this);
		projectionViewMatrix = new Matrix4f();
		projectionMatrix.mul(viewMatrix,projectionViewMatrix);

		frustumIntersection = new FrustumIntersection(projectionViewMatrix);
	}

	public void updateMatrices(){
		Maths.updateViewMatrix(this);
		projectionMatrix.mul(viewMatrix,projectionViewMatrix);
		frustumIntersection.set(projectionViewMatrix);
		viewMatrix.positiveZ(forward).negate();
	}

	public Matrix4f getProjectionMatrix(){ return projectionMatrix; }

	public Matrix4f getViewMatrix(){ return viewMatrix; }

	public Matrix4f getProjectionViewMatrix(){ return projectionViewMatrix; }

}
