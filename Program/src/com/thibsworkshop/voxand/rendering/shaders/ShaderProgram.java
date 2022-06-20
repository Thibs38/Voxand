package com.thibsworkshop.voxand.rendering.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector3f;


public abstract class ShaderProgram {
	
	private final int programID;
	private final int vertexShaderID;
	private final int fragmentShaderID;

	private final int attributeCount;

	private static final String path = "Program/res/shaders/";

	private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public ShaderProgram(String vertexFile, String fragmentFile, int attributeCount) {
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		this.attributeCount = attributeCount;
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void getAllUniformLocations();
	
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	public void start() {
		GL20.glUseProgram(programID);
	}
	
	public void stop() {
		GL20.glUseProgram(0);
	}
	
	public void cleanUp(){
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute,String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	//<editor-fold desc="Loaders">
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadVector(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x,vector.y);
	}
	
	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x,vector.y,vector.z);
	}
	
	protected void loadVector(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x,vector.y,vector.z, vector.w);
	}
	
	protected void loadBoolean(int location, boolean bool) {
		GL20.glUniform1i(location, bool?1:0);
	}
	
	protected void loadInteger(int location, int value) {
		GL20.glUniform1i(location, value);
	}
	
	protected void loadMatrix(int location, Matrix4f matrix) {
		GL20.glUniformMatrix4fv(location, false, matrix.get(matrixBuffer));
		matrixBuffer.clear();
	}
	//</editor-fold>

	public int getAttributeCount(){ return attributeCount; }
	
	private int loadShader(String file,int type) {
		
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(path + file));
			String line;
			while((line = reader.readLine())!=null){

				shaderSource.append(line).append("//\n");
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile " + file + " !");
			System.exit(-1);
		}
		return shaderID;
	}
}
