package com.thibsworkshop.voxand.gui;

import org.joml.Matrix4f;

public class Canvas {

    public int height;
    public int width;
    Matrix4f orthoMatrix;

    public Canvas(int width, int height){
        this.height = height;
        this.width = width;
        orthoMatrix = new Matrix4f().setOrtho(0,width,0,height,0.01f,1000f);

    }
}
