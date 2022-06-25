package com.thibsworkshop.voxand.physics.collisions;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.GameEntity;
import com.thibsworkshop.voxand.entities.Transform;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.physics.Rigidbody;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class CollisionEngine {


    private static final float PUSH_COEFF = 0.5f;


    //FIXME: No collisions when in high negative coordinates


    /**
     * Returns true if the entity is touching the ground, false otherwise
     * @param transform the transform of the entity
     * @return true if grounded, false otherwise
     */
    public static boolean isGrounded(Transform transform, AABB aabb){
        Vector3f pos = transform.getPosition();
        real.set((float) Maths.doubleMod(pos.x, Chunk.D_CHUNK_SIZE),
                pos.y - 2 * Maths.EPSILON,
                (float)Maths.doubleMod(pos.z, Chunk.D_CHUNK_SIZE));

        //Debug.printVector(pos); Debug.printVector(real);
        //Debug.printVector(transform.chunkPos);
        //System.out.println("------------------");
        min.set(aabb.min.x + real.x, aabb.min.y + real.y, aabb.min.z + real.z);
        max.set(aabb.max.x + real.x, aabb.max.y + real.y, aabb.max.z + real.z);

        Vector2i chunkPos = transform.chunkPos; //By default the real chunk pos is the initial player one
        int y = (int) Math.floor(min.y);

        return checkSolid(
                (int)Math.floor(min.x), y, (int) Math.floor(min.z),
                (int)Math.floor(max.x), y, (int) Math.floor(max.z),
                chunkPos.x,chunkPos.y
                );
    }

    //<editor-fold desc="AABBvsAABB">
    private static final Vector3f minAmaxB = new Vector3f(0);
    private static final Vector3f maxAminB = new Vector3f(0);
    private static final Vector3f AB = new Vector3f(0);
    public static void entityVSentity(GameEntity gameEntityA, GameEntity gameEntityB){ //TODO: Implement least overlapping axis
        Rigidbody rigidA = gameEntityA.rigidbody;
        Rigidbody rigidB = gameEntityB.rigidbody;

        AABB aabbA = rigidA.getCollider().getAabb();
        AABB aabbB = rigidB.getCollider().getAabb();
        Vector3f posA = gameEntityA.transform.getPosition();

        minI.set(aabbA.min.x + posA.x, aabbA.min.y + posA.y, aabbA.min.z + posA.z);
        maxI.set(aabbA.max.x + posA.x, aabbA.max.y + posA.y, aabbA.max.z + posA.z);
        Vector3f posB = gameEntityB.transform.getPosition();
        min.set(aabbB.min.x + posB.x, aabbB.min.y + posB.y, aabbB.min.z + posB.z);
        max.set(aabbB.max.x + posB.x, aabbB.max.y + posB.y, aabbB.max.z + posB.z);

        minAmaxB.set(max).sub(minI);
        maxAminB.set(min).sub(maxI);

        if(aabbVSaabb(minI,maxI,min,max)){
            //OPTIMIZE: Maybe don't normalize distance (avoiding on sqrt) and divide force by A²+B²
            float sqrDist = Maths.sqrDistance_xz(posA,posB);
            float dist = (float)Math.sqrt(sqrDist);
            AB.set(posB.x - posA.x, 0, posB.z - posA.z).div(Math.max(dist,0.0001f));

            float force = PUSH_COEFF / Math.max(sqrDist,0.2f);
            float forceA = force * (rigidB.mass / rigidA.mass) * Time.getDeltaTime();
            float forceB = force * (rigidA.mass / rigidB.mass) * Time.getDeltaTime();

            rigidA.addVelocity(-AB.x * forceA, 0, -AB.z * forceA);
            rigidB.addVelocity(AB.x * forceB, 0, AB.z * forceB);
            //System.out.println(rigidA.mass + " " + rigidB.mass);
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
    public static boolean aabbVSaabb(Vector3f minA, Vector3f maxA, Vector3f minB, Vector3f maxB){
        return (minA.x <= maxB.x && maxA.x >= minB.x) &&
                (minA.y <= maxB.y && maxA.y >= minB.y) &&
                (minA.z <= maxB.z && maxA.z >= minB.z);
    }

    public static boolean aabbVSaabb(Vector3f minAmaxB, Vector3f maxAminB){
        return (0 <= minAmaxB.x && 0 <= minAmaxB.y && 0 <= minAmaxB.z) &&
                (maxAminB.x <= 0 && maxAminB.y <= 0 && maxAminB.z <= 0);
    }

    private static final Vector2i getChunkPos = new Vector2i(0);
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
    public static boolean checkSolid(int x, int y, int z, int X, int Y, int Z, int chunkX, int chunkZ) {
        for (int i = x; i <= X; i++) {
            int rx = Chunk.correctPosition(i);
            int rChunkX = Chunk.correctChunkPosition(chunkX, i);
            for (int k = z; k <= Z; k++) {
                int rz = Chunk.correctPosition(k);
                int rChunkZ = Chunk.correctChunkPosition(chunkZ, k);
                for (int j = y; j <= Y; j++) {
                    getChunkPos.set(rChunkX, rChunkZ);
                    if ( j >= 0 && j < Chunk.CHUNK_HEIGHT && TerrainManager.isBlockSolid(rx, j, rz, getChunkPos)){
                        /*//System.out.println("x: " + i + " y: " + j + " z: " + k + " chunkX: " + chunkX + " chunkZ: " + chunkZ);
                        System.out.println("rx: " + rx + " ry: " + y + " rz: " + rz + " rchunkX: " + rChunkX + " rchunkZ: " + rChunkZ);
                        //System.out.println("Block: " + TerrainManager.getBlock(rx, j, rz, getChunkPos));
                        //System.out.println("Chunk: " + TerrainManager.getChunk(getChunkPos).getChunkPos());
                        System.out.println("Block in chunk: " + TerrainManager.getChunk(getChunkPos).grid[rx][j][rz]);

                        //System.out.println("---------------");*/
                        return true;
                    }

                }
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="AABB vs TERRAIN">
    private static final Vector3f normal = new Vector3f(0);
    public static void entityVSterrain(Transform transform, Vector3f movement, AABB aabb, Vector3i aura) {

        float minTime;
        float remainingTime = 1;

        for(int i = 0; i < 3; i++){
            //Debug.printVector(movement);
            movement.set(
                    movement.x * (1 - Math.abs(normal.x)) * remainingTime,
                    movement.y * (1 - Math.abs(normal.y)) * remainingTime ,
                    movement.z * (1 - Math.abs(normal.z)) * remainingTime);
            normal.set(0);

            minTime = entityVSterrainLoop(transform,movement, aabb, aura);

            //System.out.println("Before: ("+movement.x+", " + movement.z+ ") " + "(" +normal.x+ ", " + normal.z+ ") " + remainingTime);

            transform.translate(movement.x * minTime,movement.y * minTime, movement.z * minTime);


            if(minTime < 1.0f){
                transform.translate(normal.x * Maths.EPSILON, normal.y * Maths.EPSILON, normal.z * Maths.EPSILON);
            }

            remainingTime = 1.0f - minTime;

            if(remainingTime <= 0) break;
        }
        movement.set(0);
    }

    private static final Vector3f real = new Vector3f(); //player position in chunk space
    private static final Vector3f min = new Vector3f(); //minimum final collider position in chunk space
    private static final Vector3f max = new Vector3f(); //maximum final collider position in chunk space
    private static final Vector3f minI = new Vector3f(); //minimum initial collider position in chunk space
    private static final Vector3f maxI = new Vector3f(); //maximum initial collider position in chunk space
    private static final Vector3i minIint = new Vector3i(); //minimum initial collider position in chunk space floored
    private static final Vector3i maxIint = new Vector3i(); //maximum initial collider position in chunk space floored
    private static final Vector2i chunkPosR = new Vector2i(); //chunk position

    private static float entityVSterrainLoop(Transform transform, Vector3f velocity, AABB aabb, Vector3i aura){


		/* Here we are in chunk space, meanings that the initial center position is somewhere on the local grid
		   the minimum can be on another chunk than the maximum.
		   We first translate the player, then check collisions, and if there is one we apply the correction and
		   continue calculating.
		 */
        real.set(transform.getPosition());

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
            int rx = Chunk.correctPosition(x);
            chunkPosR.x = Chunk.correctChunkPosition(chunkPos.x,x);
            boolean xOKlength = x >= minIint.x && x <= maxIint.x;
            boolean xOK = x == minIint.x -1 || x == maxIint.x + 1;

            for (int z = minZ; z <= maxZ; z++) {
                int rz = Chunk.correctPosition(z);
                chunkPosR.y = Chunk.correctChunkPosition(chunkPos.y,z);
                boolean zOK = (xOKlength && (z == minIint.z-1 || z == maxIint.z+1)) ||
                        (xOK && z >= minIint.z && z <= maxIint.z);

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

    private static final Vector3f invEntry = new Vector3f();
    private static final Vector3f invExit = new Vector3f();
    private static final Vector3f entry = new Vector3f();
    private static final Vector3f exit = new Vector3f();

    private static float sweptAABB(Vector3f velocity, float x, float y, float z, float minTime){
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

        if(entryTime >= minTime) return 1.0f;
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



    /*public static RayHit rayVSterrain(Ray ray){

        step.x = ray.direction.x >= 0 ? 1 : -1;
        step.y = ray.direction.y >= 0 ? 1 : -1;
        step.z = ray.direction.z >= 0 ? 1 : -1;

        stepPos.set(ray.direction);


        blockPos.x = Maths.floor(ray.position.x);
        blockPos.y = Maths.floor(ray.position.y);
        blockPos.z = Maths.floor(ray.position.z);
        blockChunkPos.set(ray.chunkPosition);

        float sqrLength = 0;
        //System.out.println("BlockPos: " + blockPos + " chunk pos: " + blockChunkPos);
        if(TerrainManager.isBlockSolid(blockPos.x,blockPos.y, blockPos.z, ray.chunkPosition))
            return new RayHit(true,new Vector3i(blockPos), new Vector2i(blockChunkPos), (float)Math.sqrt(sqrLength));

        int i = 0;
        while(sqrLength < ray.getSqrLength()){
            i++;
            if(Math.abs(stepPos.x) > Math.abs(stepPos.z)){
                if(Math.abs(stepPos.x) > Math.abs(stepPos.y)){
                    stepPos.x += step.x;
                    blockChunkPos.x = Chunk.correctChunkPosition(blockChunkPos.x,blockPos.x + step.x);
                    blockPos.x = Chunk.correctPosition(blockPos.x + step.x);
                } else {
                    stepPos.y += step.y;
                    blockPos.y = Chunk.correctPosition(blockPos.y + step.y);
                }
            } else {
                if(Math.abs(stepPos.z) > Math.abs(stepPos.y)){
                    stepPos.z += step.z;
                    blockChunkPos.y = Chunk.correctChunkPosition(blockChunkPos.y,blockPos.y + step.y);
                    blockPos.z = Chunk.correctPosition(blockPos.z + step.z);
                } else {
                    stepPos.y += step.y;
                    blockPos.y = Chunk.correctPosition(blockPos.y + step.y);
                }
            }

            // Signed distance field of a cube:
            //TODO Verify the equation
            actualPosition.x = Maths.max(Math.abs(ray.position.x-stepPos.x + 0.5f)-0.5f,0f);
            actualPosition.y = Maths.max(Math.abs(ray.position.y-stepPos.y + 0.5f)-0.5f,0f);
            actualPosition.z = Maths.max(Math.abs(ray.position.z-stepPos.z + 0.5f)-0.5f,0f);
            sqrLength = actualPosition.lengthSquared();

            if(blockPos.y < 0 || blockPos.y > Chunk.CHUNK_HEIGHT)
                return new RayHit(false,null,null,(float)Math.sqrt(sqrLength));

            if(TerrainManager.isBlockSolid(blockPos.x,blockPos.y,blockPos.z,blockChunkPos)) {
                System.out.println("loop count: ");

                return new RayHit(true, new Vector3i(blockPos), new Vector2i(blockChunkPos), (float) Math.sqrt(sqrLength));
            }

        }


        return new RayHit(false,null,null,(float)Math.sqrt(sqrLength));
    }*/

    private static final Vector3i step = new Vector3i();
    private static final Vector3f tDelta = new Vector3f();
    private static final Vector3f dist = new Vector3f();
    private static final Vector3f tMax = new Vector3f();
    private static final Vector3i blockPos = new Vector3i();
    private static final Vector2i blockChunkPos = new Vector2i();
    private static final Vector3f actualPosition = new Vector3f();
    public static RayHit rayVSterrain( Ray ray) {

        // consider raycast vector to be parametrized by t
        //   vec = [px,py,pz] + t * [dx,dy,dz]

        // algo below is as described by this paper:
        // http://www.cse.chalmers.se/edu/year/2010/course/TDA361/grid.pdf
        // code translated from https://github.com/fenomas/fast-voxel-raycast

        float t = 0.0f;

        blockPos.x = Maths.floor(ray.position.x);
        blockPos.y = Maths.floor(ray.position.y);
        blockPos.z = Maths.floor(ray.position.z);

        blockChunkPos.set(ray.chunkPosition);

        step.x = ray.direction.x >= 0 ? 1 : -1;
        step.y = ray.direction.y >= 0 ? 1 : -1;
        step.z = ray.direction.z >= 0 ? 1 : -1;

        tDelta.x = Math.abs(1 / ray.direction.x);
        tDelta.y = Math.abs(1 / ray.direction.y);
        tDelta.z = Math.abs(1 / ray.direction.z);

        //Distance to nearest block
        dist.x = step.x > 0 ? blockPos.x + 1 - ray.position.x : ray.position.x - blockPos.x;
        dist.y = step.y > 0 ? blockPos.y + 1 - ray.position.y : ray.position.y - blockPos.y;
        dist.z = step.z > 0 ? blockPos.z + 1 - ray.position.z : ray.position.z - blockPos.z;

        // location of nearest voxel boundary, in units of t
        tMax.x = tDelta.x * dist.x;
        tMax.y = tDelta.y * dist.y;
        tMax.z = tDelta.z * dist.z;

        int steppedIndex = -1;
        ray.resetHit();

        // main loop along raycast vector
        while (t <= ray.length) {

            // exit check
            if(blockPos.y < 0 || blockPos.y >= Chunk.CHUNK_HEIGHT){ // Out of bounds
                ray.hit.distance = t;
                return ray.hit;
            }
            boolean b = TerrainManager.isBlockSolid(blockPos.x, blockPos.y,blockPos.z,blockChunkPos);
            if (b) {
                t+=0.001f;
                ray.hit.success = true;
                ray.hit.distance = t;
                ray.hit.position.set(
                        ray.position.x + t * ray.direction.x,
                        ray.position.y + t * ray.direction.y,
                        ray.position.z + t * ray.direction.z);
                ray.hit.chunkPosition.set(blockChunkPos);
                Chunk.correctPosition(ray.hit.position);

                ray.hit.normal.set(0);
                if (steppedIndex == 0) ray.hit.normal.x = -step.x;
                if (steppedIndex == 1) ray.hit.normal.y = -step.y;
                if (steppedIndex == 2) ray.hit.normal.z = -step.z;

                return ray.hit;
            }

            // advance t to next nearest voxel boundary
            if (tMax.x < tMax.y) {
                if (tMax.x < tMax.z) {

                    blockPos.x += step.x;
                    blockChunkPos.x = Chunk.correctChunkPosition(blockChunkPos.x,blockPos.x);
                    blockPos.x = Chunk.correctPosition(blockPos.x);
                    t = tMax.x;
                    tMax.x += tDelta.x; // We keep track of how much we went compared to the ray direction
                    steppedIndex = 0;
                } else {
                    blockPos.z += step.z;
                    blockChunkPos.y = Chunk.correctChunkPosition(blockChunkPos.y,blockPos.z);
                    blockPos.z = Chunk.correctPosition(blockPos.z);
                    t = tMax.z;
                    tMax.z += tDelta.z;
                    steppedIndex = 2;
                }
            } else {
                if (tMax.y < tMax.z) {
                    blockPos.y += step.y;
                    t = tMax.y;
                    tMax.y += tDelta.y;
                    steppedIndex = 1;
                } else {
                    blockPos.z += step.z;
                    blockChunkPos.y = Chunk.correctChunkPosition(blockChunkPos.y,blockPos.z);
                    blockPos.z = Chunk.correctPosition(blockPos.z);
                    t = tMax.z;
                    tMax.z += tDelta.z;
                    steppedIndex = 2;
                }
            }

        }

        ray.hit.distance = ray.length;
        return ray.hit;

    }

}
