package com.thibsworkshop.voxand.game;


//TODO: configuration loading and saving from json
public class Config {

    public static int chunkViewDist = 12;
    public static int sqr_chunkViewDist = chunkViewDist * chunkViewDist;

    public static int chunkLoadDist = chunkViewDist + 1;
    public static int sqr_chunkLoadDist = chunkLoadDist * chunkLoadDist;

    public static int chunkGenDist = chunkViewDist + 3;
    public static int sqr_chunkGenDist = chunkGenDist * chunkGenDist;

    public static int chunkUnloadDist = chunkViewDist + 6;
    public static int sqr_chunkUnloadDist = chunkUnloadDist * chunkUnloadDist;

    public static int entityViewDist = chunkViewDist;
    public static int sqr_entityViewDist = entityViewDist * entityViewDist;

    public static int tileEntityViewDist = chunkViewDist;
    public static int sqr_tileEntityViewDist = tileEntityViewDist * tileEntityViewDist;

}
