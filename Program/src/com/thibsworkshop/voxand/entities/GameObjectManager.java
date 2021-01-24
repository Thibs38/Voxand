package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.game.Config;
import com.thibsworkshop.voxand.models.TexturedModel;
import com.thibsworkshop.voxand.rendering.MasterRenderer;
import com.thibsworkshop.voxand.terrain.TerrainManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//This class manage entities, it calls their update methods every frame, manage the tick update,
// manage adding and removing etc...
public class GameObjectManager {

    private static Map<TexturedModel,List<Entity>> entities = new HashMap<>();
    private static Map<TexturedModel,List<TileEntity>> tileEntities = new HashMap<>();

    private static Map<TexturedModel,List<Entity>> entitiesToRender = new HashMap<>();
    private static Map<TexturedModel,List<TileEntity>> tileEntitiesToRender = new HashMap<>();

    public GameObjectManager(){
        MasterRenderer.gameObjectRenderer.linkManager(this);
        MasterRenderer.lineRenderer.linkGameObjectManager(this);
    }

    public void update(){
        for(TexturedModel texturedModel : entities.keySet()){
            List<Entity> batch = entities.get(texturedModel);
            List<Entity> renderBatch = entitiesToRender.get(texturedModel);
            for (Entity entity : batch) {
                entity.update();
                boolean render = renderBatch.contains(entity);
                if (entity.transform.chunk != null && entity.transform.chunk.getSqr_distance() <= Config.sqr_entityViewDist) {
                    if (!render) {
                        renderBatch.add(entity);
                        //System.out.println("ADDING ENTITY TO RENDER LIST");
                    }
                } else {
                    if (render) renderBatch.remove(entity);
                }
            }
        }

        for(TexturedModel texturedModel : tileEntities.keySet()){
            List<TileEntity> batch = tileEntities.get(texturedModel);
            List<TileEntity> renderBatch = tileEntitiesToRender.get(texturedModel);
            for(int i = 0; i < batch.size(); i++){
                TileEntity tileEntity = batch.get(i);
                tileEntity.update();
                boolean render = renderBatch.contains(tileEntity);
                if(tileEntity.transform.chunk.getSqr_distance() <= Config.sqr_tileEntityViewDist){
                    if(!render) renderBatch.add(tileEntity);
                }else{
                    if(render) renderBatch.remove(i);
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

    public static void genEntityWireframe(){
        for(TexturedModel texturedModel : entities.keySet()) {
            if(texturedModel.collider != null)
                texturedModel.collider.createWireframe();
        }
    }

    public static void genTileEntityWireframe(){
        for(TexturedModel texturedModel : tileEntities.keySet()) {
            if(texturedModel.collider != null)
                texturedModel.collider.createWireframe();
        }
    }

    public static void destroyWireframes(){
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
