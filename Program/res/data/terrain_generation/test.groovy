package data.terrain_generation

import com.thibsworkshop.voxand.terrain.GridGenerator
import com.thibsworkshop.voxand.terrain.TerrainGenerator

float generate_xz(long x, long z) {
    double xd = x;
    double zd = z;
    float freq = 1f
    float amp = 1f
    float s = 128f
    for(int i = 0; i < 4; i++){
        s += GridGenerator.simplex(xd, zd, 0.01f * freq) * 1f * amp;
        freq *=1f;
        amp *= 1f;
    }
    return s;
}