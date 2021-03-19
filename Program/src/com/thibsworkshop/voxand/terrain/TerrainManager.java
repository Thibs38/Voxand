package com.thibsworkshop.voxand.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.debugging.Timing;
import com.thibsworkshop.voxand.entities.Player;
import com.thibsworkshop.voxand.game.Config;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.rendering.MasterRenderer;
import com.thibsworkshop.voxand.terrain.TerrainGenerator.IndiceVerticeNormal;
import com.thibsworkshop.voxand.terrain.Chunk.TerrainInfo;
import com.thibsworkshop.voxand.toolbox.Maths;
import com.thibsworkshop.voxand.toolbox.Utility;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class TerrainManager {

	public static Map<Vector2i, Chunk> chunks = new HashMap<Vector2i, Chunk>();

	private Map<Vector2i,Callable<Chunk>> gridsToCreate = new HashMap<Vector2i,Callable<Chunk>>();

	private Map<Vector2i,Future<Chunk>> gridsInCreation = new HashMap<Vector2i, Future<Chunk>>();
	
	private Map<Vector2i,Callable<IndiceVerticeNormal>> terrainsToCreate = new HashMap<Vector2i, Callable<IndiceVerticeNormal>>();
	
	private Map<Vector2i,Callable<IndiceVerticeNormal>> terrainsWaitingToCreate = new HashMap<Vector2i, Callable<IndiceVerticeNormal>>();
	
	private Map<Vector2i, Future<IndiceVerticeNormal>> terrainsInCreation = new HashMap<Vector2i, Future<IndiceVerticeNormal>>();
	
	private Map<Vector2i,IndiceVerticeNormal> calculatedTerrains = new HashMap<Vector2i,IndiceVerticeNormal>(); //Calculated terrains waited to be created

	private Map<Vector2i,Chunk> terrainsToRender = new HashMap<>();
	private TerrainInfo terrainInfo;
	
	private boolean refresh = false; // refresh the chunk every two frames
	
	private Vector2f campos;
	
	ExecutorService executor;
	TerrainGenerator[] terrainGenerators;

	public static String debugName = "Terrain Generation";

	private long lastClean = 0;
	private long cleanGap = 5; // in seconds

	
	public TerrainManager(TerrainInfo terrainInfo) {
		this.terrainInfo = terrainInfo;
		MasterRenderer.terrainRenderer.linkManager(this);
		MasterRenderer.lineRenderer.linkTerrainManager(this);
		//executor = new ThreadPoolExecutor(Config.chunkGenDist*2,Config.chunkGenDist*4*(Config.chunkGenDist+1)+1,5,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
		executor = Executors.newFixedThreadPool(Utility.cores);
		terrainGenerators = new TerrainGenerator[Utility.cores + 1];
		for(int i = 0; i < Utility.cores + 1; i++) terrainGenerators[i] = new TerrainGenerator();
		Timing.add(debugName,new String[]{
			"Refreshing",
			"Grid Generation",
			"Model Generation"
		});

		lastClean = Time.getFrameMilliTime();
	}
	
	public void refreshChunks(Player player) {

		refresh = !refresh;
		if(!refresh)
			return;

		//TODO: incremental loading: load the chunk near the player and then those farther
		//TODO: divide the chunks into sub chunks of 32*32 and render them separately
		// load several empty chunks on the same frame to avoid freezes

		Timing.start(debugName,"Refreshing");


		Vector2i playerPos = player.transform.chunkPos;

		for(int x = -Config.chunkGenDist; x <= Config.chunkGenDist; x++) {
			for(int z = -Config.chunkGenDist; z <= Config.chunkGenDist; z++) {
				int rx = x + playerPos.x;
				int rz = z + playerPos.y;

				Vector2i key = new Vector2i(rx,rz);
				Chunk value = chunks.get(key);

				int sqr_distance = Maths.sqrDistance(key,playerPos);

				if(sqr_distance <= Config.sqr_chunkGenDist) {

					boolean inRenderList = terrainsToRender.containsKey(key);

					if(value == null) { //If the terrain doesn't exist, let's create it
						if(!gridsInCreation.containsKey(key)) { // If the grid doesn't exist and it is not in creation, then we'll create it
							gridsToCreate.put(key,new GridGeneratorCallable(key,terrainInfo));
						}
						continue;
					}

					value.update(sqr_distance);

					if(sqr_distance <= Config.sqr_chunkLoadDist){ //At this distance we generate the model, keeping a grid-only border
						if(terrainsWaitingToCreate.containsKey(key)) { // if the terrain has a grid and is waiting, we create it
							terrainsToCreate.put(key,terrainsWaitingToCreate.get(key));
							terrainsWaitingToCreate.remove(key);
						}

						if(sqr_distance <= Config.sqr_chunkViewDist) {
							if(value.generated && !inRenderList) // If the terrain has a model and is not in the render list
								terrainsToRender.put(key,value);
						}else if(inRenderList){ // Else if the terrain in outside of the view dist and is in the render list we remove it
							terrainsToRender.remove(value);
						}
					}
				}
			}
		}

		cleanTerrains(player);

		Timing.stop(debugName,"Refreshing");

		if(gridsToCreate.size() > 0) { //If we have grids to create
			Timing.start(debugName,"Grid Generation");
			generateGrids(gridsToCreate);
			gridsToCreate.clear();
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

	}

	private void cleanTerrains(Player player){
		List<Chunk> toRemove = new ArrayList<>();
		chunks.forEach((k, v) -> {
			if(v.getLastTickUpdate() < Time.getTick()){
				v.update(Maths.sqrDistance(k,player.transform.chunkPos));
				if(v.getSqr_distance() > Config.sqr_chunkUnloadDist)
					toRemove.add(v);
			}
		});

		for(Chunk t : toRemove){
			chunks.remove(t.getChunkPos());
			terrainsToRender.remove(t.getChunkPos());
		}
	}

	private boolean firstTime = true;

	private void generateCalculatedTerrains() {
		int i = 4;

		Iterator it = calculatedTerrains.entrySet().iterator();
	    while (it.hasNext() && i > 0) {
	        Map.Entry<Vector2i,IndiceVerticeNormal> entry = (Map.Entry)it.next();
	        Chunk t = chunks.get(entry.getKey());
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
					Chunk t = v.get();
					chunks.put(k,t);
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

	private void generateGrids(Map<Vector2i,Callable<Chunk>> gridsToCreate) {
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
		
		Chunk chunk;

		public TerrainGeneratorCallable(Chunk chunk) {

			this.chunk = chunk;
		}

		@Override
		public IndiceVerticeNormal call() throws Exception {
			for(int i = 0; i < Utility.cores; i++){
				if(!terrainGenerators[i].busy)
					return terrainGenerators[i].generate(chunk);
			}
			return terrainGenerators[Utility.cores].generate(chunk);
		}
	}
	
	public class GridGeneratorCallable implements Callable<Chunk>{
		
		Vector2i chunkPos;
		TerrainInfo tinfo;

		public GridGeneratorCallable(Vector2i chunkPos, TerrainInfo tinfo) {
			this.chunkPos = chunkPos;
			this.tinfo = tinfo;
		}

		@Override
		public Chunk call() throws Exception {
	        return GridGenerator.generate(chunkPos,tinfo);
		}
	}
	
	
	//-----------------------  Get Information on Terrain ----------------------//
	
	public Map<Vector2i,Chunk> getTerrainsToRender(){
		return terrainsToRender;
	}

	public static Chunk getChunk(Vector2i coords){
		return chunks.get(coords);
	}

	private static final Vector2i getBlockBuffer = new Vector2i(0); //TODO: REALLY BAD IDEA
	/**
	 * Retrieves the block at the specified position.
	 * Use preferably {@link #getBlock(int, int, int, Vector2i) getBlock}.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param chunkx chunk x coordinate
	 * @param chunkz chunk z coordinate
	 * @return The block type
	 */
	public static byte getBlock(int x, int y, int z, int chunkx, int chunkz) {
		getBlockBuffer.set(chunkx,chunkz);
		System.out.print("chunkx: " + chunkx + " chunkz: " + chunkz + "  ");
		Debug.printVector(getBlockBuffer);
		Chunk t = chunks.get(getBlockBuffer);

		if (t == null || t.grid == null) {
			return -1;
		} else {
			return t.grid[x][y][z];
		}
	}

	/**
	 * Retrieves the block at the specified position.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param chunkPos chunk position
	 * @return The block type
	 */
	public static byte getBlock(int x, int y, int z, Vector2i chunkPos){
		Chunk t = chunks.get(chunkPos);
		if (t == null || t.grid == null) {
			return -1;
		} else {
			return t.grid[x][y][z];
		}
	}

	public static boolean isTerrainTransparent(int x, int y, int z, Vector2i chunkPos) {
		byte blockid = getBlock(x, y, z, chunkPos);
		if(blockid == -1)
			return false;

		return Block.blocks[blockid].getTransparency() < 1;
	}

	/**
	 * Tests if the block specified with the arguments is solid or not.
	 * Use preferably {@link #isBlockSolid(int, int, int, Vector2i) isBlockSolid}.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param chunkx x chunk coordinate
	 * @param chunkz z chunk coordinate
	 * @return true if the block is solid, false otherwise
	 */
	public static boolean isBlockSolid(int x, int y, int z, int chunkx, int chunkz) {

		byte blockid = getBlock(x, y, z, chunkx, chunkz);
		if(blockid == -1)
			return false;
		return Block.blocks[blockid].isSolid();
	}

	/**
	 * Tests if the block specified with the arguments is solid or not.
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @param chunkPos chunk position
	 * @return true if the block is solid, false otherwise
	 */
	public static boolean isBlockSolid(int x, int y, int z, Vector2i chunkPos) {

		byte blockid = getBlock(x, y, z, chunkPos);
		if(blockid == -1)
			return false;
		return Block.blocks[blockid].isSolid();
	}
}
