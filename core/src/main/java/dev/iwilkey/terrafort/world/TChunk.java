package dev.iwilkey.terrafort.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.harvestable.TBush;
import dev.iwilkey.terrafort.obj.harvestable.TFlowers;
import dev.iwilkey.terrafort.obj.harvestable.TTree;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.TMob;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.persistent.TSerializable;
import dev.iwilkey.terrafort.world.terrain.TBiome;
import dev.iwilkey.terrafort.world.terrain.TDefaultBiome;
import dev.iwilkey.terrafort.world.terrain.TTerrainGenerator;

/**
 * A serializable definition of a chunk of world data. Used to dictate the state of a {@link TChunkRuntime}.
 * @author Ian Wilkey (iwilkey)
 */
public final class TChunk implements TSerializable, Disposable {

	/**
	 * The unique serialization ID.
	 */
	private static final long serialVersionUID = 2765329902184632708L;
	
	////////////////////////////////
	// Non-serializable attributes. 
	////////////////////////////////
	
	/**
	 * The size of one world chunk, in tiles. (CHUNK_SIZE x CHUNK_SIZE) tiles.
	 */
	public transient static final int CHUNK_SIZE = 16;
	
	private transient TChunkRuntime concrete = null;
	private transient TBiome        biome    = null;
	
	////////////////////////////////
	// Serializable attributes.
	////////////////////////////////
	
	private final TWorld                 parent;
	private final int                    chunkX;
	private final int                    chunkY;
	private final HashMap<Long, Integer> cachedTerrainData;
	private final ArrayList<TObject>     cachedObjectData;
	
	/**
	 * Creates a new chunk a (chunkX, chunkY) [chunk space] inside of the given parent {@link TWorld}.
	 */
	public TChunk(TWorld parent, int chunkX, int chunkY) {
		this.parent        = parent;
		this.chunkX        = chunkX;
		this.chunkY        = chunkY;
		biome              = new TDefaultBiome(this);
		concrete           = new TChunkRuntime(this);
		cachedTerrainData  = new HashMap<>();
		cachedObjectData   = new ArrayList<>();
	}
	
	/**
	 * This means that this object is loaded from memory, or from a serialized state. We must recreate the runtime version.
	 */
	@Override
	public void loadFromPersistent() {
		System.out.println("[TerrafortPersistent] Loading chunk state " + Long.toHexString(getPositionHash()));
		// recreate transients...
		biome    = new TDefaultBiome(this);
		concrete = new TChunkRuntime(this);
		// create objects that don't already exist in the concrete state...
		for(final TObject o : cachedObjectData)
			addObjectRuntime(o);
	}
	
	/**
	 * Instructs this chunk that it needs to render it's tile at given tile coordinates. This includes terrain and world objects.
	 */
	public void render(float dt, long tileX, long tileY) {
		if(!contains(tileX, tileY))
			return;
		// render terrain tile...
		biome.render(dt, tileX, tileY);
	}
	
	/**
	 * Update the state of active objects as well as render them.
	 */
	public void update(float dt) {
		concrete.update(dt);
	}
	
	/**
	 * The x position of this chunk in chunk space.
	 */
	public int getChunkX() {
		return chunkX;
	}
	
	/**
	 * The y position of this chunk in chunk space.
	 */
	public int getChunkY() {
		return chunkY;
	}

	/**
	 * Returns the position hash of this chunk.
	 */
	public long getPositionHash() {
		return (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
	}
	
	/**
	 * Returns the position hash of given tile coordinates.
	 */
	public long getTilePositionHash(long tileX, long tileY) {
		return (tileX << 32) | (tileY & 0xffffffffL);
	}
	
	/**
	 * Returns whether or not this chunk contains the given tile coordinates.
	 */
	public boolean contains(long tileX, long tileY) {
		return (tileX / CHUNK_SIZE == chunkX) && (tileY / CHUNK_SIZE == chunkY);
	}
	
	/**
	 * Generates or returns cached terrain height at given tile coordinates.
	 */
	public int getOrGenerateTile(long tileX, long tileY) {
		if(!contains(tileX, tileY))
			return -1;
		long hash = getTilePositionHash(tileX, tileY);
		if(!cachedTerrainData.containsKey(hash)) {
			final int terrainHeight = TTerrainGenerator.getHeightAt(parent.getWorldSeed(), tileX, tileY);
			switch(terrainHeight) {
				case TBiome.WATER_LEVEL:
					break;
				case TBiome.SAND_LEVEL:
					break;
				case TBiome.GRASS_LEVEL:
					float val = TTerrainGenerator.layer(parent.getWorldSeed(), tileX, tileY, 0.1f, 16);
					int   seg = TMath.partition(val, 4, 
							(float)ThreadLocalRandom.current().nextDouble(0.30f, 0.45f), 
							(float)ThreadLocalRandom.current().nextDouble(0.10f, 0.45f),
							(float)ThreadLocalRandom.current().nextDouble(0.05f, 0.15f));
					switch(seg) {
						case 0:
							if(Math.random() < 0.75f)
								addObject(new TTree((int)tileX, (int)tileY));
							break;
						case 1:
							break;
						case 2:
							addObject(new TBush((int)tileX, (int)tileY));
							break;
						case 3:
							if(Math.random() < 0.75f)
								addObject(new TFlowers((int)tileX, (int)tileY));
							break;
					}
					break;
				case TBiome.ROCK_LEVEL:
					break;
			}
			cachedTerrainData.put(hash, terrainHeight);
			return terrainHeight;
		} else return cachedTerrainData.get(hash);
	}
	
	/**
	 * Adds a new {@link TObject} to this chunk's jurisdiction.
	 */
	public void addObject(TObject object) {
		if(object instanceof TMob) 
			throw new IllegalArgumentException("[Terrafort Game Engine] TMob's aren't managed by TChunks. They can only be managed by a TWorld. Please use TWorld.addObject(TMob) instead.");
		cachedObjectData.add(object);
		addObjectRuntime(object);
	}
	
	/**
	 * Removes an object from this chunk's jurisdiction.
	 */
	public void removeObject(TObject object) {
		removeObjectRuntime(object);
		removeAbstractObject(object);
	}
	
	/**
	 * Transfers all data of object and existing runtime to this chunk's jurisdiction without destroying and rebuilding the body.
	 */
	public void transferObject(TObject object, TObjectRuntime runtime) {
		if(object instanceof TMob) 
			throw new IllegalArgumentException("[Terrafort Game Engine] TMob's aren't managed by TChunks. They can only be managed by a TWorld. Please use TWorld.addObject(TMob) instead.");
		cachedObjectData.add(object);
		concrete.addObjectRuntime(runtime);
	}
	
	/**
	 * Called to remove the abstract object, usually from the runtime chunk because it is already taking care of the runtime object.
	 */
	public void removeAbstractObject(TObject object) {
		cachedObjectData.remove(object);
	}
	
	/**
	 * Register an object runtime with abstract object.
	 */
	public void addObjectRuntime(TObject object) {
		if(object instanceof TMob) 
			throw new IllegalArgumentException("[Terrafort Game Engine] TMob's aren't managed by TChunks. They can only be managed by a TWorld. Please use TWorld.addObject(TMob) instead.");
		concrete.createObjectRuntimeFromAbstract(object);
	}
	
	/**
	 * Unregisters an object runtime with abstract object.
	 */
	public void removeObjectRuntime(TObject object) {
		concrete.removeObjectRuntimeFromAbstract(object);
	}
	
	/**
	 * Whether or not the chunk contains a referenced abstract object.
	 * @param object
	 * @return
	 */
	public boolean containsAbstract(TObject object) {
		return cachedObjectData.contains(object);
	}
	
	/**
	 * The parent world of this chunk.
	 */
	public TWorld getParent() {
		return parent;
	}
	
	/**
	 * The non-serializable physical representation of this chunk data.
	 */
	public TChunkRuntime getPhysical() {
		return concrete;
	}

	@Override
	public void dispose() {
		concrete.dispose();
	}

}
