package com.thibsworkshop.voxand.physics;

import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;

/**
 *
 */
public class Rigidbody {

    public Vector3f velocity;
    public float mass;
    public Entity entity;

    private Vector3f correction;

    public Rigidbody(float mass, Entity entity){
        this.mass = mass;
        this.entity = entity;
        velocity = new Vector3f(0,0,0);
        correction = new Vector3f(0,0,0);
    }

    public void update(){
        //First we detect the collisions based on the velocity
        if(velocity != Maths.zero){
            //With the correction vector, we can move the entity to its real position
            if(entity.getModel().collider.detectCollision(this, correction))
                entity.transform.translate(correction);
        }
    }

    public void setVelocity(float x, float y, float z){
        velocity.x = x;
        velocity.y = y;
        velocity.z = z;
    }
}
