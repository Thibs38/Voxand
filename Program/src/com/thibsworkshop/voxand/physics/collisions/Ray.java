package com.thibsworkshop.voxand.physics.collisions;

import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;


public class Ray {

    public Vector3f position;
    public Vector2i chunkPosition;
    public Vector3f direction;

    public float length;

    private float sqrLength;

    public static ArrayList<Ray> rays = new ArrayList<Ray>();

    public Ray(Vector3f position, Vector2i chunkPosition, Vector3f direction, float length){
        this.position = position;
        this.direction = direction;
        this.chunkPosition = chunkPosition;
        this.length = length;
        this.sqrLength = length * length;
        Ray.rays.add(this);
    }

    public float getSqrLength() {
        return sqrLength;
    }

    public void close(){
        rays.remove(this);
    }
}
