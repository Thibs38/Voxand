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

	public Transform transform;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f projectionViewMatrix;

	public FrustumIntersection frustumIntersection;

	private float FOV = (float)Math.toRadians(70);
	private float NEAR_PLANE = 0.01f;
	private float FAR_PLANE = 1000;
	private float ASPECT_RATIO;

	public static Camera main;

	public Vector2i currentChunk = new Vector2i(0,0);

	public Camera() {
		transform = new Transform();

		ASPECT_RATIO = Window.mainWindow.getAspectRatio();
		projectionMatrix = new Matrix4f().setPerspective(FOV,ASPECT_RATIO,NEAR_PLANE,FAR_PLANE);
		viewMatrix = Maths.createViewMatrix(this);
		projectionViewMatrix = new Matrix4f();
		projectionMatrix.mul(viewMatrix,projectionViewMatrix);

		frustumIntersection = new FrustumIntersection(projectionViewMatrix);
	}

	public void updateMatrices(){
		viewMatrix = Maths.createViewMatrix(this);
		projectionMatrix.mul(viewMatrix,projectionViewMatrix);
		frustumIntersection.set(projectionViewMatrix);
	}


	public Matrix4f getProjectionMatrix(){ return projectionMatrix; }

	public Matrix4f getViewMatrix(){ return viewMatrix; }

	public Matrix4f getProjectionViewMatrix(){ return projectionViewMatrix; }

}
