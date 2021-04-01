package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.rendering.models.TexturedModel;

//Parent class of Entity and TileEntity
public abstract class GameObject {

    public Transform transform;
    protected TexturedModel texturedModel;

    public GameObject(Transform transform, TexturedModel texturedModel){
        this.transform = transform;
        this.texturedModel = texturedModel;
    }

    public abstract void update();

    public TexturedModel getModel() {
        return texturedModel;
    }

}
