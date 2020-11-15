package com.thibsworkshop.voxand.toolbox;

import com.thibsworkshop.voxand.entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Maths {

	public static final Vector3f right = new Vector3f(1,0,0);
	public static final Vector3f up = new Vector3f(0,1,0);
	public static final Vector3f forward = new Vector3f(0,0,1);
	
	public static Matrix4f createTransformationMatrix(Vector3f translation,Vector3f rotation, Vector3f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(translation);
		
		matrix.rotate((float) Math.toRadians(rotation.x), right);
		matrix.rotate((float) Math.toRadians(rotation.y), up);
		matrix.rotate((float) Math.toRadians(rotation.z), forward);
		
		matrix.scale(scale);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation,Vector3f rotation, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(translation);
		
		matrix.rotate((float) Math.toRadians(rotation.x), right);
		matrix.rotate((float) Math.toRadians(rotation.y), up);
		matrix.rotate((float) Math.toRadians(rotation.z), forward);
		
		matrix.scale(new Vector3f(scale,scale,scale));
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(translation);

		return matrix;
	}
	
	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		
		viewMatrix.rotate((float) Math.toRadians(camera.getRotation().x), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(camera.getRotation().y), new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(camera.getRotation().z), new Vector3f(0, 0, 1));
		
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
		viewMatrix.translate(negativeCameraPos);
		return viewMatrix;
	}
	
	/*public static Vector4f[] frustumPlanes(Matrix4f mat)
	{   
		Vector4f[] v = new Vector4f[6];
		v[0] = new Vector4f(mat.m03 + mat.m00,mat.m13 + mat.m10,mat.m23 + mat.m20,mat.m33 + mat.m30); // left
		v[1] = new Vector4f(mat.m03 - mat.m00,mat.m13 - mat.m10,mat.m23 - mat.m20,mat.m33 - mat.m30); // right
		v[2] = new Vector4f(mat.m03 + mat.m01,mat.m13 + mat.m11,mat.m23 + mat.m21,mat.m33 + mat.m31); // bottom
		v[3] = new Vector4f(mat.m03 - mat.m01,mat.m13 - mat.m11,mat.m23 - mat.m21,mat.m33 - mat.m31); // top
		v[4] = new Vector4f(mat.m03 + mat.m02,mat.m13 + mat.m12,mat.m23 + mat.m22,mat.m33 + mat.m32); // near
		v[5] = new Vector4f(mat.m03 - mat.m02,mat.m13 - mat.m12,mat.m23 - mat.m22,mat.m33 - mat.m32); // far
		
		return v;
	}*/
	
	/*public static Plane[] frustumPlanes(Matrix4f mat, boolean normalize)
	{   
		Plane[] p = new Plane[6];
		p[0] = normalizePlane(mat.m30 + mat.m00, mat.m31 + mat.m01, mat.m32 + mat.m02, mat.m33 + mat.m03); // left
		p[1] = normalizePlane(mat.m30 - mat.m00, mat.m31 - mat.m01, mat.m32 - mat.m02, mat.m33 - mat.m03); // right
		p[2] = normalizePlane(mat.m30 - mat.m10, mat.m31 - mat.m11, mat.m32 - mat.m12, mat.m33 - mat.m13); // top
		p[3] = normalizePlane(mat.m30 + mat.m10, mat.m31 + mat.m11, mat.m32 + mat.m12, mat.m33 + mat.m13); // bottom
		p[4] = normalizePlane(mat.m30 + mat.m20, mat.m31 + mat.m21, mat.m32 + mat.m22, mat.m33 + mat.m23); // near
		p[5] = normalizePlane(mat.m30 - mat.m20, mat.m31 - mat.m21, mat.m32 - mat.m22, mat.m33 - mat.m23); // far
		
		return p;
	}*/
	
	public static Plane normalizePlane(float A, float B, float C, float D) {

	    float nf = 1.0f / (float)Math.sqrt(A * A + B * B + C * C);

	    return new Plane(new Vector3f(nf * A, nf * B, nf * C), nf * D);
	}
	
	
	public static boolean compareDist(Vector2f a, Vector2f b, float d) {
		return (a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y) <= d*d;
	}
	
	public static float sqrDistance(Vector2f a, Vector2f b) {
		return (a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y);
	}
	
	public static float sigmoid(float x, float a) {
		return (float) (1f/(1 + Math.exp(-a*x)));
	}
	
	public static class Plane {
		
		public final Vector3f normal;
		public final float offset;

		public Plane(Vector3f normal, float offset) {
			super();
			this.normal = normal;
			this.offset = offset;
		}
		
	}
	
		
	public static class Key {

	    public final int x;
	    public final int y;

	    public Key(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }

	    @Override
	    public boolean equals(Object o) {
	        if (this == o) return true;
	        if (!(o instanceof Key)) return false;
	        Key key = (Key) o;
	        return x == key.x && y == key.y;
	    }

	    @Override
	    public int hashCode() {
	        int result = x;
	        result = 31 * result + y;
	        return result;
	    }

	}
}
