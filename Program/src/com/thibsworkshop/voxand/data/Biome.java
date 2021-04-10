package com.thibsworkshop.voxand.data;

import com.thibsworkshop.voxand.entities.GameEntity;

public class Biome {
    public int id;
    public String name;
    public String luaFile;
    public int minTemperature;
    public int maxTemperature;
    public int minHumidity;
    public int maxHumidity;
    public Entity[] entities;

}
