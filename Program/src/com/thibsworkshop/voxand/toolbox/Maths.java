package com.thibsworkshop.voxand.toolbox;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Transform;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class Maths {

	public static final Vector3f right = new Vector3f(1, 0, 0);
	public static final Vector3f up = new Vector3f(0, 1, 0);
	public static final Vector3f forward = new Vector3f(0, 0, 1);

	public static Matrix4f createTransformationMatrix(Transform transform) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(transform.position);

		matrix.rotate((float) Math.toRadians(transform.rotation.x), right);
		matrix.rotate((float) Math.toRadians(transform.rotation.y), up);
		matrix.rotate((float) Math.toRadians(transform.rotation.z), forward);

		matrix.scale(transform.scale);
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();

		viewMatrix.rotate((float) Math.toRadians(camera.transform.rotation.x), right);
		viewMatrix.rotate((float) Math.toRadians(camera.transform.rotation.y), up);
		viewMatrix.rotate((float) Math.toRadians(camera.transform.rotation.z), forward);

		Vector3f cameraPos = camera.transform.position;
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		viewMatrix.translate(negativeCameraPos);
		return viewMatrix;
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