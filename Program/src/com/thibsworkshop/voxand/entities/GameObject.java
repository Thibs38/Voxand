package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.rendering.models.TexturedModel;

//Parent class of Entity and TileEntity
public abstract class GameObject {

    public Transform transform;
    protected TexturedModel texturedModel;

    public GameObject(TexturedModel texturedModel){
        this.transform = new Transform();
        this.texturedModel = texturedModel;
    }

    public abstract void update();

    public abstract void lateUpdate();

    public TexturedModel getModel() {
        return texturedModel;
    }

}
