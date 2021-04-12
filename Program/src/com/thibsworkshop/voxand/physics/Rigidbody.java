package com.thibsworkshop.voxand.physics;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.GameEntity;
import com.thibsworkshop.voxand.io.Input;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 *
 */
public class Rigidbody {

    public static float gravity = -9.81f;
    public float horizontalDrag = 8f;
    public float verticalDrag = 1f;
    public final Vector3f velocity = new Vector3f(0);
    private final Vector3f movement = new Vector3f(0); //Movement calculated from the velocity
    public float mass;
    private final GameEntity gameEntity;
    private final Collider collider;

    public boolean grounded = false;
    public boolean gravited = true;


    public Rigidbody(float mass, GameEntity gameEntity){
        this.mass = mass;
        this.gameEntity = gameEntity;
        collider = gameEntity.getModel().collider;
    }

    public void update(){

        if(!velocity.equals(Maths.zero)){
            movement.set(velocity).mul(Time.getDeltaTime());

            float rHDrag = Math.max(1-horizontalDrag*Time.getDeltaTime(),0);
            float rVDrag = Math.max(1-verticalDrag*Time.getDeltaTime(),0);
            velocity.mul(rHDrag,rVDrag,rHDrag);

            collider.detectCollision(gameEntity.transform, movement);

            gameEntity.transform.update();
            grounded = collider.isGrounded(gameEntity.transform);

        }

        if(grounded)
            velocity.y = 0;
        else if(gravited){
                velocity.add(0,gravity * Time.getDeltaTime()*2,0);
        }
    }

    public void setVelocity(float x, float y, float z){
        velocity.set(x,y,z);

    }

    public void addVelocity(float dx, float dy, float dz) {
        velocity.add(dx,dy,dz);

    }
    public void addVelocity(Vector3f v) {
        velocity.add(v);

    }

    public Collider getCollider(){ return collider; }

}
