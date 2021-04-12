package com.thibsworkshop.voxand.data;

public interface IBiome {

    float generate_xz(long x, long z);

    float generate_xyz(long x, long y, long z);
}
