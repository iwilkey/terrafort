package dev.iwilkey.terrafort.obj.world;

import java.util.HashMap;
import java.util.Random;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.math.TNoise;

import dev.iwilkey.terrafort.obj.entity.element.TFlower;
import dev.iwilkey.terrafort.obj.entity.element.TBoulder;
import dev.iwilkey.terrafort.obj.entity.element.TTree;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;

/**
 * Spatial partition of the infinite world of Terrafort. Caches known tile heights and hashes terraform requests for a chunk of a given
 * {@link TWorld}.
 * @author Ian Wilkey (iwilkey)
 */
public final class TChunk {
	
	public static final int   CHUNK_SIZE        = 16;
	
	public static final float STONE_REGION      = 0.40f;
	public static final float ASPHALT_REGION    = 0.45f;
	public static final float GRASS_REGION      = 0.70f;
	public static final float SAND_REGION       = 0.75f;
	public static final int   TREE_ELEMENT      = 0;
	public static final int   FLOWER_ELEMENT    = 1;
	public static final int   BUSH_ELEMENT      = 2;
	public static final int   BOULDER_ELEMENT   = 3;
	
	private final Random                         random;
	private final TWorld                         world;
	private final HashMap<Long, Integer>         terrainData;
	private final int                            chunkX;
	private final int                            chunkY;
	
	public TChunk(TWorld world, int chunkX, int chunkY) {
		this.world   = world;
		this.chunkX  = chunkX;
		this.chunkY  = chunkY;
		terrainData  = new HashMap<>();
		random = new Random(world.getSeed());
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
	 * Updates the chunk terrain data at given tile coordinates to a given height. Called internally.
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
	 * Returns the tile height at any given pair of tile coordinates.
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return the z value of the tile.
	 */
	public int getTileHeightAt(int x, int y) {
		
		// check if that value is suppose to be in the chunk.
		
		if(!contains(x, y)) 
			return TTerrainRenderer.TERRAIN_LEVELS - 1;
		
		// it is, so we either return the hash or generate it.
		
		long hash = (((long)x) << 32) | (y & 0xffffffffL);
		
		if(!terrainData.containsKey(hash)) {
			double layer0     = TNoise.get(this.world.getSeed(), x * 0.001f, y * 0.001f);
			layer0            = (layer0 + 1) / 2;
			double layer0Norm = 0.0001f + (0.01f - 0.0001f) * layer0;
			double layer1     = TNoise.get(this.world.getSeed(), x * layer0Norm, y * layer0Norm);
			layer1            = (layer1 + 1) / 2;
			int tile          = TMath.segment(layer1, 
											  TTerrainRenderer.TERRAIN_LEVELS,
											  STONE_REGION,
											  ASPHALT_REGION,
											  GRASS_REGION,
											  SAND_REGION
											 );
			terrainData.put(hash, tile);
			double layer0Norm2 =  0.01f + (0.1f - 0.01f) * layer0;
			int    element     = -1;
			if(random.nextFloat() < 0.75f) {
				double layer3 = TNoise.get(this.world.getSeed() - 1, x * layer0Norm2, y * layer0Norm2);
				layer3        = (layer3 + 1) / 2;
				element       = TMath.segment(layer3, 4);
			}
			switch(tile) {
				case TTerrainRenderer.GRASS_TILE:
					switch(element) {
						case TREE_ELEMENT:
							if(random.nextFloat() < 0.20f)
								world.addObject(new TTree(world, x, y));
							break;
						case BUSH_ELEMENT:
							if(random.nextFloat() < 0.20f)
								world.addObject(new TTree(world, x, y));
							break;
						case FLOWER_ELEMENT:
							if(random.nextFloat() < 0.60f)
								world.addObject(new TFlower(world, x, y));
							break;
					}
					break;
				
				case TTerrainRenderer.ASPHALT_TILE:
				case TTerrainRenderer.STONE_TILE:
					
					switch(element) {
						case BOULDER_ELEMENT:
							world.addObject(new TBoulder(world, x, y));
							break;
					}
					
					break;
					
			}
		}
		return terrainData.get(hash);
	}
	
	/**
	 * Returns how far away the center of this chunk is from the given player.
	 * @param player the player to test.
	 * @return the average distance to from this chunk to the player.
	 */
	public float distTo(final TPlayer player) {
		float px = player.getRenderX();
		float py = player.getRenderY();
		float xx = (chunkX * TTerrainRenderer.TERRAIN_TILE_WIDTH);
		float yy = (chunkY * TTerrainRenderer.TERRAIN_TILE_HEIGHT);
		float dx2 = (px - xx) * (px - xx);
		float dy2 = (py - yy) * (py - yy);
		return (float)Math.sqrt(dx2 + dy2);
	}
	
}
