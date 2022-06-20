package com.thibsworkshop.voxand.data;

import com.thibsworkshop.voxand.entities.GameEntity;
import com.thibsworkshop.voxand.toolbox.MinMax;

public abstract class Biome {

    public static Biome[] biomes;

    public int id;
    public String name;
    public MinMax temperature;
    public MinMax humidity;
    public Entity[] entities;



    public Biome(int id, String name, MinMax temperature,
                 MinMax humidity) {
        this.id = id;
        this.name = name;
        this.temperature = temperature;
        this.humidity = humidity;
    }



    public abstract float generate_xz(long x, long z);

    public abstract float generate_xyz(long x, long y, long z);

}
