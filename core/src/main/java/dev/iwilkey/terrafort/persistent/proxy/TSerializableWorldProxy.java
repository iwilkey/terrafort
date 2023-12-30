package dev.iwilkey.terrafort.persistent.proxy;

import java.util.HashMap;
import java.util.Map;

import dev.iwilkey.terrafort.obj.world.TChunk;
import dev.iwilkey.terrafort.obj.world.TWorld;
import dev.iwilkey.terrafort.persistent.TSerializableProxy;

/**
 * Represents a valid {@link TWorld} state.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSerializableWorldProxy extends TSerializableProxy {

	private static final long serialVersionUID = -5728193861860607552L;
	
	private final String                                 worldName;
	private final long                                   seed;            
	private final float                                  worldTime;
	private final long                                   wave;
	private final HashMap<Long, TSerializableChunkProxy> chunks;

	/**
	 * Automatically serializes a snapshot of a given {@link TWorld}, recording only
	 *  the data important enough to recreate the object state later.
	 */
	public TSerializableWorldProxy(final TWorld world) {
		super(world);
		// copy metadata...
		worldName = world.getWorldName();
		seed      = world.getSeed();
		worldTime = world.getWorldTime();
		wave      = world.getWave();
		// copy chunks...
		chunks = new HashMap<>();
		for(final Map.Entry<Long, TChunk> chunk : world.getChunkMemory().entrySet())
			chunks.put(chunk.getKey(), new TSerializableChunkProxy(chunk.getValue()));
	}
	
	public String getWorldName() { 
		return worldName;
	}
	
	public long getSeed() {
		return seed;
	}
	
	public float getWorldTime() {
		return worldTime;
	}
	
	public long getWave() {
		return wave;
	}
	
	public HashMap<Long, TSerializableChunkProxy> getChunkProxy() {
		return chunks;
	}

}
