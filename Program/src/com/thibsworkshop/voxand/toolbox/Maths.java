package com.thibsworkshop.voxand.toolbox;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Transform;
import org.joml.*;
import org.lwjgl.system.CallbackI;

import java.lang.Math;
import java.util.Vector;

public class Maths {

	public static final Vector3f right = new Vector3f(1, 0, 0);
	public static final Vector3f up = new Vector3f(0, 1, 0);
	public static final Vector3f forward = new Vector3f(0, 0, 1);
	public static final Vector3f zero = new Vector3f(0);
	public static final Vector3f one = new Vector3f(1);
	public static final Vector3f half = new Vector3f(0.5f);
	public static final Vector3f quarter = new Vector3f(0.25f);

	public static final Matrix4f identity = new Matrix4f().identity();

	public static final float EPSILON = 0.0001f;

	/**
	 * Updates the transformation matrix of the given transform
	 * @param transform the transform to update
	 */
	public static void updateTransformationMatrix(Transform transform) {
		Matrix4f matrix = transform.getTransformationMatrix().identity();
		matrix.translate(transform.getPosition());
		Vector3f rotation = transform.getRotation();

		matrix.rotate((float) Math.toRadians(rotation.x), right);
		matrix.rotate((float) Math.toRadians(rotation.y), up);
		matrix.rotate((float) Math.toRadians(rotation.z), forward);

		matrix.scale(transform.getScale());
	}

	/**
	 * Updates the view matrices of the given camera
	 * @param camera the camera to update
	 */
	public static void updateViewMatrix(Camera camera) {
		Matrix4f viewMatrix = camera.getViewMatrix();
		viewMatrix.identity();
		Vector3f rotation = camera.transform.getRotation();

		viewMatrix.rotate((float) Math.toRadians(rotation.x), right);
		viewMatrix.rotate((float) Math.toRadians(rotation.y), up);
		viewMatrix.rotate((float) Math.toRadians(rotation.z), forward);

		Vector3f cameraPos = camera.transform.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		viewMatrix.translate(negativeCameraPos);
	}

	/**
	 * Multiply m with v with w = 1
	 * @param m right
	 * @param v left
	 * @return m * v
	 */
	public static Vector4f mul(Matrix4f m, Vector3f v){
		return new Vector4f(v.x,v.y,v.z,1).mul(m);
	}

	/**
	 * Converts a Vector4f to a Vector3f by removing the w
	 * @param v4 vector to convert
	 * @return v4.xyz
	 */
	public static Vector3f vector4fToVector3f(Vector4f v4) {
		return new Vector3f(v4.x, v4.y, v4.z);
	}

	/**
	 * x mod y behaving the same way as Math.floorMod but with doubles
	 * @param x double
	 * @param y != 0
	 * @return x mod y
	 */
	public static double floatMod(double x, double y){
		return (x - Math.floor(x/y) * y);
	}

	/**
	 * Calculates the sign of a
	 * @param a float
	 * @return 1 if a > 0, -1 otherwise
	 */
	public static float sign(float a){
		return a >= 0 ? 1 : -1;
	}

	/**
	 * Compare the distance between a and b with d and returns true if dist(a,b) <= d
	 * @param a first point
	 * @param b second point
	 * @param d distance to compare with
	 * @return true if dist(a,b) <= d
	 */
	public static boolean compareDist(Vector2f a, Vector2f b, float d) {
		return sqrDistance(a, b) <= d * d;
	}

	/**
	 * Calculates the squared distance between a and b
	 * @param a first point
	 * @param b second point
	 * @return squared distance between a and b
	 */
	public static float sqrDistance(Vector2f a, Vector2f b) {
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
	}

	/**
	 * Calculates the squared distance between a and b
	 * @param a first point
	 * @param b second point
	 * @return squared distance between a and b
	 */
	public static int sqrDistance(Vector2i a, Vector2i b) {
		return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y);
	}

	/**
	 * Returns the largest int less than a
	 * @param a the number to floor
	 * @return largest int less than a
	 */
	public static int floor(float a){
		return (int) Math.floor(a);
	}

	/**
	 * Returns the smallest int greater than a
	 * @param a the number to ceil
	 * @return smallest int greater than a
	 */
	public static int ceil(float a) { return (int) Math.ceil(a); }

	/**
	 * Calculates the linear interpolation between a and b at time t
	 * @param a min bound
	 * @param b max bound
	 * @param t time
	 * @return the linear interpolation between a and b at time t
	 */
	public static float lerp(float a, float b, float t){
		return a + (b - a) * t;
	}

	/**
	 * Calculates the sigmoid of speed a at time t
	 * @param x time
	 * @param a stiffness
	 * @return value from 0 to 1
	 */
	public static float sigmoid(float x, float a) {
		return (float) (1f / (1 + Math.exp(-a * x)));
	}

}