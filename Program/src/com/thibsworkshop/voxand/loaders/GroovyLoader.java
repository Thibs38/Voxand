package com.thibsworkshop.voxand.loaders;

import com.thibsworkshop.voxand.data.Biome;
import com.thibsworkshop.voxand.terrain.GridGenerator;
import com.thibsworkshop.voxand.toolbox.Utility;
import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class GroovyLoader {

    private static final String TERRAIN_GEN_PATH = "Program/res/data/terrain_generation/";

    private static final GroovyClassLoader groovyClassLoader = new GroovyClassLoader();


    public static Biome[] loadBiomes(String[] scripts){
        Biome[] biomes = new Biome[scripts.length];
        for(int i = 0; i < scripts.length; i++){
            biomes[i] = loadScript(TERRAIN_GEN_PATH + scripts[i]);
        }
        return biomes;
    }

    public static <T> T loadScript(String scriptName) {

        try {
            String script = script = Utility.readFile(scriptName);
            return (T) groovyClassLoader.parseClass(script).getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
