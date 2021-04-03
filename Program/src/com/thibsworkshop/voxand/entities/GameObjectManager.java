package com.thibsworkshop.voxand.entities;

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

    private final Map<TexturedModel,List<Entity>> entities = new HashMap<>(50);
    private final Map<TexturedModel,List<TileEntity>> tileEntities = new HashMap<>(50);

    private final Map<Vector2i,List<Entity>> entitiesInChunk = new HashMap<>();


    private final Map<TexturedModel,List<Entity>> entitiesToRender = new HashMap<>(50);
    private final Map<TexturedModel,List<TileEntity>> tileEntitiesToRender = new HashMap<>(50);

    public static GameObjectManager main;

    public GameObjectManager(){
        MasterRenderer.gameObjectRenderer.linkManager(this);
        MasterRenderer.lineRenderer.linkGameObjectManager(this);
        main = this;
    }

    public void update(){

        collisionUpdate();

        updateEntities();

        updateTileEntities();
    }

    private final Vector2i temp = new Vector2i(0);
    private void collisionUpdate(){
        for (Map.Entry<Vector2i, List<Entity>> entry : entitiesInChunk.entrySet()) {
            List<Entity> batch = entry.getValue();
            for (int i = 0; i < batch.size(); i++) {
                Entity entityA = batch.get(i);

                if (entityA.entityCollisionsDone)
                    continue;

                for (int j = i + 1; j < batch.size(); j++) { //Collisions in the current chunk
                    Entity entityB = batch.get(j);
                    if (entityB.doEntityCollisions && !entityB.entityCollisionsDone) {
                        CollisionEngine.entityVSentity(entityA, entityB);
                    }
                }
                temp.set(entityA.transform.chunkPos);

                temp.sub(1, 0); //Collisions left
                loopCollisions(entityA);

                temp.add(0, 1); //Collisions top left
                loopCollisions(entityA);

                temp.add(1, 0); //Collisions top
                loopCollisions(entityA);

                temp.add(1, 0); //Collisions top right
                loopCollisions(entityA);

                temp.sub(0, 1); //Collisions right
                loopCollisions(entityA);

                temp.sub(0, 1); //Collisions bottom right
                loopCollisions(entityA);

                temp.sub(1, 0); //Collisions bottom
                loopCollisions(entityA);

                temp.sub(1, 0); //Collisions bottom left
                loopCollisions(entityA);

                entityA.entityCollisionsDone = true;
            }
        }
    }

    private void loopCollisions(Entity entityA){
        List<Entity> batchTemp = entitiesInChunk.get(temp);
        if (batchTemp != null) {
            for (Entity entityB : batchTemp) {
                if (entityB.doEntityCollisions && !entityB.entityCollisionsDone) {
                    if(entityA == entityB){
                        System.err.println("BIG PROBLEM: ENTITY IS COLLIDING WITH ITSELF (multiple reference to this entity");
                    }
                    CollisionEngine.entityVSentity(entityA, entityB);
                }
            }
        }
    }

    private void updateEntities(){
        for(TexturedModel texturedModel : entities.keySet()){
            List<Entity> batch = entities.get(texturedModel);
            List<Entity> renderBatch = entitiesToRender.get(texturedModel);
            for (Entity entity : batch) {
                entity.entityCollisionsDone = false;
                if(entity.doUpdate){
                    temp.set(entity.transform.chunkPos);       //Setting the previous chunkPos before the update
                    entity.update();                           //Update the entity only if it is enabled
                    if(entity.transform.hasChunkPosChanged()){ //If the chunk position changed, move the entity from list
                        System.out.println("CHUNK CHANGED " + entity.rigidbody.mass);
                        Vector2i chunkPos = entity.transform.chunkPos;
                        List<Entity> e = entitiesInChunk.get(temp);     //List where the entity was
                        if(e != null)                                   //If the list is not null
                            e.remove(entity);                           //Remove its previous reference from it
                        e = entitiesInChunk.get(chunkPos);              //Get the new list
                        if(e == null) {                                 //Create it if it doesn't exist yet
                            entitiesInChunk.put(new Vector2i(chunkPos), new ArrayList<>());
                            e = entitiesInChunk.get(chunkPos);
                        }
                        e.add(entity); //New reference
                    }
                    entity.lateUpdate(); //TODO: Probably move this later in code
                }

                boolean alreadyInBatch = renderBatch.contains(entity);
                if (entity.doRendering && entity.transform.chunk != null && entity.transform.chunk.getSqr_distance() <= Config.sqr_entityViewDist) {
                    if (!alreadyInBatch) {
                        renderBatch.add(entity);
                        System.out.println("ADDING ENTITY TO RENDER LIST");
                    }
                } else {
                    if (alreadyInBatch) renderBatch.remove(entity);
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

    public void processEntity(Entity entity) {
        TexturedModel entityTexturedModel = entity.getModel();
        List<Entity> batch = entities.get(entityTexturedModel);
        if(batch == null) {
            List<Entity> newBatch = new ArrayList<>(10);
            List<Entity> newRenderBatch = new ArrayList<>(10);
            newBatch.add(entity);
            entities.put(entityTexturedModel,newBatch);
            entitiesToRender.put(entityTexturedModel,newRenderBatch);
        }else {
            batch.add(entity);
        }

        Vector2i chunkPos = entity.transform.chunkPos;
        batch = entitiesInChunk.get(chunkPos);
        if(batch == null) {
            entitiesInChunk.put(chunkPos, new ArrayList<>());
            batch = entitiesInChunk.get(chunkPos);
        }
        batch.add(entity); //New reference
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

    public void removeEntity(Entity entity){
        entities.get(entity.texturedModel).remove(entity);
        entitiesToRender.get(entity.texturedModel).remove(entity);
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

    public Map<TexturedModel,List<Entity>> getEntitiesToRender(){ return entitiesToRender; }

}
