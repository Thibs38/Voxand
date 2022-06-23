package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.physics.collisions.Ray;
import com.thibsworkshop.voxand.rendering.gui.HUD;
import com.thibsworkshop.voxand.rendering.models.RawModel;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.rendering.shaders.LineShader;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Matrix4f;
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
            if(Debug.isRays())
                renderRays();
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
        Matrix4f transformation = Maths.createTransformationMatrix(Debug.axesPosition,0.25f);
        lineShader.loadTransformation(transformation);
        for(WireframeModel model: Debug.getAxisModels()){
            lineShader.loadColor(model.color);
            prepareModel(model.getRawModel());
            GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }
    }

    public void renderRays(){
        WireframeModel model = Debug.getRayModel();
        lineShader.loadColor(model.color);
        prepareModel(model.getRawModel());
        for(Ray ray: Ray.rays){
            Vector3f endPoint = new Vector3f();
            ray.direction.mul(ray.length, endPoint);
            endPoint.add(ray.position);
            Vector3f midPoint = new Vector3f(ray.position);
            midPoint.add(endPoint);
            midPoint.mul(0.5f);

            Vector3f vecA = new Vector3f(0.0f, 0.0f, 1.0f);
            Vector3f vecB = new Vector3f(ray.direction);

            float angle = (float) Math.acos(vecA.dot(vecB));

            Vector3f rotationAxis = new Vector3f();
            vecA.cross(vecB, rotationAxis);
            rotationAxis.normalize();

            Vector3f scale = new Vector3f(ray.length);


            Chunk.shiftPositionFromCamera(midPoint, ray.chunkPosition, Player.player.camera.transform.chunkPos);
            //System.out.println("scale: " + scale + " angle: " + angle);
            //System.out.print("rotation: ");
            //Debug.printVector(rotationAxis);
            Matrix4f transformation = Maths.createTransformationMatrix(midPoint,scale,rotationAxis, angle);
            //System.out.println(transformation);
            lineShader.loadTransformation(transformation);
            GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }
    }

    public void renderHUD(){
        Matrix4f transformation = Maths.identity;
        lineShader.loadTransformation(transformation);
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
            Matrix4f transformation = Maths.createTransformationMatrix(chunk.getPosition(),Chunk.CHUNK_SCALE);
            lineShader.loadTransformation(transformation);
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
        tempPos.set(Player.player.transform.getPosition());
        Chunk.shiftPositionFromCamera(tempPos,Player.player.transform.chunkPos,Player.player.camera.transform.chunkPos);
        Matrix4f transformation = Maths.createTransformationMatrix(tempPos,Player.player.transform.getScale());

        lineShader.loadTransformation(transformation);
        GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
    }

    private void renderTileEntitiesCollider(){
        gameObjectManager.getTileEntitiesToRender().forEach((k,v) -> {
            WireframeModel wireframe = k.collider.getWireframeModel();
            lineShader.loadColor(wireframe.color);
            RawModel rawModel = wireframe.getRawModel();
            prepareModel(rawModel);
            for(TileEntity tileEntity : v) {
                loadTransformation(tileEntity);
                GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
            }
        });
    }

    private final Vector3f tempPos = new Vector3f();
    private void loadTransformation(GameObject object){
        tempPos.set(object.transform.getPosition());
        Chunk.shiftPositionFromCamera(tempPos, object.transform.chunkPos, Player.player.camera.transform.chunkPos);
        Matrix4f transformation = Maths.createTransformationMatrix(tempPos, object.transform.getScale());
        lineShader.loadTransformation(transformation);
    }

    private void prepareModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());
    }

}
