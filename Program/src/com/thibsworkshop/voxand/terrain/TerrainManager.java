package com.thibsworkshop.voxand.terrain;

import java.util.*;
import java.util.concurrent.*;

import com.thibsworkshop.voxand.data.Block;
import com.thibsworkshop.voxand.debugging.Debug;
import com.thibsworkshop.voxand.debugging.Timing;
import com.thibsworkshop.voxand.entities.Player;
import com.thibsworkshop.voxand.game.Config;
import com.thibsworkshop.voxand.io.Time;
import com.thibsworkshop.voxand.rendering.renderers.MasterRenderer;
import com.thibsworkshop.voxand.terrain.TerrainGenerator.IndicesVerticesNormals;
import com.thibsworkshop.voxand.terrain.Chunk.TerrainInfo;
import com.thibsworkshop.voxand.toolbox.Maths;
import com.thibsworkshop.voxand.toolbox.Utility;

import org.joml.Vector2i;


//OPTIMIZE
// Use a block palette for each chunk to limit the number of bits needed to store
public class TerrainManager {

	public static final HashMap<Vector2i, Chunk> chunks = new HashMap<>();

	private final HashMap<Vector2i,Future<Chunk>> gridsInCreation = new HashMap<>();

	private final HashMap<Vector2i,Callable<IndicesVerticesNormals>> terrainsWaitingToCreate = new HashMap<>();

	private final TreeMap<Vector2i,Callable<IndicesVerticesNormals>> terrainsToCreate = new TreeMap<>(layerComparator);

	private final ArrayList<Future<IndicesVerticesNormals>> terrainsInCreation = new ArrayList<>();
	
	private final LinkedList<IndicesVerticesNormals> calculatedTerrains = new LinkedList<>(); //Calculated terrains waited to be created

	private final TerrainInfo terrainInfo;

	ExecutorService executor;

	public static String debugName = "Terrain Generation";

	private static final Vector2i playerChunkPos = Player.player.transform.chunkPos;

	public enum State{ LOOP, WAIT_GRIDS, ASSIGN_GRIDS, START_MODELS, WAIT_MODELS, LOAD_MODELS}

	private State state;

	public static TerrainManager main;

	//<editor-fold desc="Comparators">
	/**
	 * Lookup table that associates a chunkPosition to an order and a layer
	 */
	private static final Map<Vector2i,Vector2i>  chunkOrder = new HashMap<>(Config.chunkGenDist*Config.chunkGenDist);

	/**
	 * Initialize the lookup table of chunk ordering.
	 */
	public static void init(){
		int k = 0;
		chunkOrder.put(new Vector2i(0,0), new Vector2i(k++,0));

		for(int i = 1; i <= Config.chunkGenDist; i++){
			for(int j = 0; j < i * 2; j++){
				chunkOrder.put(new Vector2i(-i+j,-i), new Vector2i(k++,i));
			}

			for(int j = 0; j < i * 2; j++){
				chunkOrder.put(new Vector2i(i,-i+j), new Vector2i(k++,i));
			}

			for(int j = 0; j < i * 2; j++){
				chunkOrder.put(new Vector2i(i-j,i), new Vector2i(k++,i));
			}

			for(int j = 0; j < i * 2; j++){
				chunkOrder.put(new Vector2i(-i,i-j), new Vector2i(k++,i));
			}
		}
	}

	private static final Vector2i compare1 = new Vector2i(0);
	private static final Vector2i compare2 = new Vector2i(0);

	/**
	 * This position is set up at the loop state, and then doesn't change to ensure that the chunks that are currently
	 * being treated are using the same position as reference.
	 */
	private static final Vector2i playerChunkPosTemp = new Vector2i(0);

	/**
	 * Compare two Vector2i based on the layer order:
	 * 7 6 5
	 * 8 0 4
	 * 1 2 3
	 */
	public static Comparator<Vector2i> layerComparator = ( o1, o2 ) -> {
		compare1.set(o1.x - playerChunkPosTemp.x, o1.y - playerChunkPosTemp.y);
		compare2.set(o2.x - playerChunkPosTemp.x, o2.y - playerChunkPosTemp.y);
		return chunkOrder.get(compare1).x - chunkOrder.get(compare2).x;
	};

	/**
	 * Compare two Vector2i based on their length.
	 */
	public static Comparator<Vector2i> distanceComparator = (o1, o2) -> o1.x * o1.x + o1.y * o1.y - o2.x * o2.x - o2.y * o2.y;
	//</editor-fold>

	public TerrainManager(TerrainInfo terrainInfo) {
		this.terrainInfo = terrainInfo;
		MasterRenderer.terrainRenderer.linkManager(this);
		MasterRenderer.lineRenderer.linkTerrainManager(this);
		//executor = new ThreadPoolExecutor(Config.chunkGenDist*2,Config.chunkGenDist*4*(Config.chunkGenDist+1)+1,5,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
		executor = Executors.newFixedThreadPool(Utility.cores);
		Timing.add(debugName,new String[]{
			"Refreshing",
			"Grid Generation",
			"Model Generation"
		});
		init();
		state = State.LOOP;
		main = this;

	}

	//<editor-fold desc="Management">
	private final Vector2i chunkPosTemp = new Vector2i(0);
	public int currentLayer = 0;

	//OPTIMIZE
	// divide the chunks into sub chunks of 32*32 and render them separately
	// load several empty chunks on the same frame to avoid freezes

	/**
	 * Refreshes the state of the manager.
	 */
	public void refreshChunks() {

		chunks.forEach((k,v) ->{
			v.update(Maths.sqrDistance(k, playerChunkPos), playerChunkPos);
		});

		switch (state) {
			case LOOP -> {
				//System.out.println("LOOP");
				Timing.start(debugName,"Refreshing");
				if(loopChunk()){
					state = State.WAIT_GRIDS;
					refreshChunks();
				}
				Timing.stop(debugName,"Refreshing");

			}
			case WAIT_GRIDS -> {
				//System.out.println("WAIT GRIDS");
				if(checkForFutureGrids())
					state = State.ASSIGN_GRIDS;
			}
			case ASSIGN_GRIDS -> {
				//System.out.println("ASSIGN GRIDS");
				Timing.start(debugName,"Refreshing");
				assignGrids();
				Timing.stop(debugName,"Refreshing");
				state = State.START_MODELS;
				refreshChunks();
			}
			case START_MODELS -> {
				//System.out.println("START MODELS");
				generateModels();
				state = State.WAIT_MODELS;
			}
			case WAIT_MODELS -> {
				//System.out.println("WAIT MODELS");
				if(checkForFutureModels())
					state = State.LOAD_MODELS;
			}
			case LOAD_MODELS -> {
				//System.out.println("LOAD MODELS");
				if(loadModels()){
					//if(Input.isKeyDown(GLFW_KEY_ENTER))
						state = State.LOOP;
				}
			}
		}
	}

	/**
	 * Loops through chunks around the player, and update them or add them to generation lists.
	 */
	private boolean loopChunk(){
		playerChunkPosTemp.set(playerChunkPos);

		boolean stop = false;
		for(int i = 0; i < Config.chunkGenDist; i++){
			for(int x = -i; x <= i; x++) {
				for (int z = -i; z <= i; z++) {
					int rx = x + playerChunkPosTemp.x;
					int rz = z + playerChunkPosTemp.y;
					chunkPosTemp.set(rx, rz);
					Chunk value = chunks.get(chunkPosTemp);

					if (value == null) { //If the grid doesn't exist, let's create it
						if (!gridsInCreation.containsKey(chunkPosTemp)) { // If the grid doesn't exist and it is not in creation, then we'll create it
							Vector2i v = new Vector2i(chunkPosTemp);
							gridsInCreation.put(v, executor.submit(new GridGeneratorCallable(v, terrainInfo)));
							if(!stop){
								if(i > 0){
									stop = true;
									currentLayer = i;
								}
							}
						}
					}
				}
			}
			if(stop) break;
		}
		cleanTerrains();
		return stop;
	}

	//OPTIMIZE: deal with the tick stuff
	/**
	 * Loops through known chunks and catch those too far away and unload them.
	 */
	private void cleanTerrains(){
		Iterator it = chunks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Vector2i, Chunk> entry = (Map.Entry) it.next();
			Chunk v = entry.getValue();
			Vector2i k = entry.getKey();


			if(v.getLastTickUpdate() < Time.getTick()) { //If the tick is equal to the current one, no need to check
				v.update(Maths.sqrDistance(k, playerChunkPosTemp), playerChunkPosTemp);
			}
			if(v.getSqr_distance() > Config.sqr_chunkUnloadDist){
				it.remove();
				terrainsWaitingToCreate.remove(k);
			} else {
				if (v.dirty) {
					terrainsToCreate.put(k, new TerrainGeneratorCallable(v));
					v.dirty = false;
				}
			}

		}
	}

	/**
	 * Returns true if the specified vector is out of range of the generation
	 * @param v the vector to test in local coordinates
	 * @return true if out of range, false otherwise
	 */
	private boolean outOfRange(Vector2i v){
		return v.x > Config.chunkGenDist || v.x < -Config.chunkGenDist || v.y > Config.chunkGenDist || v.y < -Config.chunkGenDist;
	}

	/**
	 * Checks the grids in generation and retrieves those who are finished.
	 * @return returns true if there's still grids that are being calculated, false if the job is finished
	 */
	private boolean checkForFutureGrids() {
		Iterator it = gridsInCreation.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Vector2i, Future<Chunk>> entry = (Map.Entry) it.next();
			Future<Chunk> v = entry.getValue();
			Vector2i k = entry.getKey();
			if(v.isDone()) {
				try {
					Chunk t = v.get();
					chunks.put(k,t);
					terrainsWaitingToCreate.put(k,new TerrainGeneratorCallable(t));
					it.remove();
				}catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("BIG PROBLEM ON TERRAIN MANAGER");
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}

		return gridsInCreation.isEmpty();
	}

	/**
	 * Loops trough the generated grids and decide which models should be generated based on their distance from player
	 */
	private void assignGrids(){
		Iterator it = terrainsWaitingToCreate.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Vector2i, Callable<IndicesVerticesNormals>> entry = (Map.Entry) it.next();
			Vector2i k = entry.getKey();
			chunkPosTemp.set(k.x - playerChunkPosTemp.x, k.y - playerChunkPosTemp.y);
			if(outOfRange(chunkPosTemp)){
				it.remove();
				chunks.remove(k);
				continue;
			}
			int layer = chunkOrder.get(chunkPosTemp).y;
			if(layer < currentLayer){
				int sqr_distance = Maths.sqrMagnitude(chunkPosTemp);
				if (sqr_distance <= Config.sqr_chunkLoadDist) { //At this distance we generate the model, keeping a grid-only square border
					terrainsToCreate.put(k, entry.getValue());
					it.remove();
				}
			}
		}
	}

	/**
	 * Starts the generation of the models.
	 */
	private void generateModels() {
		Iterator it = terrainsToCreate.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Vector2i, Callable<IndicesVerticesNormals>> entry = (Map.Entry)it.next();
			terrainsInCreation.add(executor.submit(entry.getValue()));
			it.remove();
		}
	}

	/**
	 * Checks the models in generation and retrieves those who are finished.
	 * @return returns true if there's still models that are being calculated, false if the job is finished
	 */
	private boolean checkForFutureModels() {
		for(int i = 0; i < terrainsInCreation.size(); i++){
			Future<IndicesVerticesNormals> f = terrainsInCreation.get(i);
			if(f.isDone()){
				try{
					IndicesVerticesNormals value = f.get();
					int j = 0;
					boolean added = false;
					for(IndicesVerticesNormals ind : calculatedTerrains){ //We add values sorted
						if(layerComparator.compare(ind.chunk.getChunkPos(), value.chunk.getChunkPos()) > 0){
							calculatedTerrains.add(j,value);
							added = true;
							break;
						}
						j++;
					}
					if(!added){
						if(calculatedTerrains.isEmpty())
							calculatedTerrains.add(value);
						else
							calculatedTerrains.addLast(value);
					}
					terrainsInCreation.remove(i);
				} catch (InterruptedException e) {
					e.printStackTrace();
					System.err.println("BIG PROBLEM ON TERRAIN MANAGER");
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		loadModels();
		return terrainsInCreation.isEmpty();
	}

	/**
	 * Loads generated models into the graphics card
	 */
	private boolean loadModels() {
		if(!calculatedTerrains.isEmpty()){
			IndicesVerticesNormals v = calculatedTerrains.getFirst();
			v.chunk.generateTerrain(v.vertices, v.indices,v.blocks, v.normals);
			calculatedTerrains.removeFirst();
		}
		return calculatedTerrains.isEmpty();
	}

	/**
	 * Properly stop the manager.
	 */
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
	//</editor-fold>

	//<editor-fold desc="Callables">

	public class TerrainGeneratorCallable implements Callable<IndicesVerticesNormals>{
		
		Chunk chunk;

		public TerrainGeneratorCallable(Chunk chunk) {

			this.chunk = chunk;
		}

		@Override
		public IndicesVerticesNormals call() {
			Vector2i temp = new Vector2i(0);

			int x = chunk.getChunkPos().x;
			int z = chunk.getChunkPos().y;


			Chunk back = chunks.get(temp.set(x,z-1));
			Chunk front = chunks.get(temp.set(x,z+1));
			Chunk right = chunks.get(temp.set(x+1,z));
			Chunk left = chunks.get(temp.set(x-1,z));

			byte[][][] backB = null;
			byte[][][] frontB = null;
			byte[][][] rightB = null;
			byte[][][] leftB = null;

			if(back != null)
				backB = back.grid;
			else{
				System.out.print("null -z: ");Debug.printVector(temp.set(x,z-1));
				System.out.println("current layer: " + currentLayer);
				Debug.printVector(playerChunkPosTemp);
				System.out.println("");
			}
			if(front != null)
				frontB = front.grid;
			else{
				System.out.print("null z: ");Debug.printVector(temp.set(x,z+1));
				System.out.println("current layer: " + currentLayer);
				Debug.printVector(playerChunkPosTemp);
				System.out.println("");
			}
			if(right != null)
				rightB = right.grid;
			else{
				System.out.print("null x: ");Debug.printVector(temp.set(x+1,z));
				System.out.println("current layer: " + currentLayer);
				Debug.printVector(playerChunkPosTemp);
				System.out.println("");
			}
			if(left != null)
				leftB = left.grid;
			else{
				System.out.print("null -x: ");Debug.printVector(temp.set(x-1,z));
				System.out.println("current layer: " + currentLayer);
				Debug.printVector(playerChunkPosTemp);
				System.out.println("");
			}

			return TerrainGenerator.generate(chunk, backB, frontB, rightB, leftB);
		}
	}
	
	public class GridGeneratorCallable implements Callable<Chunk>{
		
		public Vector2i chunkPos;
		TerrainInfo tinfo;

		public GridGeneratorCallable(Vector2i chunkPos, TerrainInfo tinfo) {
			this.chunkPos = chunkPos;
			this.tinfo = tinfo;
		}

		@Override
		public Chunk call() {
	        return GridGenerator.generate(chunkPos, playerChunkPosTemp, tinfo);
		}
	}
	//</editor-fold>

	//<editor-fold desc="Getters">

	public static Chunk getChunk(Vector2i coords){
		return chunks.get(coords);
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
	//</editor-fold>
}
