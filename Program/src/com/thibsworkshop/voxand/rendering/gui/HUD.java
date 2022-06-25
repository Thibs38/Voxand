package com.thibsworkshop.voxand.rendering.gui;

import com.thibsworkshop.voxand.io.Window;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Vector3f;

public class HUD {

    private static WireframeModel crosshair;

    public static void init(){
        crosshair = new WireframeModel(Color.white);
    }
    public static WireframeModel getCrosshair(){ return crosshair;}
}
