package com.thibsworkshop.voxand.debugging;


//TODO: add a method to destroy the wireframe, and more generally destroy every debug tool.

import com.thibsworkshop.voxand.entities.GameObjectManager;
import com.thibsworkshop.voxand.terrain.Chunk;
import com.thibsworkshop.voxand.terrain.TerrainManager;

// A class to manage debugging tools
public class Debug {

    private static boolean debugMode = false;
    private static boolean timing = false;
    private static boolean chunkAABB = false;
    private static boolean entityAABB = false;
    private static boolean tileEntityAABB = false;

    public static boolean isDebugMode(){
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode){
        Debug.debugMode = debugMode;
        if(debugMode){
            printWarningDebugToolEnabled("Debug Mode");
        }else{
            timing = false;
            chunkAABB = false;
            entityAABB = false;
            tileEntityAABB = false;
        }
    }

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
                GameObjectManager.genEntityWireframe();
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
                GameObjectManager.genTileEntityWireframe();
            }
            Debug.tileEntityAABB = tileEntityAABB;
        }
        else
            printWarningDebugModeNotEnabled("Tile Entity Collider Wireframe");
    }

    public static void clearWireframeModels(){
        System.out.println("WARNING: Clearing debugging models, disabling visualization");
        setTileEntityAABB(false);
        setEntityAABB(false);
        setChunkAABB(false);
        Chunk.destroyWireframe();
        GameObjectManager.destroyWireframes();
    }
}
