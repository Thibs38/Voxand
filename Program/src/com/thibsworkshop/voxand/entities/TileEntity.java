package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.rendering.models.TexturedModel;

public class TileEntity extends GameObject {

    boolean hasCollider;
    public TileEntity(TexturedModel texturedModel) {
        super(texturedModel);
        hasCollider = texturedModel.collider != null;
    }


    @Override
    public void update(){
    }

    @Override
    public void lateUpdate(){}
}
