package dev.iwilkey.terrafort.obj.world;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.entity.element.TNaturalElement;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.entity.tile.TBuildingTile;
import dev.iwilkey.terrafort.obj.entity.tile.TFloorTile;
import dev.iwilkey.terrafort.obj.particulate.TParticulate;
import dev.iwilkey.terrafort.persistent.proxy.TSerializableChunkProxy;

/**
 * Spatial partition of the infinite world of Terrafort. Caches known tile heights and hashes terraform requests for a chunk of a given
 * {@link TWorld}. Manages objects within its bounds.
 * @author Ian Wilkey (iwilkey)
 */
public final class TChunk {

	public static final int   CHUNK_SIZE        = 16;
	
	private final TWorld                         world;
	
	private final HashMap<Long, Integer>         tdat   = new HashMap<>();
	private final HashMap<Long, TBuildingTile>   btdat  = new HashMap<>();
	private final HashMap<Long, TFloorTile>      fdat   = new HashMap<>();
	private final Array<TObject>                 odat   = new Array<>();
	private final Array<TObject>                 odatGC = new Array<>();
	
	private final int                            chunkX;
	private final int                            chunkY;
	
	private       boolean                        dormant;
	
	/**
	 * Create a new chunk with no previous data; a "clean" chunk.
	 */
	public TChunk(TWorld world, int chunkX, int chunkY) {
		this.world  = world;
		this.chunkX = chunkX;
		this.chunkY = chunkY;
		dormant     = false;
	}
	
	/**
	 * Load a chunk from a serialized chunk proxy.
	 */
	public TChunk(TWorld world, TSerializableChunkProxy proxy) {
		this.world = world;
		chunkX     = proxy.getChunkX();
		chunkY     = proxy.getChunkY();
		dormant    = false;
		
		// Load in the terrain data hash map...
		
		// Load in all the objects from the object array...
		// This one is gonna take forever.
		
	}
	
	public int getChunkX() {
		return chunkX;
	}
	
	public int getChunkY() {
		return chunkY;
	}
	
	public HashMap<Long, Integer> getTerrainDataMap() {
		return tdat;
	}
	
	public HashMap<Long, TBuildingTile> getBuildingTileMap() {
		return btdat;
	}
	
	public HashMap<Long, TFloorTile> getFloorTileMap() {
		return fdat;
	}
	
	public Array<TObject> getActiveObjects() {
		return odat;
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
		for(final TObject obj : odat)
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
		for(final TObject obj : odat)
			if(!obj.isEnabled())
				obj.maximize();
		dormant = false;
	}
	
	/**
	 * Forcefully destroy all the {@link TChunk}'s data forever. Do not use this in-game, only during engine state transitions.
	 */
	public void destroy() {
		for(final TObject obj : odat)
				obj.minimize();
		odat.clear();
		odatGC.clear();
		tdat.clear();
		dormant = true;
	}
	
	/**
	 * Registers a {@link TObject} to be the responsibility of this {@link TChunk}.
	 */
	public void register(final TObject obj) {
		// keep track of building tiles...
		if(obj instanceof TBuildingTile) {
			final int tx    = Math.round(obj.getActualX() / TTerrain.TILE_WIDTH);
			final int ty    = Math.round(obj.getActualY() / TTerrain.TILE_HEIGHT);
			final long hash = (((long)tx) << 32) | (ty & 0xffffffffL);
			if(!(obj instanceof TFloorTile))
				btdat.put(hash, (TBuildingTile)obj);
			else fdat.put(hash, (TFloorTile)obj);
		}
		odat.add(obj);
	}
	
	/**
	 * Removes a {@link TObject} from the {@link TChunk}, and thus, the {@link TWorld}.
	 */
	public void remove(final TObject obj) {
		odatGC.add(obj);
	}
	
	/**
	 * Manages all active {@link TObject}'s in this {@link TChunk}'s jurisdiction.
	 */
	public void tick(float dt) {
		for(final TObject obj : odat) {
			obj.sync();
			if(obj instanceof TMob) {
            	// Lifeforms need to be treated differently because
            	// they have the power to move out of the chunk.
            	final int ltx = Math.round(obj.getActualX() / TTerrain.TILE_WIDTH);
        		final int lty = Math.round(obj.getActualY() / TTerrain.TILE_HEIGHT);
        		if(!contains(ltx, lty)) {
        			// this chunk doesn't need to watch over this lifeform anymore, so
        			// we need to transfer it to the next chunk.
        			// System.out.println("Moving player into chunk that contains: " + ltx + ", " + lty);
        			odat.removeValue(obj, false);
        			world.requestChunkThatContains(ltx, lty).register(obj);
        			continue;
        		}
			}
			if(obj instanceof TEntity) {
            	TEntity e = (TEntity)obj;
            	if(!e.isAlive()) {
            		if(e instanceof TPlayer) {
            			world.banditsAreOP();
            		}
            		odatGC.add(e);
            		continue;
            	}
            	e.tick(dt);
            } else if(obj instanceof TParticulate) {
            	TParticulate p = (TParticulate)obj;
            	if(p.isDone()) {
            		odatGC.add(p);
            		continue;
            	}
            	p.age(dt);
            }
		}
		if(odatGC.size != 0) {
			for(TObject o : odatGC) {
				if(o instanceof TEntity) {
	        		((TEntity)o).die();
	        		if(o instanceof TBuildingTile) {
	        			final int tx    = Math.round(o.getActualX() / TTerrain.TILE_WIDTH);
	        			final int ty    = Math.round(o.getActualY() / TTerrain.TILE_HEIGHT);
	        			final long hash = (((long)tx) << 32) | (ty & 0xffffffffL);
	        			if(!(o instanceof TFloorTile))
	        					btdat.remove(hash);
	        			else fdat.remove(hash);
	        		}
				}
				if (o.getPhysicalBody() != null)
	        		world.getPhysicalWorld().destroyBody(o.getPhysicalBody());
			}
			odat.removeAll(odatGC, false);
			odatGC.clear();
		}
	}
	
	/**
	 * Render the contents of the {@link TChunk}.
	 */
	public void render() {
		for(final TObject obj : odat)
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
	 * @param tileX the tile x coordinate.
	 * @param tileY the tile y coordinate.
	 * @param terrainHeight the height to set the tile.
	 */
	public void setTerrainDataAt(int tileX, int tileY, int terrainHeight) {
		if(!contains(tileX, tileY)) 
			return;
		terrainHeight = (int)TMath.clamp(terrainHeight, 0, TTerrain.TERRAIN_LEVELS - 1);
		long hash     = (((long)tileX) << 32) | (tileY & 0xffffffffL);
		tdat.put(hash, terrainHeight);
	}

	/**
	 * Returns the chunk registered tile height at any given pair of tile coordinates.
	 * @param tileX the tile x coordinate.
	 * @param tileY the tile y coordinate.
	 */
	public int getTerrainDataAt(int tileX, int tileY) {
		if(!contains(tileX, tileY)) 
			return TTerrain.TERRAIN_LEVELS - 1;
		long hash = (((long)tileX) << 32) | (tileY & 0xffffffffL);
		if(!tdat.containsKey(hash)) {
			final int tileZ   = TTerrain.requestTerrainHeight(world.getSeed(), tileX, tileY);
			tdat.put(hash, tileZ);
			final TNaturalElement element = TTerrain.requestNaturalElement(world, tileX, tileY, tileZ);
			if(element != null)
				register(element);
		}
		if(getFloorTileDataAt(tileX, tileY) != null || getBuildingTileDataAt(tileX, tileY) != null)
			return TTerrain.BUILDING_TILE;
		return tdat.get(hash);
	}
	
	/**
	 * Returns the {@link TBuildingTile} that the {@link TPlayer} placed in the chunk given tile coordinates. 
	 * Returns null if no building tile is there.
	 * @param tileX the tile x coordinate.
	 * @param tileY the tile y coordinate.
	 */
	public TBuildingTile getBuildingTileDataAt(int tileX, int tileY) {
		if(!contains(tileX, tileY)) 
			return null;
		long hash = (((long)tileX) << 32) | (tileY & 0xffffffffL);
		if(btdat.containsKey(hash))
			return btdat.get(hash);
		else return null;
	}
	
	/**
	 * Returns the {@link TFloorTile} that the {@link TPlayer} placed in the chunk given tile coordinates. 
	 * Returns null if no building tile is there.
	 * @param tileX the tile x coordinate.
	 * @param tileY the tile y coordinate.
	 */
	public TFloorTile getFloorTileDataAt(int tileX, int tileY) {
		if(!contains(tileX, tileY)) 
			return null;
		long hash = (((long)tileX) << 32) | (tileY & 0xffffffffL);
		if(fdat.containsKey(hash))
			return fdat.get(hash);
		else return null;
	}

}
