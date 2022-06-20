package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Window;
import org.joml.Matrix4f;

public class OrthographicCamera extends Camera{

    private float zoom;

    public OrthographicCamera(float zoom) {
        super();
        this.zoom = zoom;
        projectionType = ProjectionType.ORTHOGRAPHIC;
        updateProjectionMatrix();
        updateViewMatrix();
    }

    @Override
    public void updateViewMatrix() {
        viewMatrix.identity();
    }

    @Override
    public void updateProjectionMatrix() {
        projectionMatrix.setOrtho(0,Window.mainWindow.getWidth(),0,Window.mainWindow.getHeight(), NEAR_PLANE,FAR_PLANE);
        projectionMatrix.mul(viewMatrix,projectionViewMatrix);
    }

    public void setZoom(float zoom){ this.zoom = zoom; }
    public float getZoom(){ return zoom; }
}
