package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.entities.*;
import com.thibsworkshop.voxand.io.Window;
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
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import javax.imageio.plugins.tiff.TIFFImageReadParam;

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

        renderBlock();
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

    public void renderBlock(){
        if(!Player.player.blockSelected)
            return;
        Vector3f pos = new Vector3f(Player.player.selectedBlock);

        Chunk.shiftPositionFromCamera(pos,Player.player.selectedBlockChunkPos, Player.player.camera.transform.chunkPos);
        Matrix4f transformation = Maths.createTransformationMatrix(pos,1);
        lineShader.loadTransformation(transformation);
        WireframeModel model = Chunk.blockWireframeModel;
        lineShader.loadColor(model.color);
        prepareModel(model.getRawModel());
        GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);

    }

    public void renderXYZ(){
        projectionCamera.forward.add(projectionCamera.transform.getPosition(),Debug.axesPosition);
        Matrix4f transformation = Maths.createTransformationMatrix(Debug.axesPosition,0.1f);
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
            //FIXME: Rendering broken when moving
            if(!ray.preview) // This is a temporary fix to not display ray when not in free look
                continue;
            // The model is not center, it goes form (0,0,0) to (0,0,1)
            // So only scaling is necessary
            Vector3f pos = new Vector3f(ray.position);
            Vector3f vecA = new Vector3f(0.0f, 0.0f, 1.0f);
            Vector3f vecB = new Vector3f(ray.direction);

            float angle = (float) Math.acos(vecA.dot(vecB));

            Vector3f rotationAxis = new Vector3f();
            vecA.cross(vecB, rotationAxis);
            rotationAxis.normalize();

            Vector3f scale = new Vector3f(ray.length);

            Chunk.shiftPositionFromCamera(pos, ray.chunkPosition, Player.player.camera.transform.chunkPos);
            //System.out.println("scale: " + scale + " angle: " + angle);
            //System.out.print("rotation: ");
            //Debug.printVector(rotationAxis);
            Matrix4f transformation = Maths.createTransformationMatrix(pos,scale,rotationAxis, angle);
            //System.out.println(transformation);
            lineShader.loadTransformation(transformation);
            GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }

        model = Debug.getRayHitCrossModel();
        lineShader.loadColor(model.color);
        prepareModel(model.getRawModel());
        for(Ray ray: Ray.rays){
            if(!ray.hit.success)
                continue;
            Vector3f pos = new Vector3f(ray.direction);
            pos.mul(-0.01f); // To avoid weird clipping issues
            pos.add(ray.hit.position);
            Chunk.shiftPositionFromCamera(pos, ray.hit.chunkPosition,Player.player.camera.transform.chunkPos);

            //The model is aligned on the forward axis
            Vector3f forward = new Vector3f(Maths.forward);
            forward.x += 0.0001f;
            Vector3f normal = new Vector3f(ray.hit.normal);
            float angle = (float) Math.acos( forward.dot(normal));

            Vector3f rotationAxis = new Vector3f();
            forward.cross(normal, rotationAxis);
            rotationAxis.normalize();
            //System.out.println("angle: " + angle);
            Matrix4f transformation = Maths.createTransformationMatrix(pos,Maths.quarter,rotationAxis,angle);
            lineShader.loadTransformation(transformation);
            GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        }

    }

    public void renderHUD(){
        Vector3f center = new Vector3f(Window.mainWindow.getWidth() / 2,Window.mainWindow.getHeight() / 2,-1);
        //Vector3f center = new Vector3f(0,0,0);
        Matrix4f transformation = Maths.createTransformationMatrix(center,20);
        lineShader.loadTransformation(transformation);
        WireframeModel model = HUD.getCrosshair();
        lineShader.loadColor(model.color);
        prepareModel(model.getRawModel());
        GL11.glDrawElements(GL11.GL_LINES, model.getRawModel().getVertexCount(),GL11.GL_UNSIGNED_INT,0);

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
