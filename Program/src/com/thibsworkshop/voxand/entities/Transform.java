package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;

import java.util.Vector;

public class Transform {

    private Vector3f position; //TODO change for doubles (will break a lot of code)
    private Vector3f rotation;
    private Vector3f scale;

    private Vector3f forward;
    private Vector3f right;


    public Vector2i chunkPos;
    public Chunk chunk;
    private Matrix4f transformationMatrix;

    private boolean changed = false; // Has any of position, rotation or scale changed since last update of the transformationMatrix?
    private boolean positionChanged = false; // Has position changed since last update?

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.chunkPos = Chunk.positionToChunkPos(position);
        this.transformationMatrix = Maths.createTransformationMatrix(this);
        this.forward = new Vector3f(0,0,1);
        this.right = new Vector3f(1,0,0);
    }

    public Transform(Vector3f position, Vector3f rotation, float scale){
        this(position,rotation, new Vector3f(scale));
    }
    public Transform(Vector3f position, Vector3f rotation){
        this(position,rotation, new Vector3f(1));
    }

    public Transform(Vector3f position){
        this(position,new Vector3f(0), new Vector3f(1));
    }

    public Transform(){
        this(new Vector3f(0), new Vector3f(0), new Vector3f(1));
    }

    //return true if changes happened
    public boolean update(){
        if(changed){
            transformationMatrix = Maths.createTransformationMatrix(this);
            changed = false;
            if(positionChanged) {
                Chunk.positionToChunkPos(position, chunkPos);
                chunk = TerrainManager.getChunk(chunkPos);
                positionChanged = false;
            }
            return true;
        }
        return false;
    }

    //return a position transformed from local space to world space, not rotated however
    public Vector3f localToWorldPositionUnrotated(Vector3f localPosition){
        return new Vector3f(localPosition).mul(scale).add(position);
    }

    //TODO: not working
    /*
    public Vector3f localToWorldPosition(Vector3f localPosition){
        Vector3f v = new Vector3f();
        return transformationMatrix.transformPosition(localPosition,v);
    }
    public Vector3f localToWorldDirection(Vector3f localDirection){
        Vector3f v = new Vector3f();
        return transformationMatrix.transformDirection(localDirection,v);
    }

    public Vector3f forward(){
        Vector3f v = new Vector3f();
        transformationMatrix.(v);
        return v;
    }*/

    public Vector3f forward(){
        transformationMatrix.positiveZ(forward).negate();
        return forward;
    }

    public Vector3f right(){
        transformationMatrix.positiveX(right);
        return right;
    }

    public void translate(Vector3f translation) {
        translate(translation.x, translation.y, translation.z);
    }

    public void translate(float dx,float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
        changed = true;
        positionChanged = true;
    }

    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
        changed = true;
        positionChanged = true;
    }

    public void setPosition(Vector3f position){
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
        changed = true;
        positionChanged = true;
    }

    public void setTransformationMatrix(Matrix4f matrix){
        transformationMatrix.set(matrix);
    }

    public void updateChunkPos(){
        Chunk.positionToChunkPos(position, chunkPos);
        chunk = TerrainManager.getChunk(chunkPos);
    }

    public Vector3f getPosition(){ return position; }

    public Vector3f getRotation(){ return rotation; }

    public Vector3f getScale(){ return scale;}

    public Matrix4f getTransformation(){ return transformationMatrix; }

    public void rotate(Vector3f rotation) {
        rotate(rotation.x, rotation.y, rotation.z);
    }

    public void rotate(float rx, float ry, float rz) {
        this.rotation.x += rx;
        this.rotation.y += ry;
        this.rotation.z += rz;
        changed = true;
    }

    public void setRotation(Vector3f rotation){
        setRotation(rotation.x,rotation.y,rotation.z);
    }

    public void setRotation(float x, float y, float z){
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
        changed = true;

    }
    public Matrix4f getTransformationMatrix(){ return transformationMatrix; }

}
