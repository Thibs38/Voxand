package com.thibsworkshop.voxand.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.thibsworkshop.voxand.rendering.models.RawModel;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class OBJLoader {

	public static RawModel loadObjModel(String fileName) {
		FileReader fr = null;
		try {
			fr = new FileReader(new File("Program/res/models/" + fileName + ".obj"));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't load file!");
			e.printStackTrace();
			return null;
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> rawvertices = new ArrayList<>();
		List<Vector2f> rawtextures = new ArrayList<>();
		List<Vector3f> rawnormals = new ArrayList<>();
		List<Integer> rawindices = new ArrayList<>();
		
		float[] verticesArray = null;
		float[] normalsArray = null;
		float[] textureArray = null;
		int[] indicesArray = null;
		
		try {

			while (true) {
				line = reader.readLine();
				if(line == null)
					break;
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					rawvertices.add(vertex);
				} else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]));
					rawtextures.add(texture);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					rawnormals.add(normal);
				} else if (line.startsWith("f ")) {
					String[] vertex1 = currentLine[1].split("/");
					String[] vertex2 = currentLine[2].split("/");
					String[] vertex3 = currentLine[3].split("/");
	
					rawindices.add(Integer.parseInt(vertex1[0]));
					rawindices.add(Integer.parseInt(vertex1[1]));
					rawindices.add(Integer.parseInt(vertex1[2]));
					
					rawindices.add(Integer.parseInt(vertex2[0]));
					rawindices.add(Integer.parseInt(vertex2[1]));
					rawindices.add(Integer.parseInt(vertex2[2]));

					rawindices.add(Integer.parseInt(vertex3[0]));
					rawindices.add(Integer.parseInt(vertex3[1]));
					rawindices.add(Integer.parseInt(vertex3[2]));

				}
			}

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		verticesArray = new float[rawindices.size()];
		int vertI = 0;
		indicesArray = new int[rawindices.size()/3];
		int indiceI = 0;
		textureArray = new float[(rawindices.size()/3) * 2];
		int texI = 0;
		normalsArray = new float[rawindices.size()]; // * 3 / 3
		int normI = 0;
		
		for(int i = 0; i < rawindices.size();) {

			Vector3f coord = rawvertices.get(rawindices.get(i++)-1);
			Vector2f tex = rawtextures.get(rawindices.get(i++)-1);
			Vector3f norm = rawnormals.get(rawindices.get(i++)-1);
			
			indicesArray[indiceI] = indiceI;
			indiceI++;
			
			verticesArray[vertI++] = coord.x;
			verticesArray[vertI++] = coord.y;
			verticesArray[vertI++] = coord.z;
			
			textureArray[texI++] = tex.x;
			textureArray[texI++] = tex.y;

			normalsArray[normI++] = norm.x;
			normalsArray[normI++] = norm.y;
			normalsArray[normI++] = norm.z;

		}

		
		return Loader.loadToVAO(verticesArray, indicesArray, textureArray,normalsArray);

	}


}