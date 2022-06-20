package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ProjectionCamera extends Camera{

    public final FrustumIntersection frustumIntersection;

    private float fov;

    private float aspectRatio;


    public ProjectionCamera(float fov){
        super();
        this.fov = fov;
        this.projectionType = Camera.ProjectionType.PERSPECTIVE;
        this.aspectRatio = Window.mainWindow.getAspectRatio();
        this.frustumIntersection = new FrustumIntersection();
        updateProjectionMatrix();
        updateViewMatrix();

    }

    @Override
    public void updateViewMatrix() {
        viewMatrix.identity();
        Vector3f rotation = transform.getRotation();

        viewMatrix.rotate((float) Math.toRadians(rotation.x), Maths.right);
        viewMatrix.rotate((float) Math.toRadians(rotation.y), Maths.up);
        viewMatrix.rotate((float) Math.toRadians(rotation.z), Maths.forward);

        Vector3f cameraPos = transform.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos);

        projectionMatrix.mul(viewMatrix,projectionViewMatrix);
        frustumIntersection.set(projectionViewMatrix);
        viewMatrix.positiveZ(forward).negate();
    }

    @Override
    public void updateProjectionMatrix() {
        this.aspectRatio = Window.mainWindow.getAspectRatio();
        projectionMatrix.identity();
        projectionMatrix.setPerspective(fov,aspectRatio,NEAR_PLANE,FAR_PLANE);
    }

    public void update(Transform attachedTransform){
        transform.setPosition(attachedTransform.getPosition());//We apply the final translation to the camera
        transform.translate(0,1.5f,0);
        transform.update();
        updateViewMatrix();
    }

    public void setFOV(float fov){
        this.fov = fov;
        updateProjectionMatrix();
    }

    //<editor-fold desc="Getters">

    public float getFOV() { return fov; }


    public float getAspectRatio() {
        return aspectRatio;
    }


    //</editor-fold>
}
