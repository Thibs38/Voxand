package com.thibsworkshop.voxand.rendering.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LineShader extends ShaderProgram {

    private int location_projectionViewMatrix;
    private int location_color;
    private int location_linePosition;
    private int location_lineScale;

    private static final String VERTEX_FILE = "lineVertexShader.vert";
    private static final String FRAGMENT_FILE = "lineFragmentShader.frag";


    public LineShader() {
        super(VERTEX_FILE, FRAGMENT_FILE,1);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionViewMatrix = super.getUniformLocation("projectionViewMatrix");
        location_color = super.getUniformLocation("color");
        location_linePosition = super.getUniformLocation("linePosition");
        location_lineScale = super.getUniformLocation("lineScale");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadRenderingVariables(Matrix4f projectionView){
        super.loadMatrix(location_projectionViewMatrix,projectionView);
    }

    public void loadColor(Vector3f color){
        super.loadVector(location_color,color);
    }

    public void loadTransformation(Vector3f position, Vector3f scale){
        super.loadVector(location_linePosition, position);
        super.loadVector(location_lineScale, scale);
    }


}
