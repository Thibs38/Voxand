package com.thibsworkshop.voxand.loaders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.thibsworkshop.voxand.data.Biome;
import com.thibsworkshop.voxand.data.Block;
import com.thibsworkshop.voxand.toolbox.MinMax;
import com.thibsworkshop.voxand.toolbox.Utility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.joml.Vector3f;

public class JsonLoader {

	private static final String BLOCK_PATH = "Program/res/data/blocks.json";
	private static final String BIOME_PATH = "Program/res/data/terrain_generation/biomes.json";

	/**
	 * Reads all the Json files and load data into handler classes
	 */
	public static void init(){
		Block.blocks = loadBlocks(BLOCK_PATH);
		//Biome.biomes = loadBiomes(BIOME_PATH);
	}

	/**
	 * Loads all the blocks specified in the Json file
	 * @param path The path to the Json file
	 * @return An array of all the blocks
	 */
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

	/**
	 * Loads all the biomes specified in the Json file
	 * @param path The path to the Json file
	 * @return An array of all the biomes
	 */
	public static Biome[] loadBiomes(String path) {
		String content = "";
		ArrayList<Biome> biomes = new ArrayList<>();

		try {
			content = Utility.readFile(path, StandardCharsets.US_ASCII);
		} catch (IOException e) {
			System.err.println("Couldn't find file " + path);
			e.printStackTrace();
			return null;
		}

		JSONArray jsonArray = new JSONArray(content);

		for(int i = 0; i< jsonArray.length(); i++) {
			JSONObject biome = (JSONObject) jsonArray.get(i);
			JSONObject temperature = biome.getJSONObject("temperature");
			JSONObject humidity = biome.getJSONObject("humidity");

			biomes.add(new Biome(
					(byte)biome.getInt("id"),
					biome.getString("name"),
					biome.getString("luafile"),
					new MinMax(temperature.getInt("min"), temperature.getInt("max")),
					new MinMax(humidity.getInt("min"), humidity.getInt("max"))
			)); //TODO: add entity
		}
		return (Biome[]) biomes.toArray();

	}

	
}
