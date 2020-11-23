package com.thibsworkshop.voxand.rendering;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.models.RawModel;
import com.thibsworkshop.voxand.shaders.LineShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

//TODO: add transformation matrix

public class LineRenderer extends Renderer{

    LineShader shader;

    RawModel rawModel;

    public LineRenderer(LineShader shader){
        super(shader);
        this.shader = shader;
    }
    @Override
    public void render(Camera camera) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, rawModel.getIboID());
        GL11.glDrawElements(GL11.GL_LINES, rawModel.getVertexCount(),GL11.GL_UNSIGNED_INT,0);
        unbindModel();
    }

    public void setRawModel(RawModel rawModel){
        this.rawModel = rawModel;
    }
}
