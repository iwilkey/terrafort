package dev.iwilkey.terrafort.persistent.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.world.TChunk;
import dev.iwilkey.terrafort.persistent.TSerializableProxy;

/**
 * Represents a valid {@link TChunk} state.
 * @author Ian Wilkey (iwilkey)
 */
public class TSerializableChunkProxy extends TSerializableProxy {

	private static final long serialVersionUID = 642981829211874405L;
	
	private final int                                 chunkX;
	private final int                                 chunkY;
	private final HashMap<Long, Integer>              tdat = new HashMap<>();
	private final ArrayList<TSerializableObjectProxy> odat = new ArrayList<>();
	
	public TSerializableChunkProxy(final TChunk target) {
		super(target);
		chunkX = target.getChunkX();
		chunkY = target.getChunkY();
		// copy terrain data...
		for(Map.Entry<Long, Integer> tileDat : target.getTerrainDataMap().entrySet())
			tdat.put(tileDat.getKey(), tileDat.getValue());
		// copy object data...
		for(TObject obj : target.getActiveObjects()) {
			if(obj instanceof TPlayer)
				continue;
			odat.add(new TSerializableObjectProxy(obj));
		}
	}
	
	public int getChunkX() {
		return chunkX;
	}
	
	public int getChunkY() {
		return chunkY;
	}
	
	public HashMap<Long, Integer> getTerrainData() {
		return tdat;
	}
	
	public ArrayList<TSerializableObjectProxy> getObjectData() {
		return odat;
	}

}
