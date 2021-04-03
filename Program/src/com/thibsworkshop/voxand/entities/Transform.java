package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.debugging.Debug;
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

    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;

    private final Vector3f forward;
    private final Vector3f right;

    public final Vector2i chunkPos;
    public Chunk chunk;
    private final Matrix4f transformationMatrix;

    private boolean changed = false; // Has any of position, rotation or scale changed since last update of the transformationMatrix?
    private boolean positionChanged = false; // Has position changed since last update?

    /**
     * Has chunk position changed?
     */
    private boolean chunkPosChanged = false;

    //<editor-fold desc="Constructors">
    public Transform(Vector3f position, Vector3f rotation, Vector3f scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.chunkPos = Chunk.positionToChunkPos(position);
        this.transformationMatrix = new Matrix4f();
        Maths.updateTransformationMatrix(this);
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

    //</editor-fold>

    /**
     * If a change occurred since last update, updates the transformation matrix.
     * If also the position changed, updates the entity's chunk position.
     */
    public void update(){
        if(changed){
            Maths.updateTransformationMatrix(this);
            changed = false;
            if(positionChanged) {
                chunkPosChanged = updateChunkPos();
                positionChanged = false;
            }
        }
        if(chunk == null){
            chunk = TerrainManager.getChunk(chunkPos);
        }
    }

    public void lateUpdate(){
        chunkPosChanged = false;
    }

    private final Vector2i newChunkPos = new Vector2i(0);
    /**
     * Updates the chunk position of the transform
     */
    public boolean updateChunkPos(){
        Chunk.positionToChunkPos(position, newChunkPos);
        if(!newChunkPos.equals(chunkPos)){
            chunkPos.set(newChunkPos);
            chunk = TerrainManager.getChunk(chunkPos);
            return true;
        }
        return false;
    }

    /**
     * Transforms the given position from local to global space, without taking rotations into account.
     * @param localPosition the position to transform
     * @return a new position transformed from local to global space
     */
    public Vector3f localToWorldPositionUnrotated(Vector3f localPosition){
        return new Vector3f(localPosition).mul(scale).add(position);
    }

    /**
     * Updates and returns the local forward vector of the transform
     * @return a reference to the local forward vector of the transform
     */
    public Vector3f forward(){
        transformationMatrix.positiveZ(forward).negate();
        return forward;
    }

    /**
     * Updates and returns the local right vector of the transform
     * @return a reference to the local right vector of the transform
     */
    public Vector3f right(){
        transformationMatrix.positiveX(right);
        return right;
    }

    //<editor-fold desc="Setters">
    /**
     * Translates the transform by the given {@code translation}.
     * @param translation x y z shift
     */
    public void translate(Vector3f translation) {
        translate(translation.x, translation.y, translation.z);
    }

    /**
     * Translates the transform by the given values.
     * @param dx x offset
     * @param dy y offset
     * @param dz z offset
     */
    public void translate(float dx,float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
        changed = true;
        positionChanged = true;
    }

    /**
     * Sets the position of the transform
     * @param x x set
     * @param y y set
     * @param z z set
     */
    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
        changed = true;
        positionChanged = true;
    }

    /**
     * Sets the position of the transform
     * @param position new position
     */
    public void setPosition(Vector3f position){
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
        changed = true;
        positionChanged = true;
    }

    /**
     * Rotates the transform by the given {@code rotation}
     * @param rotation xyz shift
     */
    public void rotate(Vector3f rotation) {
        rotate(rotation.x, rotation.y, rotation.z);
    }

    /**
     * Rotates the transform by the given values
     * @param rx x shift
     * @param ry y shift
     * @param rz z shift
     */
    public void rotate(float rx, float ry, float rz) {
        this.rotation.x += rx;
        this.rotation.y += ry;
        this.rotation.z += rz;
        changed = true;
    }

    /**
     * Sets the transform's rotation
     * @param rotation new rotation
     */
    public void setRotation(Vector3f rotation){
        setRotation(rotation.x,rotation.y,rotation.z);
    }

    /**
     * Sets the transform's rotation
     * @param x new x
     * @param y new y
     * @param z new z
     */
    public void setRotation(float x, float y, float z){
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
        changed = true;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    public Matrix4f getTransformationMatrix(){ return transformationMatrix; }

    public Vector3f getPosition(){ return position; }

    public Vector3f getRotation(){ return rotation; }

    public Vector3f getScale(){ return scale;}

    public boolean hasChunkPosChanged(){ return chunkPosChanged; }
    //</editor-fold>

}
