package com.thibsworkshop.voxand.physics.collisions;

import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;

public class AABB {

    public final Vector3f min;
    public final Vector3f max;
    public final Vector3f center;
    public final Vector3f size;

    private AABB (Vector3f min, Vector3f max, Vector3f center, Vector3f size){
        this.min = min;
        this.max = max;
        this.center = center;
        this.size = size;
    }

    public static AABB moreThanOne(){
        return createMinMax(Maths.lessThanZero,Maths.moreThanOne);
    }
    public static AABB one(){
        return createMinMax(Maths.zero,Maths.one);
    }

    public static AABB createMinMax(Vector3f min, Vector3f max){
        Vector3f center = new Vector3f(max).sub(min).mul(0.5f);
        Vector3f size = new Vector3f(max).sub(center);
        return new AABB(min,max,center,size);
    }

    public static AABB createCenterSize(Vector3f center, Vector3f size){
        Vector3f max = new Vector3f(center).add(size);
        Vector3f min = new Vector3f(center).sub(size);
        return new AABB(min,max,center,size);
    }
    public static AABB createCenterSize(Vector3f center, float xSize, float ySize, float zSize){
        Vector3f size = new Vector3f(xSize, ySize, zSize);
        Vector3f max = new Vector3f(center).add(size);
        Vector3f min = new Vector3f(center).sub(size);
        return new AABB(min,max,center,size);
    }
}
