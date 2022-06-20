package data.terrain_generation.biomes

import com.thibsworkshop.voxand.data.Biome
import com.thibsworkshop.voxand.terrain.GridGenerator
import com.thibsworkshop.voxand.toolbox.MinMax

class BiomePlains extends Biome{

    BiomePlains() {
        super(0, "plains", new MinMax(15,35), new MinMax(30,70))
    }

    @Override
    float generate_xz(long x, long z) {
        double xd = x
        double zd = z
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

    @Override
    float generate_xyz(long x, long y, long z) {
        return 0
    }
}
