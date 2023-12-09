package dev.iwilkey.terrafort.obj.world;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.math.TNoise;

import dev.iwilkey.terrafort.obj.entity.element.TFlower;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.entity.element.TBoulder;
import dev.iwilkey.terrafort.obj.entity.element.TBush;
import dev.iwilkey.terrafort.obj.entity.element.TTree;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;
import dev.iwilkey.terrafort.obj.particle.TParticle;

/**
 * Spatial partition of the infinite world of Terrafort. Caches known tile heights and hashes terraform requests for a chunk of a given
 * {@link TSinglePlayerWorld}. Manages objects within its bounds.
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
	private final TSinglePlayerWorld                         world;
	private final HashMap<Long, Integer>         terrainData;
	private final Array<TObject>                 objData;
	private final Array<TObject>                 objDataGarbageCollector;
	private final int                            chunkX;
	private final int                            chunkY;
	
	private boolean                              dormant;
	
	/**
	 * Create a new chunk with no previous data; a "clean" chunk.
	 */
	public TChunk(TSinglePlayerWorld world, int chunkX, int chunkY) {
		this.world              = world;
		this.chunkX             = chunkX;
		this.chunkY             = chunkY;
		terrainData             = new HashMap<>();
		objData                 = new Array<>();
		objDataGarbageCollector = new Array<>();
		random                  = new Random(world.getSeed());
		dormant                 = false;
	}
	
	public int getChunkX() {
		return chunkX;
	}
	
	public int getChunkY() {
		return chunkY;
	}
	
	/**
	 * Query if the chunk is dormant or not.
	 */
	public boolean isDormant() {
		return dormant;
	}
	
	/**
	 * Set the chunk dormant. That is, minimize the overhead as much as possible while
	 * still keeping valuable {@link TObject} data intact. This is a great optimization.
	 */
	public void sleep() {
		if(dormant)
			return;
		for(final TObject obj : objData)
			if(obj.isEnabled())
				obj.minimize();
		dormant = true;
	}
	
	/**
	 * Set the chunk active again after it has been sleeping.
	 */
	public void wake() {
		if(!dormant)
			return;
		for(final TObject obj : objData)
			if(!obj.isEnabled())
				obj.maximize();
		dormant = false;
	}
	
	/**
	 * Forcefully destroy all the {@link TChunk}'s data forever. Do not use this in-game, only during engine state transitions.
	 */
	public void destroy() {
		for(final TObject obj : objData)
				obj.minimize();
		objData.clear();
		objDataGarbageCollector.clear();
		terrainData.clear();
		dormant = true;
	}
	
	/**
	 * Registers a {@link TObject} to be the responsibility of this {@link TChunk}.
	 */
	public void register(final TObject obj) {
		objData.add(obj);
	}
	
	/**
	 * Removes a {@link TObject} from the {@link TChunk}, and thus, the {@link TSinglePlayerWorld}.
	 */
	public void remove(final TObject obj) {
		objDataGarbageCollector.add(obj);
	}
	
	/**
	 * Manages all active {@link TObject}'s in this {@link TChunk}'s jurisdiction.
	 */
	public void tick(float dt) {
		for(final TObject obj : objData) {
			if(!obj.isEnabled())
				continue;
			obj.sync();
			if(obj instanceof TLifeform) {
            	// Lifeforms need to be treated differently because
            	// they have the power to move out of the chunk.
            	final int ltx = Math.round(obj.getActualX() / TTerrainRenderer.TERRAIN_TILE_WIDTH);
        		final int lty = Math.round(obj.getActualY() / TTerrainRenderer.TERRAIN_TILE_HEIGHT);
        		if(!contains(ltx, lty)) {
        			// this chunk doesn't need to watch over this lifeform anymore, so
        			// we need to transfer it to the next chunk.
        			// System.out.println("Moving player into chunk that contains: " + ltx + ", " + lty);
        			objData.removeValue(obj, false);
        			world.requestChunkThatContains(ltx, lty).register(obj);
        			continue;
        		}
			}
			if(obj instanceof TEntity) {
            	TEntity e = (TEntity)obj;
            	if(!e.isAlive()) {
            		objDataGarbageCollector.add(e);
            		continue;
            	}
            	e.tick(dt);
            } else if(obj instanceof TParticle) {
            	TParticle p = (TParticle)obj;
            	if(p.isDone()) {
            		objDataGarbageCollector.add(p);
            		continue;
            	}
            	p.tick(dt);
            }
		}
		if(objDataGarbageCollector.size != 0) {
			for(TObject o : objDataGarbageCollector) {
				if(o instanceof TEntity)
	        		((TEntity)o).die();
				if (o.getPhysicalBody() != null)
	        		world.getPhysicalWorld().destroyBody(o.getPhysicalBody());
			}
			objData.removeAll(objDataGarbageCollector, false);
			objDataGarbageCollector.clear();
		}
	}
	
	/**
	 * Render the contents of the {@link TChunk}.
	 */
	public void render() {
		for(final TObject obj : objData)
			TGraphics.draw(obj);
	}
	
	/**
	 * Queries the chunk to see if it contains the given a tile coordinate.
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return whether or not the chunk contains the given tile coordinate.
	 */
	public boolean contains(int x, int y) {
		return (int)(x / CHUNK_SIZE) == chunkX && (int)(y / CHUNK_SIZE) == chunkY;
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
			double layer0     = TNoise.get(this.world.getSeed(), x * 0.01f, y * 0.01f);
			layer0            = (layer0 + 1) / 2;
			double layer0Norm = 0.0000001f + (0.001f - 0.0000001f) * layer0;
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
				element       = TMath.segment(layer3, 4, 0.40f, 0.50f, 0.60f);
			}
			switch(tile) {
				case TTerrainRenderer.GRASS_TILE:
					switch(element) {
						case TREE_ELEMENT:
							if(random.nextFloat() < 0.80f)
								register(new TTree(world, x, y));
							break;
						case BUSH_ELEMENT:
							if(random.nextFloat() < 0.70f)
								register(new TBush(world, x, y));
							break;
						case FLOWER_ELEMENT:
							if(random.nextFloat() < 0.60f)
								register(new TFlower(world, x, y));
							break;
					}
					break;
				case TTerrainRenderer.ASPHALT_TILE:
				case TTerrainRenderer.STONE_TILE:
					switch(element) {
						case BOULDER_ELEMENT:
							register(new TBoulder(world, x, y));
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
	public float tileDistTo(final TPlayer player) {
		final int tw = TTerrainRenderer.TERRAIN_TILE_WIDTH;
		final int th = TTerrainRenderer.TERRAIN_TILE_HEIGHT;
		float px = Math.round(player.getActualX() / tw);
		float py = Math.round(player.getActualY() / th);
		float xx = chunkX * CHUNK_SIZE;
		float yy = chunkY * CHUNK_SIZE;
		float dx2 = (px - xx) * (px - xx);
		float dy2 = (py - yy) * (py - yy);
		return (float)Math.sqrt(dx2 + dy2);
	}
}
