package com.thibsworkshop.voxand.loaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.thibsworkshop.voxand.terrain.Block;
import com.thibsworkshop.voxand.toolbox.Utility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.joml.Vector3f;

public class JsonLoader {

	public static Block[] loadBlocks(String path) {
		String content = "";
		Block[] blocks = new Block[Block.MAX_BLOCK];
		try {
			content = Utility.readFile(path, StandardCharsets.US_ASCII);
		} catch (IOException e) {
			System.err.println("Couldn't find file " + path);
			e.printStackTrace();
			return null;
		}
		
		JSONArray jsonArray = new JSONArray(content);
		
		for(int i = 0; i< jsonArray.length(); i++) {
			JSONObject block = (JSONObject) jsonArray.get(i);
			JSONArray colors = block.getJSONArray("color");
			Vector3f color = new Vector3f(
					(float)colors.getDouble(0),
					(float)colors.getDouble(1),
					(float)colors.getDouble(2)
					);
					
			blocks[i] = new Block((byte)block.getInt("id"),
					color,
					(float)colors.getDouble(3),
					block.getFloat("shineDamper"),
					block.getFloat("reflectivity"),
					block.getBoolean("solid"));
		}
		return blocks;

	}
	
	
}
