package com.thibsworkshop.voxand.io;

import com.scs.voxlib.*;
import com.thibsworkshop.voxand.data.Block;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import org.joml.Vector2i;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Binary {
    private static final int BUFFER_SIZE = 4096; // 4KB

    public static void readVoxFile(String inputFile){
        ArrayList<Byte> blocks = new ArrayList<Byte>();
        try (VoxReader reader = new VoxReader(new FileInputStream(inputFile))) {
            VoxFile voxFile = reader.read();

            for (VoxModelInstance model_instance : voxFile.getModelInstances()) {
                GridPoint3 world_Offset = model_instance.worldOffset;
                VoxModelBlueprint model = model_instance.model;
                for (Voxel voxel : model.getVoxels()) {
                    int x = voxel.getPosition().x;
                    int y = voxel.getPosition().y;
                    int z = voxel.getPosition().z +200;
                    if(x < 0 || y < 0 || z < 0){
                        //System.out.println("Voxel out of bounds: " + x + " " + y + " " + z + "block: " + voxel.getColourIndex());
                        continue;
                    }
                    byte block = 0;

                    switch(voxel.getColourIndex()){
                        case (byte)228: // Grass
                            block = 1;
                            break;
                        case (byte)137: // Dirt
                            block = 2;
                            break;
                        case (byte)138: // Wood
                            block = 4;
                            break;
                        case (byte)186: // Leaves
                            block = 5;
                            break;
                    }
                    TerrainManager.getChunk(new Vector2i(0,0)).grid[x][z][y] = block;
                    // Do stuff with the data
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        TerrainManager.getChunk(new Vector2i(0,0)).dirty =true;

    }
    public static void write(String outputFile, byte[] bytes){
        Path path;
        try{
            path = Paths.get(outputFile);

        }catch(InvalidPathException e){
            System.err.println(e.getMessage());
            return;
        }

        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
