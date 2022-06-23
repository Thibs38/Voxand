package com.thibsworkshop.voxand.physics.collisions;

import org.joml.Vector2i;
import org.joml.Vector3i;

public class RayHit {
    public Vector3i blockPosition;
    public Vector2i chunkPosition;

    public boolean success;

    public float distance;

    public RayHit(boolean success, Vector3i blockPosition, Vector2i chunkPosition, float distance){
        this.success = success;
        this.blockPosition = blockPosition;
        this.chunkPosition = chunkPosition;
        this.distance = distance;
    }
}
