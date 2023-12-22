package dev.iwilkey.terrafort.obj.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TEngine;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.math.TCollisionManifold;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.mob.TBandit;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.entity.tile.TBuildingTile;
import dev.iwilkey.terrafort.obj.entity.tile.TFloorTile;
import dev.iwilkey.terrafort.state.TMainMenuState;

/**
 * A physical space that efficiently manages {@link TChunk}s, global and local forces, and dynamic lighting all active 
 * within a Single-player game of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TWorld implements Disposable {

	public static final short           LIGHTING_RAYS           = 16;
	public static final short           CHUNK_CULLING_THRESHOLD = 4;
	public static final float           DAY_NIGHT_CYCLE_PERIOD  = 60.0f;

	private final World              	world;
	private final long                  seed;
	
	private final HashMap<Long, TChunk> calloc;  // chunk allocation
	private final Set<Long>             acalloc; // active chunk allocation
	private final Set<Long>             ucalloc; // indeterminate chunk allocation

	private final Box2DDebugRenderer    debugRenderer;
	private final RayHandler            lightRenderer;
	
	private TPlayer                     clientPlayer;
	
	private boolean                     debug;
	private float                       worldTime;
	private long                        wave;
	private boolean                     day;
	private boolean                     dusk;
	private boolean                     night;
	private boolean                     dawn;
	private int                         dormantChunks;
	
	public TWorld(long seed) {
		this.seed                       = seed;
		world                           = new World(new Vector2(0, 0), false);
		calloc                          = new HashMap<>();
		acalloc                         = new HashSet<>();
		ucalloc                         = new HashSet<>();
		lightRenderer                   = new RayHandler(world);
		debugRenderer                   = new Box2DDebugRenderer();
		debug                           = false;
		clientPlayer                    = null;
		worldTime                       = DAY_NIGHT_CYCLE_PERIOD;
		day                             = true;
		dusk                            = false;
		night                           = false;
		dawn                            = false;
		wave                            = 16;
		dormantChunks                   = 0;
		lightRenderer.setAmbientLight(0.1f, 0.1f, 0.1f, 0.5f);
		world.setContactListener(new TCollisionManifold());
	}
	
	/**
	 * @return the active Box2D world.
	 */
	public World getPhysicalWorld() {
		return world;
	}
	
	/**
	 * Adds and returns a point light to the world.
	 */
	public PointLight addPointLight(int x, int y, float r, Color color) {
		final PointLight ret = new PointLight(lightRenderer, LIGHTING_RAYS, color, r, x, y);
		return ret;
	}
	
	/**
	 * Registers and returns an object to the world. Will be active until explicitly removed.
	 * @param obj the object to add.
	 * @return the object added. NOTE: Keep track of this object, as it must be explicitly removed.
	 */
	public TObject addObject(final TObject obj) {
		if(obj instanceof TPlayer) 
			clientPlayer = (TPlayer)obj;
		final int xx = Math.round(obj.getActualX() / TTerrain.TILE_WIDTH);
		final int yy = Math.round(obj.getActualY() / TTerrain.TILE_HEIGHT);
		final TChunk chunk = requestChunkThatContains(xx, yy);
		chunk.register(obj);
		return obj;
	}
	
	/**
	 * Removes a {@link TObject} from the world.
	 * @param obj the object to remove.
	 */
	public void removeObject(final TObject obj) {
		final int xx = Math.round(obj.getActualX() / TTerrain.TILE_WIDTH);
		final int yy = Math.round(obj.getActualY() / TTerrain.TILE_HEIGHT);
		final TChunk chunk = requestChunkThatContains(xx, yy);
		chunk.remove(obj);
	}
	
	/**
	 * Runs Terrafort's spatial partitioning algorithm given a center chunk to provide efficient infinite terrain generation.
	 */
	private void infinity(float dt, int pcx, int pcy) {
		world.step(1 / 60f, 4, 8);
		updateDayNightCycle(dt);
		// Optimization: since the chunks are hashed by position, I don't need to search through the entire list of loaded chunks
		// to find what chunks are closest to the player...
		acalloc.clear();
		for(int cx = pcx - CHUNK_CULLING_THRESHOLD; cx < pcx + CHUNK_CULLING_THRESHOLD; cx++) {
			for(int cy = pcy - CHUNK_CULLING_THRESHOLD; cy < pcy + CHUNK_CULLING_THRESHOLD; cy++) {
				long hash = (((long)cx) << 32) | (cy & 0xffffffffL);
				final TChunk currentChunk = calloc.get(hash);
				if(currentChunk == null)
					continue;
				acalloc.add(hash);
				ucalloc.add(hash);
				if(currentChunk.isDormant())
					currentChunk.wake();
				currentChunk.tick(dt);
			}
		}
		// Optimization: check the difference between watchlist and the loadedChunks to know what to sleep instead of looking through
		// the entire calloc.
		final Set<Long> sleepers = new HashSet<>();
		for(long hash : ucalloc) 
			if(!acalloc.contains(hash)) {
				calloc.get(hash).sleep();
				sleepers.add(hash);
			}
		ucalloc.removeAll(sleepers);
		dormantChunks = calloc.size() - acalloc.size();	
		collectEngineMetrics();
	}
	
	/**
	 * Steps the world simulation forward one tick.
	 * @param dt the change in time since the last tick.
	 */
	public void update(float dt) {
		final int pcx = clientPlayer.getCurrentTileX() / TChunk.CHUNK_SIZE;
		final int pcy = clientPlayer.getCurrentTileY() / TChunk.CHUNK_SIZE;
		infinity(dt, pcx, pcy);
		render(clientPlayer.getCurrentTileX(), clientPlayer.getCurrentTileY());
	}
	
	/**
	 * Steps the world simulation forward one tick using given tile coordinates.
	 * @param dt the change in time since the last tick.
	 */
	public void update(float dt, int ctx, int cty) {
		final int pcx = ctx / TChunk.CHUNK_SIZE;
		final int pcy = cty / TChunk.CHUNK_SIZE;
		infinity(dt, pcx, pcy);
		render(ctx, cty);
	}
	
	float btmt = 0.0f;
	
	/**
	 * Renders the world's objects, Box2D debug information, and dynamic lighting.
	 */
	private void render(int ctx, int cty) {
		TTerrain.render(this, ctx, cty);
		for(long hash : acalloc)
			calloc.get(hash).render();
		if(debug) debugRenderer.render(world, TGraphics.CAMERA.combined);
		lightRenderer.setCombinedMatrix(TGraphics.CAMERA);
		lightRenderer.updateAndRender();
		if(playerdead) {
			btmt += TClock.dt();
			if(btmt >= 5.0f) {
				TEngine.setState(new TMainMenuState());
				btmt = 0.0f;
			}
		}
	}
	
	/**
	 * Get the tile height at any given tile coordinates. Will always return a non-negative value, and
	 * will load a chunk that contains (x, y) if not loaded. Do not use this method frequently, if ever.
	 * 
	 * <p>
	 * This function must be (and is) highly optimized and should always be called internally. Please do not change the algorithm unless you know
	 * exactly what you are doing.
	 * </p>
	 * 
	 * @param tileX the tile x coordinate.
	 * @param tileY the tile y coordinate.
	 * @return the z value of the tile.
	 */
	public int getOrGenerateTileHeightAt(int tileX, int tileY) {
		return requestChunkThatContains(tileX, tileY).getTerrainDataAt(tileX, tileY);
	}
	
	/**
	 * Checks for a tile height at a given tile coordinate. If the chunk that contains it isn't loaded yet, -1 is returned.
	 * 
	 * <p>
	 * For simple terrain height queries, use this method: it's lightweight and quick.
	 * </p>
	 */
	public int checkTileHeightAt(int tileX, int tileY) {
		final int chunkX     = tileX / TChunk.CHUNK_SIZE;
		final int chunkY     = tileY / TChunk.CHUNK_SIZE;
		final long chunkHash = (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
		if(calloc.containsKey(chunkHash))
			return requestChunkThatContains(tileX, tileY).getTerrainDataAt(tileX, tileY);
		return -1;
	}
	
	/**
	 * Checks for a {@link TBuildingTile} at a given tile coordinate. If the chunk that contains it isn't loaded yet, 
	 * null is returned.
	 */
	public TBuildingTile checkBuildingTileAt(int tileX, int tileY) {
		final int chunkX     = tileX / TChunk.CHUNK_SIZE;
		final int chunkY     = tileY / TChunk.CHUNK_SIZE;
		final long chunkHash = (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
		if(calloc.containsKey(chunkHash))
			return requestChunkThatContains(tileX, tileY).getBuildingTileDataAt(tileX, tileY);
		return null;
	}
	
	/**
	 * Checks for a {@link TFloorTile} at a given tile coordinate. If the chunk that contains it isn't loaded yet, 
	 * null is returned.
	 */
	public TFloorTile checkFloorTileAt(int tileX, int tileY) {
		final int chunkX     = tileX / TChunk.CHUNK_SIZE;
		final int chunkY     = tileY / TChunk.CHUNK_SIZE;
		final long chunkHash = (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
		if(calloc.containsKey(chunkHash))
			return requestChunkThatContains(tileX, tileY).getFloorTileDataAt(tileX, tileY);
		return null;
	}

	/**
	 * Updates the world terrain data at given tile coordinates to a given height.
	 * 
	 * <p>
	 * Note: z value will be clamped to [0, TERRAIN_MAX_HEIGHT - 1].
	 * </p>
	 * @param tileX the tile x coordinate.
	 * @param tileY the tile y coordinate.
	 * @param z the height to set the tile.
	 */
	public void setTileHeightAt(int tileX, int tileY, int z) {
		requestChunkThatContains(tileX, tileY).setTerrainDataAt(tileX, tileY, z);
	}
	
	/**
	 * Requests and returns the {@link TChunk} that contains the given tile coordinates. If the chunk is not
	 * loaded in the chunk cache, it will be loaded automatically.
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return the chunk that contains the given tile coordinates.
	 */
	public TChunk requestChunkThatContains(int x, int y) {
		int chunkX = (x / TChunk.CHUNK_SIZE);
		int chunkY = (y / TChunk.CHUNK_SIZE);
		long hash  = (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
		if(!calloc.containsKey(hash)) {
			final TChunk newChunk = new TChunk(this, chunkX, chunkY);
			calloc.put(hash, newChunk);
		}
		return calloc.get(hash);
	}
	
	public long getSeed() {
		return seed;
	}
	
	public HashMap<Long, TChunk> getChunkMemory() {
		return calloc;
	}
	
	public Set<Long> getLoadedChunks() {
		return acalloc;
	}
	
	public boolean isDay() {
		return day;
	}
	
	public boolean isDusk() {
		return dusk;
	}
	
	public boolean isNight() {
		return night;
	}
	
	public boolean isDawn() {
		return dawn;
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	public long getWave() {
		return wave;
	}
	
	/**
	 * Converts world time to a 12-hour clock format.
	 */
	public String worldTimeTo12hrClock() {
	    float th = 24.0f * (worldTime / DAY_NIGHT_CYCLE_PERIOD);
	    int   h  = (int)th % 12;
	    int   m  = (int)(60 * (th - (int) th));
	    if(h == 0) 
	    	h = 12;
	    final String amPm = th < 12 || th >= 24 ? "PM" : "AM";
	    return String.format("%02d:%02d %s", h, m, amPm);
	}
	
	public TPlayer getPlayer() {
		return clientPlayer;
	}
	
	/**
	 * Set to render Box2D debug information.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	boolean playerdead = false;
	
	public void banditsAreOP() {
		playerdead = true;
	}
	
	boolean wavechecl = false;
	
	/**
	 * Updates dynamic lighting to represent the world time. Cycles through day, dusk, night, and dawn.
	 */
	private void updateDayNightCycle(float dt) {
	    if(clientPlayer != null) {
			worldTime += dt;
		    if(Math.abs(worldTime - (DAY_NIGHT_CYCLE_PERIOD / 2f)) <= 0.5f && wavechecl) {
		    	wave++;
		    	for(int i = 0; i < wave; i++) {
		    	    double angle = 2 * Math.PI * (ThreadLocalRandom.current().nextInt(0, (int)wave)) / wave;
		    	    int cx = clientPlayer.getCurrentTileX();
		    	    int cy = clientPlayer.getCurrentTileY();
		    	    int r = ThreadLocalRandom.current().nextInt(TTerrain.TILE_WIDTH * 4, TTerrain.TILE_WIDTH * 8);
		    	    int spawnXPos = cx + (int)(r * Math.cos(angle));
		    	    int spawnYPos = cy + (int)(r * Math.sin(angle));
		    	    addObject(new TBandit(this, spawnXPos, spawnYPos));
		    	}
		    	wavechecl = false;
		    }
		    if(worldTime > DAY_NIGHT_CYCLE_PERIOD) {
		    	wavechecl = true;
		    	worldTime = 0.0f;
		    }
	    }
	    final float progress    = worldTime / DAY_NIGHT_CYCLE_PERIOD;
	    float sunlightIntensity = 0.5f * ((float)Math.cos(2 * Math.PI * progress) + 1.0f);
	    sunlightIntensity       = TMath.clamp(sunlightIntensity, 0.1f, 0.9f);
	    day                     = (progress >= 0.0f && progress < 0.25f) || (progress >= 0.75f && progress <= 1.0f);
	    dusk                    = (progress >= 0.25f && progress < 0.375f);
	    night                   = (progress >= 0.375f && progress < 0.625f);
	    dawn                    = (progress >= 0.625f && progress < 0.75f);
	    lightRenderer.setAmbientLight(0.1f, 0.1f, 0.1f, sunlightIntensity);
	}
	
	/**
	 * Destroys all bodies and objects current active in the world.
	 */
	public void clearAllActiveObjectsAndBodies() {
		for(final TChunk o : calloc.values())
			o.destroy();
		 final Array<Body> others = new Array<>();
		 world.getBodies(others);
		 for(Body b : others)
			 world.destroyBody(b);
	}
	
	@Override
	public void dispose() {
		clearAllActiveObjectsAndBodies();
		calloc.clear();
		lightRenderer.removeAll();
		lightRenderer.dispose();
		collectEngineMetrics();
		world.dispose();
		debugRenderer.dispose();
	}
	
	/**
	 * Updates the metrics in the {@link TEngine}.
	 */
	private void collectEngineMetrics() {
		TEngine.mChunksInMemory = calloc.size();
		TEngine.mChunksDormant = dormantChunks;
		TEngine.mPhysicalBodies = world.getBodyCount();
	}

}
