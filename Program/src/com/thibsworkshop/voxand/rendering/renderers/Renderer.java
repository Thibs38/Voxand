package com.thibsworkshop.voxand.rendering.renderers;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.rendering.shaders.ShaderProgram;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public abstract class Renderer {

    ShaderProgram shader;
    public Renderer(ShaderProgram shader){
        this.shader = shader;
    }

    public abstract void render();

    protected void unbindModel() {

        for(int i = 0; i < shader.getAttributeCount(); i++){
            GL20.glDisableVertexAttribArray(i);
        }

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER,0);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        shader.cleanUp();
    }


}
