package com.thibsworkshop.voxand.models;

import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.toolbox.AABB;
import org.joml.Vector3f;

public class WireframeModel {

    private RawModel rawModel;

    public Vector3f color;

    public WireframeModel(RawModel rawModel, Vector3f color){
        this.rawModel = rawModel;
        this.color = color;
    }

    public WireframeModel(AABB aabb, Vector3f color){
        this.color = color;

        float x = aabb.min.x;
        float y = aabb.min.y;
        float z = aabb.min.z;
        float a = aabb.max.x;
        float b = aabb.max.y;
        float c = aabb.max.z;

        float[] positions = new float[]{
                x,y,z,
                a,y,z,
                a,b,z,
                x,b,z,

                x,y,c,
                a,y,c,
                a,b,c,
                x,b,c
        };

        int[] indices = new int[]{
                0,1,1,2,
                2,3,3,0,

                4,5,5,6,
                6,7,7,4,

                0,4,1,5,
                2,6,3,7
        };
        this.rawModel = Loader.loadToVAOLine(positions,indices);
    }

    public RawModel getRawModel(){ return rawModel; }
}
