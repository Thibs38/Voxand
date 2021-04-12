package com.thibsworkshop.voxand.loaders;

import com.thibsworkshop.voxand.data.Biome;
import com.thibsworkshop.voxand.lua.LuaTest;
import com.thibsworkshop.voxand.terrain.GridGenerator;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaLoader {

    private static LuaValue globals;
    public static final String path = "Program/res/data/terrain_generation/";


    public static void init(){
        globals = JsePlatform.standardGlobals();
        loadBiomes();
    }

    /**
     * Loads all of the biomes' lua file
     */
    public static void loadBiomes(){

        LuaValue gridGenerator = CoerceJavaToLua.coerce(GridGenerator.class);
        globals.set("gen", gridGenerator); //Allows to call the GridGenerators' methods in the lua file


        globals.get("dofile").call(LuaValue.valueOf(path + "plains.lua"));
        Biome biome = Biome.biomes[0];
        biome.initLua(globals.get("generate_xz"), globals.get("generate_xyz"));
    }
}
