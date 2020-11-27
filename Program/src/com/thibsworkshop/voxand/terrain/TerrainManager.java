package com.thibsworkshop.voxand.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.thibsworkshop.voxand.debugging.Timing;
import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.rendering.MasterRenderer;
import com.thibsworkshop.voxand.terrain.TerrainGenerator.IndiceVerticeNormal;
import com.thibsworkshop.voxand.terrain.Terrain.TerrainInfo;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class TerrainManager {
	
	private static final int CHUNK_VIEW_DIST = 12;

	private static final int CHUNK_GENERATE_DIST = 15;

	private static final int CHUNK_LOAD_DIST = 13;

	private static final int CHUNK_UNLOAD_DIST = 18;

	public static Map<Vector2i,Terrain> terrains = new HashMap<Vector2i,Terrain>();

	private Map<Vector2i,Future<Terrain>> gridsInCreation = new HashMap<Vector2i, Future<Terrain>>();
	
	private Map<Vector2i,Callable<IndiceVerticeNormal>> terrainsToCreate = new HashMap<Vector2i, Callable<IndiceVerticeNormal>>();
	
	private Map<Vector2i,Callable<IndiceVerticeNormal>> terrainsWaitingToCreate = new HashMap<Vector2i, Callable<IndiceVerticeNormal>>();
	
	private Map<Vector2i, Future<IndiceVerticeNormal>> terrainsInCreation = new HashMap<Vector2i, Future<IndiceVerticeNormal>>();
	
	private Map<Vector2i,IndiceVerticeNormal> calculatedTerrains = new HashMap<Vector2i,IndiceVerticeNormal>(); //Calculated terrains waited to be created

	private List<Terrain> terrainsToRender = new ArrayList<Terrain>();
	private TerrainInfo terrainInfo;
	
	private boolean refresh = false; // refresh the chunk every two frames
	
	private Vector2f campos;
	
	ExecutorService executor;

	public static String debugName = "Terrain Generation";

	private long lastClean = 0;
	private long cleanGap = 5; // in seconds

	
	public TerrainManager(TerrainInfo terrainInfo) {
		this.terrainInfo = terrainInfo;
		MasterRenderer.terrainRenderer.linkManager(this);
		executor = new ThreadPoolExecutor(CHUNK_GENERATE_DIST*2,CHUNK_GENERATE_DIST*4*(CHUNK_GENERATE_DIST+1)+1,5,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());

		Timing.add(debugName,new String[]{
			"Refreshing",
			"Grid Generation",
			"Model Generation"
		});

		lastClean = Time.getFrameMilliTime();
	}
	
	public void refreshChunks() {

		refresh = !refresh;
		if(!refresh)
			return;

		//TODO: Refactor the generation management (too many lists and hashmaps)
		//TODO: incremental loading: load the chunk near the player and then those farther
		//TODO: divide the chunks into sub chunks of 32*32 and render them separately
		//TODO: refresh the chunks every 2 frames, but load them on separated frame,
		// load several empty chunks on the same frame to avoid freezes

		Timing.start(debugName,"Refreshing");

		Map<Vector2i,Callable<Terrain>> gridsToCreate = new HashMap<Vector2i,Callable<Terrain>>();
		
		int camposX = (int)(Camera.main.getPosition().x/(float)Terrain.CHUNK_SIZE);
		int camposZ = (int)(Camera.main.getPosition().z/(float)Terrain.CHUNK_SIZE);
		campos = new Vector2f(camposX,camposZ);

		for(int x = -CHUNK_GENERATE_DIST; x <= CHUNK_GENERATE_DIST; x++) {
			for(int z = -CHUNK_GENERATE_DIST; z <= CHUNK_GENERATE_DIST; z++) {
				int rx = x + camposX;
				int rz = z + camposZ;

				int ax = Math.abs(x);
				int az = Math.abs(z);

				Vector2i key = new Vector2i(rx,rz);
				Terrain value = terrains.get(key);

				boolean inRenderList = terrainsToRender.contains(value);

				if(value == null) { //If the terrain doesn't exist, let's create it
					if(!gridsInCreation.containsKey(key)) { // If the grid doesn't exist and it is not in creation, then we'll create it
						gridsToCreate.put(key,new GridGeneratorCallable(key,terrainInfo));
					}
					continue;
				}

				if(ax <= CHUNK_LOAD_DIST && az <= CHUNK_LOAD_DIST){ //At this distance we generate the model, keeping a grid-only border
					if(terrainsWaitingToCreate.containsKey(key)) { // if the terrain has a grid and is waiting, we create it
						terrainsToCreate.put(key,terrainsWaitingToCreate.get(key));
						terrainsWaitingToCreate.remove(key);
					}

					if(ax <= CHUNK_VIEW_DIST && az <= CHUNK_VIEW_DIST) {
						if(value.generated && !inRenderList) // If the terrain has a model and is not in the render list
							terrainsToRender.add(value);
					}else if(inRenderList){ // Else if the terrain in outside of the view dist and is in the render list we remove it
						terrainsToRender.remove(value);
					}

				}
			}
		}

		Timing.stop(debugName,"Refreshing");

		if(gridsToCreate.size() > 0) { //If we have grids to create
			Timing.start(debugName,"Grid Generation");
			generateGrids(gridsToCreate);
			Timing.stop(debugName,"Grid Generation");
		}

		if(gridsInCreation.size() > 0)
			checkForFutureGrids(); // We check for future at each frame
		
		if(terrainsInCreation.size()>0) {
			checkForFutureTerrains();
		}
		
		if(gridsInCreation.size() == 0 && terrainsToCreate.size()>0) {
			generateTerrains();
			terrainsToCreate.clear();
		}


		if(terrainsInCreation.size() == 0 && calculatedTerrains.size() > 0){
			Timing.start(debugName,"Model Generation");
			generateCalculatedTerrains();
			Timing.stop(debugName,"Model Generation");
		}

		if(lastClean - Time.getFrameMilliTime() > cleanGap){
			cleanTerrains();
			lastClean = Time.getFrameMilliTime();
		}

	}

	private void cleanTerrains(){
		List<Terrain> toRemove = new ArrayList<>();
		terrains.forEach((k,v) -> {
			if(Math.abs(k.x) > CHUNK_UNLOAD_DIST || Math.abs(k.y) > CHUNK_UNLOAD_DIST)
				toRemove.add(v);
		});

		for(Terrain t : toRemove){
			terrains.remove(t.getChunkPos());
			terrainsToRender.remove(t.getChunkPos());
		}
	}

	private boolean firstTime = true;

	private void generateCalculatedTerrains() {
		int i = 4;

		Iterator it = calculatedTerrains.entrySet().iterator();
	    while (it.hasNext() && i > 0) {
	        Map.Entry<Vector2i,IndiceVerticeNormal> entry = (Map.Entry)it.next();
	        Terrain t = terrains.get(entry.getKey());
			IndiceVerticeNormal v = entry.getValue();
			if(t == null)
				System.err.println("TERRAIN IS NULL");
			else
				t.generateTerrain(v.vertices, v.indices,v.blocks, v.normals);
	        it.remove(); // avoids a ConcurrentModificationException
	        i--;
	    }
	    

	}
	
	private void checkForFutureTerrains() {
	
		List<Vector2i> futuresToRemove = new ArrayList<Vector2i>();
		
		terrainsInCreation.forEach((k,v) -> {
			
			if(v.isDone()) {
				try {
					calculatedTerrains.put(k,v.get());
					futuresToRemove.add(k);
				}catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("BIG PROBLEM ON TERRAIN MANAGER");		   

				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
		

		
		for(Vector2i k:futuresToRemove){
			terrainsInCreation.remove(k);
		}

	}
	
	private void checkForFutureGrids() {

		List<Vector2i> futuresToRemove = new ArrayList<Vector2i>();
		
		gridsInCreation.forEach((k,v) -> {
			if(v.isDone()) {
				try {
					Terrain t = v.get();
					terrains.put(k,t);
					terrainsWaitingToCreate.put(k,new TerrainGeneratorCallable(t));
					futuresToRemove.add(k);
				}catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("BIG PROBLEM ON TERRAIN MANAGER");		   

				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});

		for(Vector2i k:futuresToRemove){
			gridsInCreation.remove(k);
		}
	}
	
	private void generateTerrains() {
		terrainsToCreate.forEach((k,v) -> {
			terrainsInCreation.put(k,executor.submit(v));
		});
	}

	private void generateGrids(Map<Vector2i,Callable<Terrain>> gridsToCreate) {
		gridsToCreate.forEach((k, v) -> {
			gridsInCreation.put(k,executor.submit(v));
		});
	}

	
	public void cleanUp() {
		executor.shutdown(); // Disable new tasks from being submitted
		   try {
		     // Wait a while for existing tasks to terminate
		     if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
		    	 executor.shutdownNow(); // Cancel currently executing tasks
		       // Wait a while for tasks to respond to being cancelled
		       if (!executor.awaitTermination(10, TimeUnit.SECONDS))
		           System.err.println("Pool did not terminate");
		     }
		   } catch (InterruptedException ie) {
		     // (Re-)Cancel if current thread also interrupted
			   executor.shutdownNow();
		     // Preserve interrupt status
		     Thread.currentThread().interrupt();
		   }
	}
	
	public class TerrainGeneratorCallable implements Callable<IndiceVerticeNormal>{
		
		Terrain terrain;

		public TerrainGeneratorCallable(Terrain terrain) {

			this.terrain = terrain;
		}

		@Override
		public IndiceVerticeNormal call() throws Exception {

	        return TerrainGenerator.generate(terrain);
		}
	}
	
	public class GridGeneratorCallable implements Callable<Terrain>{
		
		Vector2i chunkPos;
		TerrainInfo tinfo;

		public GridGeneratorCallable(Vector2i chunkPos, TerrainInfo tinfo) {
			this.chunkPos = chunkPos;
			this.tinfo = tinfo;
		}

		@Override
		public Terrain call() throws Exception {
	        return GridGenerator.generate(chunkPos,tinfo);
		}
	}
	
	
	//-----------------------  Get Information on Terrain ----------------------//
	
	public List<Terrain> getTerrainsToRender(){
		return terrainsToRender;
	}

	public static byte getBlock(int x, int y, int z, int chunkx, int chunkz) {

		Terrain t = terrains.get(new Vector2i(chunkx,chunkz));
		if(t == null || t.grid ==null) {
			return -1;
		} else {
			return t.grid[x][y][z];
		}
	}
	
	public static boolean isTerrainTransparent(int x, int y, int z, int chunkx, int chunky) {
		byte blockid = getBlock(x, y, z, chunkx, chunky);
		if(blockid == -1)
			return false;
		
		return Block.blocks[blockid].getTransparency() < 1;
	}
	
	
	public static boolean isTerrainSolid(int x, int y, int z, int chunkx, int chunky) {

		byte blockid = getBlock(x, y, z, chunkx, chunky);
		if(blockid == -1)
			return false;
		
		return Block.blocks[blockid].isSolid();
	}
}
