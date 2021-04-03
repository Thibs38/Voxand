package com.thibsworkshop.voxand.loaders;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.terrain.Block;
import com.thibsworkshop.voxand.rendering.textures.Texture;
import com.thibsworkshop.voxand.toolbox.BufferTools;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

//TODO: Rearenge Loader class, maybe make subclasses and remove the static prefix

public class Loader {
	
	private static final List<Integer> vaos = new ArrayList<>();
	private static final List<Integer> vbos = new ArrayList<>();
	private static final List<Integer> textures = new ArrayList<>();

	/**
	 * Initializes loaders
	 */
	public static void init() {
		Block.blocks = JsonLoader.loadBlocks("Program/res/data/blocks");
	}
	
	public static RawModel loadToVAO(float[] positions, int[] indices, float[] textureCoords, float[] normals) {
		
		int vaoID = createVAO();
		int iboID = bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(vaoID, iboID, indices.length);
	}
	
	public static RawModel loadToVAOColor(float[] positions, int[] indices, byte[] blocks, byte[] normals) {
		int vaoID = createVAO();
		int iboID = bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,1, blocks);
		storeDataInAttributeList(2,1, normals);
		unbindVAO();
		return new RawModel(vaoID,iboID, indices.length);
	}

	public static RawModel loadToVAOLine(float[] positions, int[] indices){
		int vaoID = createVAO();
		int iboID = bindIndicesBuffer(indices);
		storeDataInAttributeList(0,3,positions);
		unbindVAO();
		return new RawModel(vaoID,iboID, indices.length);
	}
	
	public static Texture loadTexture(String fileName) {
		Texture texture = TextureLoader.loadTexture("PNG", "Program/res/textures/"+fileName+".png");
		if(texture == null) return null;
		textures.add(texture.getID());
		return texture;
	}
	
	public static void cleanUp() {
		for(int vao:vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		
		for(int vbo:vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		
		for(int tex:textures) {
			GL11.glDeleteTextures(tex);
		}
	}
	
	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();

		vaos.add(vaoID);

		GL30.glBindVertexArray(vaoID);

		return vaoID;
	}
	
	private static void storeDataInAttributeList(int attributeNumber, int coordsSize,float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vboID);
		FloatBuffer buffer = BufferTools.storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordsSize, GL11.GL_FLOAT, false, 0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private static void storeDataInAttributeList(int attributeNumber, int coordsSize,byte[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,vboID);
		ByteBuffer buffer = BufferTools.storeDataInByteBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attributeNumber, coordsSize, GL11.GL_BYTE, 0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}
	
	private static int bindIndicesBuffer(int[] indices) {
		int iboID = GL15.glGenBuffers();
		vbos.add(iboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboID);
		IntBuffer buffer = BufferTools.storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER,buffer,GL15.GL_STATIC_DRAW);
		return iboID;
		}
	
}
