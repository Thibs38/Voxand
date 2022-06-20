package com.thibsworkshop.voxand.data;

public interface IBiomeMap {

    /**
     * Choose the biome for the given x and z coordinate
     * @param x x coordinate
     * @param z z coordinate
     * @return a biome ID
     */
    int biome(long x, long z);

    /**
     * Generates a procedural precipitation value in the range [0, 1] at a given coordinate
     * @param x x coordinate
     * @param z z coordinate
     * @return precipitation in range [0, 1]
     */
    float precipitation(long x, long z);

    /**
     * Generates a procedural temperature in the range [0, 1] at a given coordinate
     * @param x x coordinate
     * @param z z coordinate
     * @return temperature in range [0, 1]
     */
    float temperature(long x, long z);

    /**
     * Generates a procedural elevation in the range [0, 1] at a given coordinate
     * @param x x coordinate
     * @param z z coordinate
     * @return elevation in the range [0, 1]
     */
    float elevation(long x, long z);
}
