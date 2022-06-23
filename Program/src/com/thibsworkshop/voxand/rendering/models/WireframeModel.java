package com.thibsworkshop.voxand.rendering.models;

import com.thibsworkshop.voxand.loaders.Loader;
import com.thibsworkshop.voxand.physics.collisions.AABB;
import com.thibsworkshop.voxand.toolbox.Line;
import org.joml.Vector3f;

public class WireframeModel {

    private final RawModel rawModel;

    public Vector3f color;

    public WireframeModel(RawModel rawModel, Vector3f color){
        this.rawModel = rawModel;
        this.color = color;
    }

    public WireframeModel(Vector3f A, Vector3f B, Vector3f color){
        this.color = color;

        float[] positions = new float[]{
                A.x, A.y, A.z,
                B.x, B.y, B.z
        };

        int[] indices = new int[]{
                0,1
        };

        this.rawModel = Loader.loadToVAOLine(positions,indices);
    }

    public WireframeModel(Line line, Vector3f color){
        this(line.A, line.B, color);
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
