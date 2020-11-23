package com.thibsworkshop.voxand.shaders;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class LineShader extends ShaderProgram {

    private int location_projectionViewMatrix;

    private static final String VERTEX_FILE = "res/shaders/lineVertexShader";
    private static final String FRAGMENT_FILE = "res/shaders/lineFragmentShader";


    public LineShader() {
        super(VERTEX_FILE, FRAGMENT_FILE,2);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionViewMatrix = super.getUniformLocation("projectionViewMatrix");
    }


    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "color");
    }


    public void loadRenderingVariables(Matrix4f projectionView){
        super.loadMatrix(location_projectionViewMatrix,projectionView);
    }


}
