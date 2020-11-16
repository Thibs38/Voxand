package com.thibsworkshop.voxand.terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.thibsworkshop.voxand.entities.Camera;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.rendering.MasterRenderer;
import com.thibsworkshop.voxand.rendering.TerrainRenderer;
import com.thibsworkshop.voxand.toolbox.Maths;
import com.thibsworkshop.voxand.toolbox.Maths.Key;
import com.thibsworkshop.voxand.terrain.TerrainGenerator.IndiceVerticeNormal;
import com.thibsworkshop.voxand.terrain.Terrain.TerrainInfo;
import org.joml.Vector2f;
import org.joml.Vector3f;


public class TerrainManager {
	
	private static final int CHUNK_VIEW_DIST = 12;
	private final int SQR_CHUNK_VIEW_DIST = 12*12;
	
	private static final int CHUNK_GENERATE_DIST = 15;
	private static final int SQR_CHUNK_GENERATE_DIST = 15*15;
	private final int SQR4_CHUNK_GENERATE_DIST = 15*15*4;

	
	private static final int CHUNK_LOAD_DIST = 13;
	private final int SQR_CHUNK_LOAD_DIST = 13*13;
	
	private static final int CHUNK_UNLOAD_DIST = 18;
	private final int SQR_CHUNK_UNLOAD_DIST = 18*18;
	
	public static Map<Key,Terrain> terrains = new HashMap<Key,Terrain>();

	private Map<Key,Future<Terrain>> gridsInCreation = new HashMap<Key, Future<Terrain>>();
	
	private Map<Key,Callable<IndiceVerticeNormal>> terrainsToCreate = new HashMap<Key, Callable<IndiceVerticeNormal>>();
	
	private Map<Key,Callable<IndiceVerticeNormal>> terrainsWaitingToCreate = new HashMap<Key, Callable<IndiceVerticeNormal>>();
	
	private Map<Key, Future<IndiceVerticeNormal>> terrainsInCreation = new HashMap<Key, Future<IndiceVerticeNormal>>();
	
	private Map<Key,IndiceVerticeNormal> calculatedTerrains = new HashMap<Key,IndiceVerticeNormal>(); //Calculated terrains waited to be created

	private List<Terrain> terrainsToRender = new ArrayList<Terrain>();
	private TerrainInfo terrainInfo;
	
	private long lastClean = 0;
	private int cleanGap = 5000;
	
	private Vector2f campos;
	
	ExecutorService executor;

	
	public TerrainManager(TerrainInfo terrainInfo) {
		this.terrainInfo = terrainInfo;
		MasterRenderer.terrainRenderer.linkManager(this);
		lastClean = Time.getCurrentTime();
		executor = new ThreadPoolExecutor(CHUNK_GENERATE_DIST*2,CHUNK_GENERATE_DIST*4*(CHUNK_GENERATE_DIST+1)+1,5,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());

	}
	
	public void refreshChunks() {

		//TODO: Maybe check if a terrain is in the list of rendering instead of flushing the list
		//TODO: can't see terrains
		Map<Key,Callable<Terrain>> gridsToCreate = new HashMap<Key,Callable<Terrain>>();
		
		int camposX = (int)(Camera.mainCamera.getPosition().x/(float)Terrain.CHUNK_SIZE);
		int camposZ = (int)(Camera.mainCamera.getPosition().z/(float)Terrain.CHUNK_SIZE);
		campos = new Vector2f(camposX,camposZ);

		for(int x = -SQR_CHUNK_GENERATE_DIST + camposX; x <= SQR_CHUNK_GENERATE_DIST + camposX; x++) {
			for(int z = -SQR_CHUNK_GENERATE_DIST + camposZ; z <= SQR_CHUNK_GENERATE_DIST + camposZ; z++) {
				Key key = new Key(x,z);
				Terrain value = terrains.get(key);
				
				float dist = Maths.sqrDistance(new Vector2f(x,z), campos);
				if(dist <= SQR_CHUNK_GENERATE_DIST) { // At this distance, we generate all the grids
					if(value == null) {
						if(!gridsInCreation.containsKey(key)) { // If the grid doesn't exist and it is not in creation, then we'll create it
							gridsToCreate.put(key,new GridGeneratorCallable(x,z,terrainInfo));
						}
						continue;
					}
					if(dist <= SQR_CHUNK_LOAD_DIST) { // At this distance, we generate only the terrains, keeping an ungenerated terrain border around
						if(terrainsWaitingToCreate.containsKey(key)) {
							terrainsToCreate.put(key,terrainsWaitingToCreate.get(key));
							terrainsWaitingToCreate.remove(key);
						}
						
						if(dist <= SQR_CHUNK_VIEW_DIST && value.generated) {
							terrainsToRender.add(value);
						}
					}
				}
			}
		}

		if(gridsToCreate.size() > 0) { //If we have grids to create
			generateGrids(gridsToCreate);

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
		
		if(terrainsInCreation.size() == 0 && calculatedTerrains.size() > 0)
			generateCalculatedTerrains();		

		terrainsToRender.clear();

		if(Time.getCurrentTime() - lastClean > cleanGap) {
			lastClean = Time.getCurrentTime();
			deleteUnusedChunks();
		}

	}
	private void generateCalculatedTerrains() {
		int i = 0;
		if(calculatedTerrains.size() >= SQR4_CHUNK_GENERATE_DIST)
			i = calculatedTerrains.size();
		else
			i = 2;
		

		Iterator it = calculatedTerrains.entrySet().iterator();
	    while (it.hasNext() && i > 0) {
	        Map.Entry<Key,IndiceVerticeNormal> entry = (Map.Entry)it.next();
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
	
		List<Key> futuresToRemove = new ArrayList<Key>();
		
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
		

		
		for(Key k:futuresToRemove){
			terrainsInCreation.remove(k);
		}

	}
	
	private void checkForFutureGrids() {

		List<Key> futuresToRemove = new ArrayList<Key>();
		
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
		

		
		for(Key k:futuresToRemove){
			gridsInCreation.remove(k);
		}

		
		
	}
	
	private void generateTerrains() {
		terrainsToCreate.forEach((k,v) -> {
			terrainsInCreation.put(k,executor.submit(v));
		});
	}

	private void generateGrids(Map<Key,Callable<Terrain>> gridsToCreate) {
		gridsToCreate.forEach((k, v) -> {
			gridsInCreation.put(k,executor.submit(v));
		});
	}
	
	private void deleteUnusedChunks() {
		List<Key> toRemove = new ArrayList<Key>();
		terrains.forEach((k, v) -> {
			float dist = Maths.sqrDistance(new Vector2f(k.x,k.y), campos);
            if(dist > SQR_CHUNK_UNLOAD_DIST){
            	toRemove.add(k);
            }
        });
		
		for(Key s:toRemove) {
			terrains.remove(s);
		}
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
		
		int chunkx;
		int chunkz;
		TerrainInfo tinfo;

		public GridGeneratorCallable(int chunkx, int chunkz, TerrainInfo tinfo) {
			this.chunkx = chunkx;
			this.chunkz = chunkz;
			this.tinfo = tinfo;
		}

		@Override
		public Terrain call() throws Exception {
	        return GridGenerator.generate(chunkx, chunkz,tinfo);
		}
	}
	
	
	//-----------------------  Get Information on Terrain ----------------------//
	
	public List<Terrain> getTerrainsToRender(){
		return terrainsToRender;
	}

	public static byte getBlock(int x, int y, int z, int chunkx, int chunkz) {

		Terrain t = terrains.get(new Key(chunkx,chunkz));
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
