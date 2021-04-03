package com.thibsworkshop.voxand.rendering.models;

public class RawModel {

	private final int vaoID;
	private final int iboID;
	private final int vertexCount;
	
	public RawModel(int vaoID, int iboID, int vertexCount) {
		this.vaoID = vaoID;
		this.iboID = iboID;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaoID;
	}
	public int getIboID() {
		return iboID;
	}


	public int getVertexCount() {
		return vertexCount;
	}
	
}
