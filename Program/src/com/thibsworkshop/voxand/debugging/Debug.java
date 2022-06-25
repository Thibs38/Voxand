package com.thibsworkshop.voxand.debugging;


import com.thibsworkshop.voxand.entities.GameObjectManager;
import com.thibsworkshop.voxand.entities.Player;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

// A class to manage debugging tools
public class Debug {

    private static boolean debugMode = false;
    private static boolean timing = false;
    private static boolean chunkAABB = false;
    private static boolean entityAABB = false;
    private static boolean tileEntityAABB = false;
    private static boolean rays = false;
    private static boolean cameraFree = false;

    private static WireframeModel[] axisModels;
    private static WireframeModel rayModel;
    private static WireframeModel rayHitCrossModel;

    public static Vector3f axesPosition = new Vector3f();




    private static void genRayModel(){
        rayModel = new WireframeModel(Maths.zero,Maths.forward,Color.red);
        rayHitCrossModel = new WireframeModel(Color.red);
    }


    public static WireframeModel[] getAxisModels(){ return axisModels; }

    public static WireframeModel getRayModel(){ return rayModel; }

    public static WireframeModel getRayHitCrossModel(){ return rayHitCrossModel; }




    //<editor-fold desc="Getters">
    public static boolean isTiming() {
        return timing;
    }
    public static boolean isChunkAABB() {
        return chunkAABB;
    }
    public static boolean isEntityAABB() {
        return entityAABB;
    }
    public static boolean isTileEntityAABB() {
        return tileEntityAABB;
    }
    public static boolean isRays() { return rays; }
    public static boolean isCameraFree(){ return cameraFree; }
    public static boolean isDebugMode(){
        return debugMode;
    }



    //</editor-fold>

    //<editor-fold desc="Setters">

    public static void setTiming(boolean timing) {
        if(debugMode){
            if(!Debug.timing && timing)
                printWarningDebugToolEnabled("Timing");
            Debug.timing = timing;
        }
        else
            printWarningDebugModeNotEnabled("Timing");
    }

    public static void setChunkAABB(boolean chunkAABB) {
        if(debugMode){
            if(!Debug.chunkAABB && chunkAABB){
                printWarningDebugToolEnabled("Chunk Border Wireframe");
                Chunk.genWireframe();
            }
            Debug.chunkAABB = chunkAABB;
        }
        else
            printWarningDebugModeNotEnabled("Chunk Border Wireframe");
    }

    public static void setEntityAABB(boolean entityAABB) {
        if(debugMode){
            if(!Debug.entityAABB && entityAABB) {
                printWarningDebugToolEnabled("Entity Collider Wireframe");
                GameObjectManager.main.genEntityWireframe();
            }
            Debug.entityAABB = entityAABB;
        }
        else
            printWarningDebugModeNotEnabled("Entity Collider Wireframe");
    }

    public static void setTileEntityAABB(boolean tileEntityAABB) {
        if(debugMode){
            if(!Debug.tileEntityAABB && tileEntityAABB) {
                printWarningDebugToolEnabled("Tile Entity Collider Wireframe");
                GameObjectManager.main.genTileEntityWireframe(); //TODO check if null before generating
            }
            Debug.tileEntityAABB = tileEntityAABB;
        }
        else
            printWarningDebugModeNotEnabled("Tile Entity Collider Wireframe");
    }

    public static void setRays(boolean rays){
        if(debugMode){
            if(!Debug.rays && rays) {
                printWarningDebugToolEnabled("Rays Wireframe");
                if(rayModel == null)
                    genRayModel();
            }
            Debug.rays = rays;
        }
        else
            printWarningDebugModeNotEnabled("Rays Wireframe");
    }

    public static void setCameraFree(boolean cameraFree){
        if(debugMode){
            if(!Debug.cameraFree && cameraFree) {
                printWarningDebugToolEnabled("Free look");
            }
            Debug.cameraFree = cameraFree;
            if(cameraFree)
                Player.player.setMode(Player.Mode.Spectator);
            else
                Player.player.setMode(Player.Mode.Survival);

        }
        else
            printWarningDebugModeNotEnabled("Rays Wireframe");
    }

    public static void setDebugMode(boolean debugMode){
        Debug.debugMode = debugMode;
        if(debugMode){
            printWarningDebugToolEnabled("Debug Mode");
            if(axisModels == null){
                axisModels = new WireframeModel[3];
                axisModels[0] = new WireframeModel(Maths.zero,Maths.right, Color.red);
                axisModels[1] = new WireframeModel(Maths.zero,Maths.up, Color.green);
                axisModels[2] = new WireframeModel(Maths.zero,Maths.forward, Color.blue);
            }

        }else{
            setTiming(false);
            setChunkAABB(false);
            setEntityAABB(false);
            setTileEntityAABB(false);
            setRays(false);
            setCameraFree(false);

        }
    }

    //</editor-fold>


    public static void clear(){
        System.out.println("WARNING: Clearing debugging models, disabling visualization");
        setTileEntityAABB(false);
        setEntityAABB(false);
        setChunkAABB(false);
        Chunk.destroyWireframe();
        GameObjectManager.main.destroyWireframes();
        axisModels = null;
    }

    //<editor-fold desc="Printers">
    private static void printWarningDebugModeNotEnabled(String toolName){
        System.out.println("WARNING: Debug mode is not enabled, can't enabled " + toolName);
    }

    private static void printWarningDebugToolEnabled(String toolName){
        System.out.println("WARNING: " + toolName + " is enabled, you may notice performance impact");
    }
    public static void printVector(Vector3f v){
        System.out.printf("(%.2f, %.2f, %2f)\n", v.x, v.y, v.z);
    }
    public static void printVector(Vector2f v){
        System.out.printf("(%.2f, %.2f)\n", v.x, v.y);
    }

    public static void printVector(Vector2i v){
        System.out.printf("(%d, %d)\n", v.x, v.y);
    }

    //</editor-fold>
}
