package com.thibsworkshop.voxand.rendering.shaders;

public class SkyboxShader extends ShaderProgram{
    public SkyboxShader(String vertexFile, String fragmentFile) {
        super(vertexFile, fragmentFile, 3);
    }

    @Override
    protected void getAllUniformLocations() {

    }

    @Override
    protected void bindAttributes() {

    }
}
