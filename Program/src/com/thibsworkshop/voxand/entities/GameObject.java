package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.models.TexturedModel;
import com.thibsworkshop.voxand.terrain.Chunk;

//Parent class of Entity and TileEntity
public abstract class GameObject {

    public Transform transform;
    protected TexturedModel texturedModel;
    public Chunk chunk;

    public GameObject(Transform transform, TexturedModel texturedModel){
        this.transform = transform;
        this.texturedModel = texturedModel;
    }

    public abstract void update();

    public TexturedModel getModel() {
        return texturedModel;
    }

}
