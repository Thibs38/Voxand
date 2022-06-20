package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.rendering.gui.HUD;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.rendering.shaders.LineShader;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;


public class LineRenderer extends Renderer{

    private final LineShader lineShader;
    private final ProjectionCamera projectionCamera;
    private final OrthographicCamera orthographicCamera;

    GameObjectManager gameObjectManager;



    public LineRenderer(LineShader lineShader, ProjectionCamera projectionCamera, OrthographicCamera orthographicCamera){
        super(lineShader);
        this.lineShader = lineShader;
        this.projectionCamera = projectionCamera;
        this.orthographicCamera = orthographicCamera;
    }

    public void linkGameObjectManager(GameObjectManager gameObjectManager){ this.gameObjectManager = gameObjectManager; }

    // WARNING: Clearing depth buffer here
    @Override
    public void render() {
        lineShader.start();
        lineShader.loadRenderingVariables(projectionCamera.getProjectionViewMatrix());

        if(Debug.isDebugMode()){

            if(Debug.isChunkAABB())
                renderChunks();
            if (Debug.isEntityAABB())
                renderEntitiesCollider();
            if (Debug.isTileEntityAABB())
                renderTileEntitiesCollider();
        }

        glClear(GL_DEPTH_BUFFER_BIT); //Clear the depth buffer

        if(Debug.isDebugMode()){
            renderXYZ();
        }
        lineShader.loadRenderingVariables(orthographicCamera.getProjectionViewMatrix());
        renderHUD();

        unbindModel();
        lineShader.stop();
    }

    public void renderXYZ(){
        projectionCamera.forward.add(projectionCamera.transform.getPosition(),Debug.axesPosition);
        lineShader.loadTransformation(Debug.axesPosition,Maths.quarter);
        for(WireframeModel model: Debug.getAxisModels()){
            lineShader.loadColor(model.color);
            prepareModel(model.getRawModel());
            GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }
    }

    public void renderHUD(){
        lineShader.loadTransformation(Maths.one,Maths.one);
        for(WireframeModel model: HUD.getCrosshair()){
            lineShader.loadColor(model.color);
            prepareModel(model.getRawModel());
            GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }
    }

    private void renderChunks(){
        RawModel rawModel = Chunk.wireModel.getRawModel();
        prepareModel(rawModel);
        for(Chunk chunk: TerrainManager.chunks.values()){
            if(chunk.generated)
                lineShader.loadColor(Chunk.wireModel.color);
            else
                lineShader.loadColor(Color.red);
            lineShader.loadTransformation(chunk.getPosition(),Chunk.CHUNK_SCALE);
            GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }
    }

    private void renderEntitiesCollider(){
        gameObjectManager.getEntitiesToRender().forEach((k,v) -> {
            WireframeModel wireframe = k.collider.getWireframeModel();
            lineShader.loadColor(wireframe.color);
            RawModel rawModel = wireframe.getRawModel();
            prepareModel(rawModel);
            for(GameEntity gameEntity : v) {
                loadTransformation(gameEntity);
                GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
            }
        });

        WireframeModel wireframe = Player.player.getModel().collider.getWireframeModel();
        lineShader.loadColor(wireframe.color);
        RawModel rawModel = wireframe.getRawModel();
        prepareModel(rawModel);
        lineShader.loadTransformation(Player.player.transform.getPosition(),Player.player.transform.getScale());
        GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
    }

    private void renderTileEntitiesCollider(){
        gameObjectManager.getTileEntitiesToRender().forEach((k,v) -> {
            WireframeModel wireframe = k.collider.getWireframeModel();
            lineShader.loadColor(wireframe.color);
            RawModel rawModel = wireframe.getRawModel();
            prepareModel(rawModel);
            for(TileEntity entity : v) {
                lineShader.loadTransformation(entity.transform.getPosition(),entity.transform.getScale());
                GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
            }
        });
    }

    private final Vector3f tempPos = new Vector3f();
    private void loadTransformation(GameObject object){
        tempPos.set(object.transform.getPosition());
        Chunk.shiftPositionFromPlayer(tempPos, object.transform.chunkPos, Player.player.transform.chunkPos);
        lineShader.loadTransformation(tempPos, object.transform.getScale());
    }

    private void prepareModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());
    }

}
