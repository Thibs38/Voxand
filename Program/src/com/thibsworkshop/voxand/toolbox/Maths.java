package com.thibsworkshop.voxand.toolbox;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.entities.Transform;
import org.joml.*;

import java.lang.Math;

public class Maths {


	public static final float EPSILON = 0.0001f;

	public static final Vector3f right = new Vector3f(1, 0, 0);
	public static final Vector3f left = new Vector3f(-1,0,0);
	public static final Vector3f up = new Vector3f(0, 1, 0);
	public static final Vector3f down = new Vector3f(0,-1,0);
	public static final Vector3f forward = new Vector3f(0, 0, 1);
	public static final Vector3f backward = new Vector3f(0,0,-1);
	public static final Vector3f zero = new Vector3f(0);
	public static final Vector3f moreThanZero = new Vector3f(0.001f);
	public static final Vector3f lessThanZero = new Vector3f(-0.001f);
	public static final Vector3f one = new Vector3f(1);
	public static final Vector3f lessThanOne = new Vector3f(0.999f);
	public static final Vector3f moreThanOne = new Vector3f(1.001f);

	public static final Vector3f half = new Vector3f(0.5f);
	public static final Vector3f quarter = new Vector3f(0.25f);

	public static final Matrix4f identity = new Matrix4f().identity();


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

	public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f scale) {
		Matrix4f matrix = new Matrix4f().identity();
		matrix.translate(position);

		matrix.scale(scale);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f position, Vector3f scale, Vector3f rotationAxis, float angle) {
		Matrix4f matrix = new Matrix4f().identity();
		matrix.translate(position);

		matrix.rotate(angle, rotationAxis);
		matrix.scale(scale);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f position, float scale) {
		Matrix4f matrix = new Matrix4f().identity();
		matrix.translate(position);

		matrix.scale(scale);
		return matrix;
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
	public static double doubleMod(double x, double y){
		return (x - Math.floor(x/y) * y);
	}

	/**
	 * x mod y behaving the same way as Math.floorMod but with floats
	 * @param x float
	 * @param y != 0
	 * @return x mod y
	 */
	public static float floatMod(float x, float y){
		return (float)(x - Math.floor(x/y) * y);
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
		float x = a.x - b.x;
		float y = a.y - b.y;
		return x * x + y * y;
	}

	/**
	 * Calculates the squared distance between a and b
	 * @param a first point
	 * @param b second point
	 * @return squared distance between a and b
	 */
	public static float sqrDistance(Vector3f a, Vector3f b) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		float z = a.z - b.z;
		return x * x + y * y + z * z;
	}

	/**
	 * Calculates the squared distance between a and b on the xz plane
	 * @param a first point
	 * @param b second point
	 * @return squared distance between a and b
	 */
	public static float sqrDistance_xz(Vector3f a, Vector3f b) {
		float x = a.x - b.x;
		float z = a.z - b.z;
		return x * x + z * z;
	}

	/**
	 * Calculates the squared distance between a and b
	 * @param a first point
	 * @param b second point
	 * @return squared distance between a and b
	 */
	public static int sqrDistance(Vector2i a, Vector2i b) {
		int x = a.x - b.x;
		int y = a.y - b.y;
		return x * x + y * y;
	}

	/**
	 * Calculates the squared magnitude (length) of the given vector.
	 * @param v The vector
	 * @return The squared magnitude
	 */
	public static int sqrMagnitude(Vector2i v){
		return v.x * v.x + v.y * v.y;
	}

	/**
	 * Calculates the squared magnitude (length) of the given vector.
	 * @param x x of the vector
	 * @param y y of the vector
	 * @return The squared magnitude
	 */
	public static int sqrMagnitude(int x, int y){ return x*x + y*y; }

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
	 * Returns the maximum between a and b
	 * @param a
	 * @param b
	 * @return maximum between a and b
	 */
	public static float max(float a, float b){
		return (a >= b) ? a : b;
	}

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
	 * Calculates the sigmoid of speed a at a given time t
	 * @param t time
	 * @param a stiffness
	 * @return value from 0 to 1
	 */
	public static float sigmoid(float t, float a) {
		return (float) (1f / (1 + Math.exp(-a * t)));
	}

}