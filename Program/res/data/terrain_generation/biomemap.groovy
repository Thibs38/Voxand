package data.terrain_generation

import com.thibsworkshop.voxand.data.IBiomeMap
import com.thibsworkshop.voxand.toolbox.SimplexNoise

class BiomeMap implements IBiomeMap{

    @Override
    int biome(long x, long z) {
        float p = precipitation(x,z) //Precipitation value
        float t = temperature(x,z)   //Temperature value
        float e = elevation(x,z)     //Elevation value

        p *= t                       //Precipitation <= temperature
        t = t * 40 - 10              //[0, 1] -> [-10, 30]
        p = p * 60 + 10              //[0, 1] -> [10, 70]
        e = e * 100                  //[0, 1] -> [0, 100]

        
    }

    @Override
    float precipitation(long x, long z) {
        return SimplexNoise.noise(x,z)
    }


    @Override
    float temperature(long x, long z) {
        return SimplexNoise.noise(x,z)
    }


    @Override
    float elevation(long x, long z) {
        return SimplexNoise.noise(x,z);
    }
}
