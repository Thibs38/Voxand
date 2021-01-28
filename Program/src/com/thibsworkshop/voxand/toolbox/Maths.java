package com.thibsworkshop.voxand.toolbox;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Transform;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Vector;

public class Maths {

	public static final Vector3f right = new Vector3f(1, 0, 0);
	public static final Vector3f up = new Vector3f(0, 1, 0);
	public static final Vector3f forward = new Vector3f(0, 0, 1);
	public static final Vector3f zero = new Vector3f(0,0,0);

	public static Matrix4f createTransformationMatrix(Transform transform) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(transform.getPosition());
		Vector3f rotation = transform.getRotation();

		matrix.rotate((float) Math.toRadians(rotation.x), right);
		matrix.rotate((float) Math.toRadians(rotation.y), up);
		matrix.rotate((float) Math.toRadians(rotation.z), forward);

		matrix.scale(transform.getScale());
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		Vector3f rotation = camera.transform.getRotation();

		viewMatrix.rotate((float) Math.toRadians(rotation.x), right);
		viewMatrix.rotate((float) Math.toRadians(rotation.y), up);
		viewMatrix.rotate((float) Math.toRadians(rotation.z), forward);

		Vector3f cameraPos = camera.transform.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		viewMatrix.translate(negativeCameraPos);
		return viewMatrix;
	}


	public static double floatMod(double x, double y){
		// x mod y behaving the same way as Math.floorMod but with doubles
		return (x - Math.floor(x/y) * y);
	}

	public static float sign(float a){
		return a >= 0 ? 1 : -1;
	}

	public static boolean compareDist(Vector2f a, Vector2f b, float d) {
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) <= d * d;
	}

	public static float sqrDistance(Vector2f a, Vector2f b) {
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
	}

	public static int sqrDistance(Vector2i a, Vector2i b) {
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
	}

	public static float sigmoid(float x, float a) {
		return (float) (1f / (1 + Math.exp(-a * x)));
	}

}