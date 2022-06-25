package com.thibsworkshop.voxand.physics.collisions;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class RayHit {
    public Vector3f position;
    public Vector2i chunkPosition;

    public Vector3i normal;

    public boolean success;

    public float distance;


    public RayHit(){
        this.success = false;
        this.position = new Vector3f(0);
        this.chunkPosition = new Vector2i(0);
        this.distance = 0;
        this.normal = new Vector3i(0);
    }
}
