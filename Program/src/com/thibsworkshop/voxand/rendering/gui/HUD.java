package com.thibsworkshop.voxand.rendering.gui;

import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;

public class HUD {

    private static WireframeModel[] crosshair;

    public static void init(){
        crosshair = new WireframeModel[2];
        Vector3f center = new Vector3f(Window.mainWindow.getWidth() / 2,Window.mainWindow.getHeight() / 2,0);
        crosshair[0] = new WireframeModel(center.add(-100,0,0),center.add(100,0,0), Color.white);
        crosshair[1] = new WireframeModel(center.add(0,-100,0),center.add(0,100,0), Color.white);
    }
    public static WireframeModel[] getCrosshair(){ return crosshair;}
}
