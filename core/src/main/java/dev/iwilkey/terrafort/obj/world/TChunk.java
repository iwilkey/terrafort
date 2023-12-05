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
	 * Queries the chunk to see if it contains the given a tile coordinate.
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return whether or not the chunk contains the given tile coordinate.
	 */
	public boolean contains(int x, int y) {
		return (int)(x / CHUNK_SIZE) == chunkX || (int)(y / CHUNK_SIZE) == chunkY;
	}
	
	/**
	 * Updates the chunk terrain data at given tile coordinates to a given height.
	 * 
	 * <p>
	 * Note: z value will be clamped to [0, TERRAIN_LEVELS - 1].
	 * </p>
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @param z the height to set the tile.
	 */
	public void setTileHeightAt(int x, int y, int z) {
		if(!contains(x, y)) 
			return;
		z         = (int)TMath.clamp(z, 0, TTerrainRenderer.TERRAIN_LEVELS - 1);
		long hash = (((long)x) << 32) | (y & 0xffffffffL);
		terrainData.put(hash, z);
	}
	
	/**
	 * Returns the tile height at any given pair of tile coordinates. Will return -1 if the pair of
	 * coordinates is not in the chunk.
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return the z value of the tile.
	 */
	public int getTileHeightAt(int x, int y) {
		// check if that value is suppose to be in the chunk.
		if(!contains(x, y)) 
			return TTerrainRenderer.TERRAIN_LEVELS - 1;
		long hash = (((long)x) << 32) | (y & 0xffffffffL);
		if(!terrainData.containsKey(hash)) {
			// we know this coordinate is represented by the chunk, so we need to hash it.
			double v  = TNoise.get(this.world.getSeed(), x * 0.01f, y * 0.01f);
			v         = (v + 1) / 2;
			int vq    = TMath.quantize(v, TTerrainRenderer.TERRAIN_LEVELS);
			terrainData.put(hash, vq);
		}
		return terrainData.get(hash);
	}
	
}
