package com.thibsworkshop.voxand.models;

public class RawModel {

	private int vaoID;
	private int iboID;
	private int vertexCount;
	
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
