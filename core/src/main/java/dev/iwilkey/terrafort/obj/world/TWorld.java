package dev.iwilkey.terrafort.obj.world;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.math.TCollisionManifold;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.entity.lifeform.TLifeform;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;
import dev.iwilkey.terrafort.obj.particle.TParticle;

/**
 * A physical space that manages {@link TObjects}, global and local forces, and dynamic lighting.
 * @author Ian Wilkey (iwilkey)
 */
public final class TWorld implements Disposable {

	public static final short           IGNORE_LIGHTING         = 0x0001;
	public static final short           LIGHTING_RAYS           = 16;
	public static final float           DAY_NIGHT_CYCLE_PERIOD  = 12000.0f;
	public static final Filter          LIGHTING_COLLISION_MASK = new Filter();
	
	static {
		LIGHTING_COLLISION_MASK.maskBits = (short)(~IGNORE_LIGHTING);
	}

	private final World              	world;
	private final long                  seed;
	private final HashMap<Long, TChunk> loadedChunks;
	private final Array<TObject>        objects;
	private final Array<TObject>        deathrow;
	private final Box2DDebugRenderer    debugRenderer;
	private final RayHandler            lightRenderer;
	
	private TPlayer                     player;
	private boolean                     debug;
	private float                       worldTime;
	private boolean                     day;
	private boolean                     dusk;
	private boolean                     night;
	private boolean                     dawn;

	public TWorld(long seed) {
		this.seed                       = seed;
		world                           = new World(new Vector2(0, 0), false);
		loadedChunks                    = new HashMap<>();
		lightRenderer                   = new RayHandler(world);
		objects                         = new Array<>();
		deathrow                        = new Array<>();
		debugRenderer                   = new Box2DDebugRenderer();
		debug                           = false;
		player                          = null;
		worldTime                       = DAY_NIGHT_CYCLE_PERIOD;
		day                             = true;
		dusk                            = false;
		night                           = false;
		dawn                            = false;
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
		PointLight ret = new PointLight(lightRenderer, LIGHTING_RAYS, color, r, x, y);
		ret.setContactFilter(LIGHTING_COLLISION_MASK);
		return ret;
	}
	
	/**
	 * Registers and returns an object to the world. Will be active until explicitly removed.
	 * @param obj the object to add.
	 * @return the object added. NOTE: Keep track of this object, as it must be explicitly removed.
	 */
	public TObject addObject(final TObject obj) {
		if(obj instanceof TPlayer)
			player = (TPlayer)obj;
		if(obj instanceof TLifeform) 
			obj.getFixture().getFilterData().categoryBits = IGNORE_LIGHTING;
		else obj.getFixture().getFilterData().categoryBits = ~IGNORE_LIGHTING;
		objects.add(obj);
		return obj;
	}
	
	/**
	 * Removes a {@link TObject} from the world.
	 * @param obj the object to remove.
	 */
	public void removeObject(final TObject obj) {
		world.destroyBody(obj.getPhysicalBody());
		objects.removeValue(obj, false);
	}
	
	float time = 0.0f;
	
	/**
	 * Steps the world simulation forward one tick.
	 * @param dt the change in time since the last tick.
	 */
	public void update(float dt) {
		time += dt;
		if(time > 1.0f) {
			System.out.println("Loaded chunks: " + loadedChunks.keySet().size());
			time = 0.0f;
		}
		world.step(dt, 6, 2);
		updateDayNightCycle(dt);
        for(final TObject obj : objects) {
            obj.sync();
            if(obj instanceof TEntity) {
            	TEntity e = (TEntity)obj;
            	if(!e.isAlive()) {
            		deathrow.add(e);
            		continue;
            	}
            	e.tick(dt);
            } else if(obj instanceof TParticle) {
            	TParticle p = (TParticle)obj;
            	if(p.isDone()) {
            		deathrow.add(p);
            		continue;
            	}
            	p.tick(dt);
            }
        }
        for(final TObject e : deathrow) {
        	if(e instanceof TEntity)
        		((TEntity)e).die();
        	if (e.getPhysicalBody() != null)
        		world.destroyBody(e.getPhysicalBody());
        }
        objects.removeAll(deathrow, false);
        deathrow.clear();
	}
	
	/**
	 * Renders the world's objects, Box2D debug information, and dynamic lighting.
	 */
	public void render() {
		TTerrainRenderer.render(this, player);
		for(final TObject obj : objects)
			TGraphics.draw(obj);
		if(debug) debugRenderer.render(world, TGraphics.CAMERA.combined);
		lightRenderer.setCombinedMatrix(TGraphics.CAMERA);
		lightRenderer.updateAndRender();
	}
	
	/**
	 * Get the tile height at any given tile coordinates. Will always return a non-negative value, and
	 * will load a chunk that contains (x, y) if not loaded.
	 * 
	 * <p>
	 * This function must be (and is) highly optimized. Please do not change the algorithm unless you know
	 * exactly what you are doing.
	 * </p>
	 * 
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return the z value of the tile.
	 */
	public int getTileHeightAt(int x, int y) {
		return requestChunkThatContains(x, y).getTileHeightAt(x, y);
	}
	
	/**
	 * Updates the world terrain data at given tile coordinates to a given height.
	 * 
	 * <p>
	 * Note: z value will be clamped to [0, TERRAIN_MAX_HEIGHT - 1].
	 * </p>
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @param z the height to set the tile.
	 */
	public void setTileHeightAt(int x, int y, int z) {
		if(z != 0) {
			// We know it's not stone, so we might need to remove a tile physical.
			TTerrainRenderer.removePhysicalAt(this, x, y);
		}
		requestChunkThatContains(x, y).setTileHeightAt(x, y, z);
	}
	
	public long getSeed() {
		return seed;
	}
	
	public HashMap<Long, TChunk> getChunkCache() {
		return loadedChunks;
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
	
	/**
	 * Set to render Box2D debug information.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	/**
	 * Requests and returns the {@link TChunk} that contains the given tile coordinates. If the chunk is not
	 * loaded in the chunk cache, it will be loaded automatically.
	 * @param x the tile x coordinate.
	 * @param y the tile y coordinate.
	 * @return the chunk that contains the given tile coordinates.
	 */
	private TChunk requestChunkThatContains(int x, int y) {
		int chunkX = (x / TChunk.CHUNK_SIZE);
		int chunkY = (y / TChunk.CHUNK_SIZE);
		long hash  = (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
		if(!loadedChunks.containsKey(hash)) {
			final TChunk newChunk = new TChunk(this, chunkX, chunkY);
			loadedChunks.put(hash, newChunk);
		}
		return loadedChunks.get(hash);
	}
	
	/**
	 * Updates dynamic lighting to represent the world time. Cycles through day, dusk, night, and dawn.
	 */
	private void updateDayNightCycle(float dt) {
	    worldTime += dt;
	    if (worldTime > DAY_NIGHT_CYCLE_PERIOD) 
	    	worldTime = 0.0f;
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
		 for(final TObject obj : objects)
			 world.destroyBody(obj.getPhysicalBody());
		 deathrow.clear();
		 objects.clear();
		 Array<Body> others = new Array<>();
		 world.getBodies(others);
		 for(Body b : others)
			 world.destroyBody(b);
	}
	
	@Override
	public void dispose() {
		loadedChunks.clear();
		lightRenderer.removeAll();
		lightRenderer.dispose();
		clearAllActiveObjectsAndBodies();
		world.dispose();
		debugRenderer.dispose();
		TTerrainRenderer.gc();
	}

}
