package com.thibsworkshop.voxand.rendering.gui;

import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;

public class HUD {

    private static WireframeModel[] crosshair;

    public static void init(){
        crosshair = new WireframeModel[2];
        crosshair[0] = new WireframeModel(Maths.left,Maths.right, Color.white);
        crosshair[1] = new WireframeModel(Maths.down,Maths.up, Color.white);
    }
    public static WireframeModel[] getCrosshair(){ return crosshair;}
}
