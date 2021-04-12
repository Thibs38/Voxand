package com.thibsworkshop.voxand.data;

import com.thibsworkshop.voxand.entities.GameEntity;
import com.thibsworkshop.voxand.toolbox.MinMax;
import org.luaj.vm2.LuaValue;

public class Biome {

    public static Biome[] biomes;

    public int id;
    public String name;
    public String luaFile;
    public MinMax temperature;
    public MinMax humidity;
    public Entity[] entities;

    private LuaValue generate_xz;
    private LuaValue generate_xyz;


    public Biome(int id, String name, String luaFile, MinMax temperature,
                 MinMax humidity) {
        this.id = id;
        this.name = name;
        this.luaFile = luaFile;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public void initLua(LuaValue generate_xz, LuaValue generate_xyz){
        this.generate_xyz = generate_xyz;
        this.generate_xz = generate_xz;
    }

    public float generate_xz(int x, int z){
        return generate_xz.call(LuaValue.valueOf(x), LuaValue.valueOf(z)).tofloat();
    }

    public byte generate_xyz(int x, int y, int z, int simplex){
        return generate_xyz.invoke(new LuaValue[]{
                LuaValue.valueOf(x),
                LuaValue.valueOf(y),
                LuaValue.valueOf(z),
                LuaValue.valueOf(simplex)}).arg1().tobyte();

    }

}
