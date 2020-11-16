package com.thibsworkshop.voxand.rendering;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.shaders.BasicShader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public abstract class Renderer {

    BasicShader shader;

    public Renderer(BasicShader shader){
        this.shader = shader;
    }

    public abstract void render(Camera camera);

    public void updateProjectionMatrix(Matrix4f projection) {
        shader.start();
        shader.loadProjectionMatrix(projection);
        shader.stop();
    }

    protected void unbindModel() {

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,0);
        GL30.glBindVertexArray(0);
    }
}
