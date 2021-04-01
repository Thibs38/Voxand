package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.rendering.shaders.LineShader;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;


public class LineRenderer extends Renderer{

    LineShader shader;

    TerrainManager terrainManager;

    GameObjectManager gameObjectManager;

    public LineRenderer(LineShader shader){
        super(shader);
        this.shader = shader;
    }

    public void linkTerrainManager(TerrainManager terrainManager){ this.terrainManager = terrainManager; }

    public void linkGameObjectManager(GameObjectManager gameObjectManager){ this.gameObjectManager = gameObjectManager; }

    @Override
    public void render(Camera camera) {
        if(Debug.isChunkAABB())
            renderChunks();
        if (Debug.isEntityAABB())
            renderEntitiesCollider();
        if (Debug.isTileEntityAABB())
            renderTileEntitiesCollider();


        unbindModel();
    }

    public void renderXYZ(Camera camera){
        camera.forward.add(camera.transform.getPosition(),Debug.axesPosition);
        shader.loadTransformation(Debug.axesPosition,Maths.quarter);
        for(WireframeModel model: Debug.getAxisModels()){
            shader.loadColor(model.color);
            prepareModel(model.getRawModel());
            GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }
        unbindModel();
    }

    private void renderChunks(){
        RawModel rawModel = Chunk.wireModel.getRawModel();
        prepareModel(rawModel);
        for(Chunk chunk: TerrainManager.chunks.values()){
            if(chunk.generated)
                shader.loadColor(Chunk.wireModel.color);
            else
                shader.loadColor(Color.red);
            shader.loadTransformation(chunk.getPosition(),Chunk.CHUNK_SCALE);
            GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }
    }

    private void renderEntitiesCollider(){
        gameObjectManager.getEntitiesToRender().forEach((k,v) -> {
            WireframeModel wireframe = k.collider.getWireframeModel();
            shader.loadColor(wireframe.color);
            RawModel rawModel = wireframe.getRawModel();
            prepareModel(rawModel);
            for(Entity entity : v) {
                shader.loadTransformation(entity.transform.getPosition(),entity.transform.getScale());
                GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
            }
        });
    }

    private void renderTileEntitiesCollider(){
        gameObjectManager.getTileEntitiesToRender().forEach((k,v) -> {
            WireframeModel wireframe = k.collider.getWireframeModel();
            shader.loadColor(wireframe.color);
            RawModel rawModel = wireframe.getRawModel();
            prepareModel(rawModel);
            for(TileEntity entity : v) {
                shader.loadTransformation(entity.transform.getPosition(),entity.transform.getScale());
                GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
            }
        });
    }

    private void prepareModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());
    }

}
