package com.thibsworkshop.voxand.game;


//TODO: configuration loading and saving from json
public class Config {

    /**
     * Render distance
     */
    public static int chunkViewDist = 12;
    /**
     * Render distance squared
     */
    public static int sqr_chunkViewDist = chunkViewDist * chunkViewDist;

    /**
     * Model generation distance
     */
    public static int chunkLoadDist = chunkViewDist + 1;
    /**
     * Model generation distance squared
     */
    public static int sqr_chunkLoadDist = chunkLoadDist * chunkLoadDist;

    /**
     * Grid generation distance
     */
    public static int chunkGenDist = chunkViewDist + 2;
    /**
     * Grid generation distance squared
     */
    public static int sqr_chunkGenDist = chunkGenDist * chunkGenDist;

    /**
     * Chunk unloading and saving distance
     */
    public static int chunkUnloadDist = chunkViewDist + 6;
    /**
     * Chunk unloading and saving distance squared
     */
    public static int sqr_chunkUnloadDist = chunkUnloadDist * chunkUnloadDist;

    public static int entityViewDist = chunkViewDist;
    public static int sqr_entityViewDist = entityViewDist * entityViewDist;

    public static int tileEntityViewDist = chunkViewDist;
    public static int sqr_tileEntityViewDist = tileEntityViewDist * tileEntityViewDist;

}
