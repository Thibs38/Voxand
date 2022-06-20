package com.thibsworkshop.voxand.debugging;


import com.thibsworkshop.voxand.entities.GameObjectManager;
import com.thibsworkshop.voxand.rendering.models.WireframeModel;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.toolbox.Color;
import com.thibsworkshop.voxand.toolbox.Maths;
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

    private static WireframeModel[] axisModels;

    public static Vector3f axesPosition = new Vector3f();

    public static boolean isDebugMode(){
        return debugMode;
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
            timing = false;
            chunkAABB = false;
            entityAABB = false;
            tileEntityAABB = false;
        }
    }

    public static WireframeModel[] getAxisModels(){ return axisModels; }

    private static void printWarningDebugModeNotEnabled(String toolName){
        System.out.println("WARNING: Debug mode is not enabled, can't enabled " + toolName);
    }

    private static void printWarningDebugToolEnabled(String toolName){
        System.out.println("WARNING: " + toolName + " is enabled, you may notice performance impact");
    }

    public static boolean isTiming() {
        return timing;
    }

    public static void setTiming(boolean timing) {
        if(debugMode){
            if(!Debug.timing && timing)
                printWarningDebugToolEnabled("Timing");
            Debug.timing = timing;
        }
        else
            printWarningDebugModeNotEnabled("Timing");
    }

    public static boolean isChunkAABB() {
        return chunkAABB;
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

    public static boolean isEntityAABB() {
        return entityAABB;
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

    public static boolean isTileEntityAABB() {
        return tileEntityAABB;
    }

    public static void setTileEntityAABB(boolean tileEntityAABB) {
        if(debugMode){
            if(!Debug.tileEntityAABB && tileEntityAABB) {
                printWarningDebugToolEnabled("Tile Entity Collider Wireframe");
                GameObjectManager.main.genTileEntityWireframe();
            }
            Debug.tileEntityAABB = tileEntityAABB;
        }
        else
            printWarningDebugModeNotEnabled("Tile Entity Collider Wireframe");
    }

    public static void clear(){
        System.out.println("WARNING: Clearing debugging models, disabling visualization");
        setTileEntityAABB(false);
        setEntityAABB(false);
        setChunkAABB(false);
        Chunk.destroyWireframe();
        GameObjectManager.main.destroyWireframes();
        axisModels = null;
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
}
