package dev.iwilkey.terrafort.obj.world;

import java.util.HashMap;

import box2dLight.Light;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.math.TNoise;

/**
 * Manages local lighting, caches known tile heights and hashes terraform requests for a chunk of a given
 * {@link TWorld}.
 * @author Ian Wilkey (iwilkey)
 */
public final class TChunk {
	
	public static final int CHUNK_SIZE = 16;
	
	private final TWorld                 world;
	private final HashMap<Long, Integer> terrainData;
	private final HashMap<Long, Light>   lightingData;
	private final int                    chunkX;
	private final int                    chunkY;
	
	public TChunk(TWorld world, int chunkX, int chunkY) {
		this.world   = world;
		this.chunkX  = chunkX;
		this.chunkY  = chunkY;
		terrainData  = new HashMap<>();
		lightingData = new HashMap<>();
	}
	
	public int getChunkX() {
		return chunkX;
	}
	
	public int getChunkY() {
		return chunkY;
	}
	
	/**
	 * Returns the tile height at any given pair of tile coordinates. Will return -1 if the pair of
	 * coordinates is not in the chunk.
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return the z value of the tile.
	 */
	public int getTileHeightAt(int x, int y) {
		long hash = (((long)x) << 32) | (y & 0xffffffffL);
		if(!terrainData.containsKey(hash)) {
			// check if that value is suppose to be in the chunk...
			int cx = (int)(x / CHUNK_SIZE);
			int cy = (int)(y / CHUNK_SIZE);
			if(cx == chunkX && cy == chunkY) {
				// it is, so calculate and hash it.
				double v  = TNoise.get(this.world.getSeed(), x * 0.01f, y * 0.01f);
				v         = (v + 1) / 2;
				int vq    = TMath.quantize(v, TTerrainRenderer.TERRAIN_HEIGHT);
				terrainData.put(hash, vq);
			} else return 3;
		}
		return terrainData.get(hash);
	}
	
}
