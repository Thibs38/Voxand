package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.game.Config;
import com.thibsworkshop.voxand.physics.CollisionEngine;
import com.thibsworkshop.voxand.rendering.models.TexturedModel;
import com.thibsworkshop.voxand.rendering.renderers.MasterRenderer;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This class manage entities, it calls their update methods every frame, manage the tick update,
// manage adding and removing etc...
public class GameObjectManager {

    //OPTIMIZE: statistics about how many entities there are on average
    //OPTIMIZE: a lot of entries will be in entitiesInChunk, it needs to remove unloaded chunks
    //TODO: BE CAREFUL WHEN UNLOADING ENTITIES, THERE ARE TWO MAPS REFERRING TO THEM

    private final Map<TexturedModel,List<GameEntity>> entities = new HashMap<>(50);
    private final Map<TexturedModel,List<TileEntity>> tileEntities = new HashMap<>(50);

    private final Map<Vector2i,List<GameEntity>> entitiesInChunk = new HashMap<>();


    private final Map<TexturedModel,List<GameEntity>> entitiesToRender = new HashMap<>(50);
    private final Map<TexturedModel,List<TileEntity>> tileEntitiesToRender = new HashMap<>(50);

    public static GameObjectManager main;

    public GameObjectManager(){
        main = this;
    }

    public void update(){

        collisionUpdate();

        updateEntities();

        updateTileEntities();
    }

    private final Vector2i temp = new Vector2i(0);
    private void collisionUpdate(){
        for (Map.Entry<Vector2i, List<GameEntity>> entry : entitiesInChunk.entrySet()) {
            List<GameEntity> batch = entry.getValue();
            for (int i = 0; i < batch.size(); i++) {
                GameEntity gameEntityA = batch.get(i);

                if (gameEntityA.entityCollisionsDone)
                    continue;
                //FIXME: Entity are colliding with themselves
                for (int j = i + 1; j < batch.size(); j++) { //Collisions in the current chunk
                    GameEntity gameEntityB = batch.get(j);
                    if (gameEntityB.doEntityCollisions && !gameEntityB.entityCollisionsDone) {
                        CollisionEngine.entityVSentity(gameEntityA, gameEntityB);
                    }
                }
                temp.set(gameEntityA.transform.chunkPos);

                temp.sub(1, 0); //Collisions left
                loopCollisions(gameEntityA);

                temp.add(0, 1); //Collisions top left
                loopCollisions(gameEntityA);

                temp.add(1, 0); //Collisions top
                loopCollisions(gameEntityA);

                temp.add(1, 0); //Collisions top right
                loopCollisions(gameEntityA);

                temp.sub(0, 1); //Collisions right
                loopCollisions(gameEntityA);

                temp.sub(0, 1); //Collisions bottom right
                loopCollisions(gameEntityA);

                temp.sub(1, 0); //Collisions bottom
                loopCollisions(gameEntityA);

                temp.sub(1, 0); //Collisions bottom left
                loopCollisions(gameEntityA);

                gameEntityA.entityCollisionsDone = true;
            }
        }
    }

    private void loopCollisions(GameEntity gameEntityA){
        List<GameEntity> batchTemp = entitiesInChunk.get(temp);
        if (batchTemp != null) {
            for (GameEntity gameEntityB : batchTemp) {
                if (gameEntityB.doEntityCollisions && !gameEntityB.entityCollisionsDone) {
                    if(gameEntityA == gameEntityB){
                        System.err.println("BIG PROBLEM: ENTITY IS COLLIDING WITH ITSELF (multiple reference to this entity");
                    }
                    CollisionEngine.entityVSentity(gameEntityA, gameEntityB);
                }
            }
        }
    }

    private void updateEntities(){
        for(TexturedModel texturedModel : entities.keySet()){
            List<GameEntity> batch = entities.get(texturedModel);
            List<GameEntity> renderBatch = entitiesToRender.get(texturedModel);
            for (GameEntity gameEntity : batch) {
                gameEntity.entityCollisionsDone = false;
                if(gameEntity.doUpdate){
                    temp.set(gameEntity.transform.chunkPos);       //Setting the previous chunkPos before the update
                    gameEntity.update();                           //Update the entity only if it is enabled
                    if(gameEntity.transform.hasChunkPosChanged()){ //If the chunk position changed, move the entity from list
                        //System.out.print("CHUNK CHANGED ");
                        //Debug.printVector(gameEntity.transform.chunkPos);
                        Vector2i chunkPos = gameEntity.transform.chunkPos;
                        List<GameEntity> e = entitiesInChunk.get(temp);     //List where the entity was
                        if(e != null)                                   //If the list is not null
                            e.remove(gameEntity);                           //Remove its previous reference from it
                        e = entitiesInChunk.get(chunkPos);              //Get the new list
                        if(e == null) {                                 //Create it if it doesn't exist yet
                            entitiesInChunk.put(new Vector2i(chunkPos), new ArrayList<>());
                            e = entitiesInChunk.get(chunkPos);
                        }
                        e.add(gameEntity); //New reference
                    }
                    gameEntity.lateUpdate(); //TODO: Probably move this later in code
                }

                boolean alreadyInBatch = renderBatch.contains(gameEntity);
                if (gameEntity.doRendering && gameEntity.transform.chunk != null && gameEntity.transform.chunk.getSqr_distance() <= Config.sqr_entityViewDist) {
                    if (!alreadyInBatch) {
                        renderBatch.add(gameEntity);
                        System.out.println("ADDING ENTITY TO RENDER LIST");
                    }
                } else {
                    if (alreadyInBatch) renderBatch.remove(gameEntity);
                }
            }
        }
    }

    private void updateTileEntities(){
        for(TexturedModel texturedModel : tileEntities.keySet()){
            List<TileEntity> batch = tileEntities.get(texturedModel);
            List<TileEntity> renderBatch = tileEntitiesToRender.get(texturedModel);
            for(TileEntity tileEntity : batch){ //TODO: wrong
                tileEntity.update();
                boolean render = renderBatch.contains(tileEntity);
                if(tileEntity.transform.chunk.getSqr_distance() <= Config.sqr_tileEntityViewDist){
                    if(!render) renderBatch.add(tileEntity);
                }else{
                    if(render) renderBatch.remove(tileEntity);
                }
            }
        }
    }

    public void processEntity(GameEntity gameEntity) {
        TexturedModel entityTexturedModel = gameEntity.getModel();
        List<GameEntity> batch = entities.get(entityTexturedModel);
        if(batch == null) {
            List<GameEntity> newBatch = new ArrayList<>(10);
            List<GameEntity> newRenderBatch = new ArrayList<>(10);
            newBatch.add(gameEntity);
            entities.put(entityTexturedModel,newBatch);
            entitiesToRender.put(entityTexturedModel,newRenderBatch);
        }else {
            batch.add(gameEntity);
        }

        Vector2i chunkPos = gameEntity.transform.chunkPos;
        batch = entitiesInChunk.get(chunkPos);
        if(batch == null) {
            entitiesInChunk.put(chunkPos, new ArrayList<>());
            batch = entitiesInChunk.get(chunkPos);
        }
        batch.add(gameEntity); //New reference
    }

    public void processTileEntity(TileEntity tileEntity) {
        TexturedModel texturedModel = tileEntity.getModel();
        List<TileEntity> batch = tileEntities.get(texturedModel);
        if(batch == null) {
            List<TileEntity> newBatch = new ArrayList<>(10);
            List<TileEntity> newRenderBatch = new ArrayList<>(10);
            newBatch.add(tileEntity);
            tileEntities.put(texturedModel,newBatch);
            tileEntitiesToRender.put(texturedModel,newRenderBatch);
        }else {
            batch.add(tileEntity);
        }
    }

    public void removeEntity(GameEntity gameEntity){
        entities.get(gameEntity.texturedModel).remove(gameEntity);
        entitiesToRender.get(gameEntity.texturedModel).remove(gameEntity);
    }

    public void removeEntity(TileEntity tileEntity){
        tileEntities.get(tileEntity.texturedModel).remove(tileEntity);
        tileEntitiesToRender.get(tileEntity.texturedModel).remove(tileEntity);
    }

    public void genEntityWireframe(){
        for(TexturedModel texturedModel : entities.keySet()) {
            if(texturedModel.collider != null)
                texturedModel.collider.createWireframe();
        }
    }

    public void genTileEntityWireframe(){
        for(TexturedModel texturedModel : tileEntities.keySet()) {
            if(texturedModel.collider != null)
                texturedModel.collider.createWireframe();
        }
    }

    public void destroyWireframes(){
        for(TexturedModel texturedModel : entities.keySet()) {
            if(texturedModel.collider != null)
                texturedModel.collider.destroyWireframe();
        }
        for(TexturedModel texturedModel : tileEntities.keySet()) {
            if(texturedModel.collider != null)
                texturedModel.collider.destroyWireframe();
        }
    }

    public Map<TexturedModel,List<TileEntity>> getTileEntitiesToRender(){ return tileEntitiesToRender; }

    public Map<TexturedModel,List<GameEntity>> getEntitiesToRender(){ return entitiesToRender; }

}
