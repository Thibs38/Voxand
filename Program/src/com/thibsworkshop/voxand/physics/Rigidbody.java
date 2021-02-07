package com.thibsworkshop.voxand.physics;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.Entity;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;

/**
 *
 */
public class Rigidbody {

    public static float gravity = -9.81f;
    public static float drag = 0.9f;
    private Vector3f dragVector;
    public Vector3f velocity;
    private Vector3f movement; //Movement calculated from the velocity
    public float mass;
    private Entity entity;
    private Collider collider;

    public static final float ZERO_VEL = 0.01f;

    public boolean grounded = false;
    public boolean gravited = true;


    public Rigidbody(float mass, Entity entity){
        this.mass = mass;
        this.entity = entity;
        velocity = new Vector3f(0);
        movement = new Vector3f(0);
        dragVector = new Vector3f(0);
        collider = entity.getModel().collider;
    }

    public void update(){
        movement.set(velocity).mul(Time.getDeltaTime());
        float rDrag = Math.max(1-drag*Time.getDeltaTime(),0);
        velocity.mul(1,rDrag,1);

        if(!velocity.equals(Maths.zero))
            grounded = collider.detectCollision(entity.transform, movement);

        grounded = collider.isGrounded(entity.transform);
            //velocity.set(0);
        //}
        //System.out.print("grounded: " + grounded);Debug.printVector(velocity);
        //System.out.println(entity.transform.getPosition());

        if(grounded)
            velocity.y = 0;
        else if(gravited)
            velocity.add(0,gravity * Time.getDeltaTime()*2,0);
    }

    public void setVelocity(float x, float y, float z){
        velocity.set(x,y,z);
    }

    public void addVelocity(float dx, float dy, float dz) { velocity.add(dx,dy,dz); }
    public void addVelocity(Vector3f v) { velocity.add(v); }

}
