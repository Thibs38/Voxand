package com.thibsworkshop.voxand.entities;

import com.thibsworkshop.voxand.models.TexturedModel;
import com.thibsworkshop.voxand.physics.Collider;

public class TileEntity extends GameObject {

    Collider collider;
    public TileEntity(Transform transform, TexturedModel texturedModel, Collider collider) {
        super(transform, texturedModel);
        this.collider = collider;
    }

    public TileEntity(Transform transform, TexturedModel texturedModel) {
        super(transform, texturedModel);
        this.collider = null;
    }

    @Override
    public void update(){

    }
}
