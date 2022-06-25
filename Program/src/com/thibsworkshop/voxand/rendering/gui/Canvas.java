package com.thibsworkshop.voxand.rendering.gui;

import com.thibsworkshop.voxand.entities.OrthographicCamera;
import org.joml.Matrix4f;

public class Canvas {

    public int height;
    public int width;
    OrthographicCamera camera;

    public Canvas(int width, int height, OrthographicCamera camera){
        this.height = height;
        this.width = width;
        this.camera = camera;

    }
}
