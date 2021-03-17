package com.thibsworkshop.voxand.physics;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.Transform;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.AABB;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class CollisionEngine {

    private Vector3f normal = new Vector3f(0);

    private final float PUSH_COEFF = 1f;

    public static CollisionEngine engine;

    public CollisionEngine(){
        engine = this;
    }

    /**
     * Returns true if the entity is touching the ground, false otherwise
     * @param transform the transform of the entity
     * @return true if grounded, false otherwise
     */
    public boolean isGrounded(Transform transform, AABB aabb){
        Vector3f pos = transform.getPosition();
        real.set((float) Maths.floatMod(pos.x, Chunk.D_CHUNK_SIZE),
                pos.y - 2 * Maths.EPSILON,
                (float)Maths.floatMod(pos.z, Chunk.D_CHUNK_SIZE));

        //Debug.printVector(pos); Debug.printVector(real);
        //Debug.printVector(transform.chunkPos);
        //System.out.println("------------------");

        min.set(aabb.min.x + real.x, aabb.min.y + real.y, aabb.min.z + real.z);
        max.set(aabb.max.x + real.x, aabb.max.y + real.y, aabb.max.z + real.z);

        Vector2i chunkPos = transform.chunkPos; //By default the real chunk pos is the initial player one
        int y = (int) Math.floor(min.y);

        boolean ok = checkSolid(
                (int)Math.floor(min.x), y, (int) Math.floor(min.z),
                (int)Math.floor(max.x), y, (int) Math.floor(max.z),
                chunkPos.x,chunkPos.y
                );
        return ok;
    }

    private Vector3f AB = new Vector3f();
    public void entityVSentity(Transform entityA, Transform entityB, Rigidbody rigidA, Rigidbody rigidB){
        AABB aabbA = rigidA.getCollider().getAabb();
        AABB aabbB = rigidB.getCollider().getAabb();
        Vector3f velA = rigidA.velocity;
        Vector3f velB = rigidB.velocity;
        Vector3f posA = entityA.getPosition();

        minI.set(aabbA.min.x + posA.x, aabbA.min.y + posA.y, aabbA.min.z + posA.z);
        maxI.set(aabbA.max.x + posA.x, aabbA.max.y + posA.y, aabbA.max.z + posA.z);
        Vector3f posB = entityB.getPosition();
        min.set(aabbB.min.x + posB.x, aabbB.min.y + posB.y, aabbB.min.z + posB.z);
        max.set(aabbB.max.x + posB.x, aabbB.max.y + posB.y, aabbB.max.z + posB.z);

        if(aabbVSaabb(minI,maxI,min,max)){
            AB.set(min).sub(minI);
            float invDist = PUSH_COEFF / Maths.sqrDistance(posA,posB);
            float massCoeffA = rigidA.mass / rigidB.mass;
            float forceA = invDist * massCoeffA;
            float forceB = invDist / massCoeffA;
            rigidA.addVelocity(-AB.x * forceA, -AB.y * forceA, -AB.z * forceA);
            rigidB.addVelocity(AB.x * forceB, AB.y * forceB, AB.z * forceB);
        }
    }

    /**
     * Compares the first AABB with the second
     * @param minA min first AABB
     * @param maxA max first AABB
     * @param minB min second AABB
     * @param maxB max second AABB
     * @return true if the first AABB overlaps the second.
     */
    public boolean aabbVSaabb(Vector3f minA, Vector3f maxA, Vector3f minB, Vector3f maxB){
        return (minA.x <= maxB.x && maxA.x >= minB.x) &&
                (minA.y <= maxB.y && maxA.y >= minB.y) &&
                (minA.z <= maxB.z && maxA.z >= minB.z);
    }

    /**
     * Check every block in the given boundaries and return false if none of them is solid
     * @param x min x
     * @param y min y
     * @param z min z
     * @param X max X (included)
     * @param Y max Y (included)
     * @param Z max Z (included)
     * @param chunkX x chunk pos of the minimum bound
     * @param chunkZ z chunk pos of the minimum bound
     * @return true if one of the blocks is solid, false otherwise
     */
    public boolean checkSolid(int x, int y, int z, int X, int Y, int Z, int chunkX, int chunkZ) {
        for (int i = x; i <= X; i++) {
            int rx = Chunk.posCorrect(i);
            int rChunkX = Chunk.chunkPosCorrect(chunkX, i);
            for (int k = z; k <= Z; k++) {
                int rz = Chunk.posCorrect(k);
                int rChunkZ = Chunk.chunkPosCorrect(chunkZ, k);
                for (int j = y; j <= Y; j++) {
                    if ( j >= 0 && j < Chunk.CHUNK_HEIGHT && TerrainManager.isBlockSolid(rx, j, rz, rChunkX, rChunkZ)){
                        //System.out.println("x: " + i + " y: " + j + " z: " + k + " chunkX: " + chunkX + " chunkZ: " + chunkZ);
                        //System.out.println("rx: " + rx + " ry: " + y + " rz: " + rz + " rchunkX: " + rChunkX + " rchunkZ: " + rChunkZ);
                        //System.out.println("---------------");

                        return true;
                    }

                }
            }
        }
        return false;
    }


    //<editor-fold desc="AABB vs TERRAIN">
    public void entityVSterrain(Transform transform, Vector3f movement, AABB aabb, Vector3i aura) {

        float minTime = 0;
        float remainingTime = 1;

        for(int i = 0; i < 3; i++){

            movement.set(
                    movement.x * (1 - Math.abs(normal.x)) * remainingTime,
                    movement.y * (1 - Math.abs(normal.y)) * remainingTime ,
                    movement.z * (1 - Math.abs(normal.z)) * remainingTime);
            normal.set(0);

            minTime = entityVSterrainLoop(transform,movement, aabb, aura);

            transform.translate(movement.x * minTime,movement.y * minTime, movement.z * minTime);
            if(minTime < 1.0f){
                transform.translate(normal.x * Maths.EPSILON, normal.y * Maths.EPSILON, normal.z * Maths.EPSILON);
            }

            remainingTime = 1.0f - minTime;
            //System.out.print("i: " + i + " Entry time: " + minTime + " normal: "); Debug.printVector(normal);


            //System.out.print(i + " : " +remainingTime + " "); Debug.printVector(normal);

            if(remainingTime <= 0) break;
        }
        movement.set(0);
    }

    private Vector3f real = new Vector3f(); //player position in chunk space
    private Vector3f min = new Vector3f(); //minimum final collider position in chunk space
    private Vector3f max = new Vector3f(); //maximum final collider position in chunk space
    private Vector3f minI = new Vector3f(); //minimum initial collider position in chunk space
    private Vector3f maxI = new Vector3f(); //maximum initial collider position in chunk space
    private Vector3i minIint = new Vector3i(); //minimum initial collider position in chunk space floored
    private Vector3i maxIint = new Vector3i(); //maximum initial collider position in chunk space floored
    private Vector2i chunkPosR = new Vector2i(); //chunk position
    private Vector3i collisionPos = new Vector3i(); //block we collided with

    private boolean climb = false; //If true, we override the collision detection, push the player up and start again

    private float entityVSterrainLoop(Transform transform, Vector3f velocity, AABB aabb, Vector3i aura){

        Vector3f positionI = transform.getPosition(); //initial global position

		/* Here we are in chunk space, meanings that the initial center position is somewhere on the local grid
		   the minimum can be on another chunk than the maximum.
		   We first translate the player, then check collisions, and if there is one we apply the correction and
		   continue calculating.
		 */
        real.set((float)Maths.floatMod(positionI.x,Chunk.D_CHUNK_SIZE),
                positionI.y,
                (float)Maths.floatMod(positionI.z, Chunk.D_CHUNK_SIZE));

        minI.set(aabb.min.x + real.x, aabb.min.y + real.y, aabb.min.z + real.z);
        maxI.set(aabb.max.x + real.x, aabb.max.y + real .y, aabb.max.z + real.z);

        minIint.set(Maths.floor(minI.x),Maths.floor(minI.y),Maths.floor(minI.z));
        maxIint.set(Maths.floor(maxI.x),Maths.floor(maxI.y),Maths.floor(maxI.z));

        real.add(velocity);

        min.set(aabb.min.x + real.x, aabb.min.y + real.y, aabb.min.z + real.z);
        max.set(aabb.max.x + real.x, aabb.max.y + real.y, aabb.max.z + real.z);

        int minX = Maths.floor(Math.min(min.x,minI.x));
        int minY = Maths.floor(Math.min(min.y,minI.y));
        int minZ = Maths.floor(Math.min(min.z,minI.z));
        int maxX = Maths.floor(Math.max(max.x,maxI.x));
        int maxY = Maths.floor(Math.max(max.y,maxI.y));
        int maxZ = Maths.floor(Math.max(max.z,maxI.z));

        float minTime = 1.0f;
        Vector2i chunkPos = transform.chunkPos; //By default the real chunk pos is the initial player one

        for (int x = minX; x <= maxX; x ++) {
            int rx = Chunk.posCorrect(x);
            chunkPosR.x = Chunk.chunkPosCorrect(chunkPos.x,x);
            boolean xOKlength = x >= minIint.x && x <= maxIint.x;
            boolean xOK = x == minIint.x -1 || x == maxIint.x + 1;

            for (int z = minZ; z <= maxZ; z++) {
                int rz = Chunk.posCorrect(z);
                chunkPosR.y = Chunk.chunkPosCorrect(chunkPos.y,z);
                boolean zOK = (xOKlength && (z == minIint.z-1 || z == maxIint.z+1)) || (xOK && z >= minIint.z && z <= maxIint.z);

                for (int y = minY; y <= maxY; y++) {

                    if (y >= 0 && y < Chunk.CHUNK_HEIGHT && TerrainManager.isBlockSolid(rx, y, rz, chunkPosR)){
                        float collisionTime = sweptAABB(velocity,x,y,z,minTime);
                        if(y == minIint.y && collisionTime < 1 && zOK){
                            //If the block we collided with is close to the entity,
                            //we check for the surrounding blocks if they are solid.
                            //If they are not, the entity auto climbs it
                           if(!checkSolid(
                                    Math.min(minIint.x,x),minIint.y + 1, Math.min(minIint.z,z),
                                    Math.max(maxIint.x,x), minIint.y + 1 + aura.y, Math.max(maxIint.z,z),
                                    chunkPos.x,chunkPos.y)){
                                climb = true;
                                //TODO: The aura.y is not very good practice.
                                transform.translate(0,1,0);

                                return entityVSterrainLoop(transform,velocity,aabb,aura);
                            }
                        }

                        if(collisionTime < minTime)
                            minTime = collisionTime;
                    }
                }
            }
        }
        return minTime;
    }

    private Vector3f invEntry = new Vector3f();
    private Vector3f invExit = new Vector3f();
    private Vector3f entry = new Vector3f();
    private Vector3f exit = new Vector3f();

    private float sweptAABB(Vector3f velocity, float x, float y, float z, float minTime){
        // find the distance between the objects on the near and far sides for both x, y and z
        // and find time of collision and time of leaving for each axis
        if (velocity.x > 0.0f) {
            invEntry.x = x - maxI.x;
            entry.x = invEntry.x / velocity.x;
            invExit.x = x + 1 - minI.x;
            exit.x = invExit.x / velocity.x;
        } else if (velocity.x < 0.0f){
            invEntry.x = x + 1 - minI.x;
            entry.x = invEntry.x / velocity.x;
            invExit.x = x - maxI.x;
            exit.x = invExit.x / velocity.x;
        } else {
            invEntry.x = x + 1 - minI.x;
            invExit.x = x - maxI.x;
            entry.x = -Float.MAX_VALUE;
            exit.x = Float.MAX_VALUE;
        }

        if (velocity.y > 0.0f) {
            invEntry.y = y - maxI.y;
            entry.y = invEntry.y / velocity.y;
            invExit.y = y + 1 - minI.y;
            exit.y = invExit.y / velocity.y;
        } else if (velocity.y < 0.0f){
            invEntry.y = y + 1 - minI.y;
            entry.y = invEntry.y / velocity.y;
            invExit.y = y - maxI.y;
            exit.y = invExit.y / velocity.y;
        } else {
            invEntry.y = y + 1 - minI.y;
            invExit.y = y - maxI.y;
            entry.y = -Float.MAX_VALUE;
            exit.y = Float.MAX_VALUE;
        }

        if (velocity.z > 0.0f) {
            invEntry.z = z - maxI.z;
            entry.z = invEntry.z / velocity.z;
            invExit.z = z + 1 - minI.z;
            exit.z = invExit.z / velocity.z;
        } else if (velocity.z < 0.0f){
            invEntry.z = z + 1 - minI.z;
            entry.z = invEntry.z / velocity.z;
            invExit.z = z - maxI.z;
            exit.z = invExit.z / velocity.z;
        } else {
            invEntry.z = z + 1 - minI.z;
            invExit.z = z - maxI.z;
            entry.z = -Float.MAX_VALUE;
            exit.z = Float.MAX_VALUE;
        }
        // find the earliest/latest times of collision

        float entryTime = Math.max(Math.max(entry.x,entry.z),entry.y);

        //if(entryTime >= minTime) return 1.0f;
        if(entryTime < 0) return 1.0f;

        float exitTime = Math.min(Math.min(exit.x,exit.z),exit.y);

        if (entryTime > exitTime) return 1.0f;

        if(entry.x > 1.0f) {
            if(max.x < x || min.x > x + 1)
                return 1.0f;
        }

        if(entry.y > 1.0f){
            if(max.y < y || min.y > y + 1)
                return 1.0f;
        }

        if(entry.z > 1.0f){
            if(max.z < z || min.z > z + 1)
                return 1.0f;
        }

        if (entry.x > entry.z) {
            if(entry.x > entry.y){
                normal.x = -Maths.sign(velocity.x);
                normal.y = 0.0f;
                normal.z = 0.0f;
            }else{
                normal.x = 0.0f;
                normal.y = -Maths.sign(velocity.y);
                normal.z = 0.0f;
            }
        } else {
            if (entry.z > entry.y) {
                normal.x = 0.0f;
                normal.y = 0.0f;
                normal.z = -Maths.sign(velocity.z);
            } else {
                normal.x = 0.0f;
                normal.y = -Maths.sign(velocity.y);
                normal.z = 0.0f;
            }
        }
        return entryTime;
    }
    //</editor-fold>
}
