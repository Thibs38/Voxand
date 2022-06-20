package com.thibsworkshop.voxand.loaders;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.thibsworkshop.voxand.data.Biome;
import com.thibsworkshop.voxand.data.Block;
import com.thibsworkshop.voxand.debugging.CompilingException;
import com.thibsworkshop.voxand.toolbox.MinMax;
import com.thibsworkshop.voxand.toolbox.Utility;
import org.json.JSONArray;
import org.json.JSONObject;
import org.joml.Vector3f;

import com.thibsworkshop.voxand.debugging.CompilingException.*;

public class JsonLoader {

	private static final String BLOCK_PATH = "Program/res/data/blocks.json";
	private static final String BIOME_PATH = "Program/res/data/terrain_generation/biomes.json";

	/**
	 * Loads all the blocks specified in the Json file
	 * @return An array of all the blocks
	 */
	static Block[] loadBlocks() {
		String content = "";
		Block[] blocks = new Block[Block.MAX_BLOCK];
		try {
			content = Utility.readFile(BLOCK_PATH, StandardCharsets.US_ASCII);
		} catch (IOException e) {
			System.err.println("Couldn't find file " + BLOCK_PATH);
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

	static String[] loadBiomes(){
		String content = "";
		List<String> scripts = new ArrayList<>();
		try {
			content = Utility.readFile(BIOME_PATH, StandardCharsets.US_ASCII);
		} catch (IOException e) {
			System.err.println("Couldn't find file " + BIOME_PATH);
			e.printStackTrace();
			return null;
		}

		JSONArray jsonArray = new JSONArray(content);

		for(int i = 0; i< jsonArray.length(); i++)
			scripts.add(jsonArray.getString(0));
		return (String[]) scripts.toArray();
	}

	/*public static Biome[] loadBiomes(String path) {
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

		for(int i = 0; i < 1 jsonArray.length; i++) {
			JSONObject biomeData = (JSONObject) jsonArray.get(i);


			String groovyPath = biomeData.getString("groovy");
			Class<Biome> biomeClass = null;
			Biome biome = null;
			try {
				biomeClass = GroovyLoader.loadBiome(TERRAIN_GEN_PATH + groovyPath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<Object> values = new ArrayList<>();
			Iterator<String> it = biomeData.keys();
			while(it.hasNext()){
				String name = it.next();
				Object obj = biomeData.get(name);
				String suffix = Utility.getClassName(obj.getClass());

				try{
					switch(suffix){
						case "JSONObject" -> {
							values.add(loadJSONObject((JSONObject) obj));
						}
						case "JSONArray" -> {
							values.add(loadJSONArray((JSONArray) obj));
						}
						default -> {
							values.add(obj);
						}
					}
				}catch (IllegalJSONObjectType illegalJSONObjectType) {
					illegalJSONObjectType.printStackTrace();
				}catch (IllegalJSONArrayType illegalJSONArrayType) {
					illegalJSONArrayType.printStackTrace();
				}
				System.out.println();
			}

			try {
				biome = biomeClass.getDeclaredConstructor().newInstance(values.toArray());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			biome.generate_xz()

		}
		return (Biome[]) biomes.toArray();
	}*/

	private static Object loadJSONObject(JSONObject jsonObject) throws IllegalJSONObjectType {
		String[] keys = (String[])jsonObject.keySet().toArray();
		switch(keys.length)
		{
			case 2 -> {
				if(keys[0].equals("min") && keys[1].equals("max")){
					return new MinMax(jsonObject.getInt("min"), jsonObject.getInt("max"));
				}
			}
			default -> throw new IllegalJSONObjectType("biomes.json: you have defined an Object of an unhandled type");
		}
		return null;
	}

	private static Object loadJSONArray(JSONArray jsonArray) throws IllegalJSONArrayType {
		if(jsonArray.length() == 0) return null;
		String type = Utility.getClassName(jsonArray.get(0).getClass());

		switch(type)
		{
			case "Integer", "Long", "Boolean", "String", "Float", "Double" -> {
				Object[] obj = new Object[jsonArray.length()];
				for(int i = 0; i < jsonArray.length(); i++){
					obj[i] = jsonArray.get(i);
				}
				return obj;
			}
			default -> {
				throw new IllegalJSONArrayType("biomes.json: you have defined an array of an unhandled type");
			}
		}
	}
}
