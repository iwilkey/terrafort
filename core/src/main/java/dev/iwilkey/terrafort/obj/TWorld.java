package dev.iwilkey.terrafort.obj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import box2dLight.PointLight;
import box2dLight.RayHandler;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.math.TNoise;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.tile.TTile;

/**
 * A physical space that manages {@link TObjects}, global and local forces, and dynamic lighting.
 * @author Ian Wilkey (iwilkey)
 */
public final class TWorld implements Disposable {
	
	public static final int          TERRAIN_TILE_WIDTH = 8;
	public static final int          TERRAIN_TILE_HEIGHT = 8;
	public static final short        IGNORE_LIGHTING = 0x0001;
	public static final float        DAY_DURATION = 30000.0f;

	private final World              world;
	private final RayHandler         lighting;
	private final Filter             lightingCollisionMask;
	private final Array<TObject>     objects;
	private final Array<TEntity>     deathrow;
	private final Box2DDebugRenderer debugRenderer;
	
	private TPlayer                  player;
	private boolean                  debug;
	private float                    worldTime;
	private boolean                  day;
	private boolean                  dusk;
	private boolean                  night;
	private boolean                  dawn;

	public TWorld() {
		world                           = new World(new Vector2(0, 0), false);
		lighting                        = new RayHandler(world);
		lightingCollisionMask           = new Filter();
		objects                         = new Array<>();
		deathrow                        = new Array<>();
		debugRenderer                   = new Box2DDebugRenderer();
		debug                           = false;
		player                          = null;
		worldTime                       = DAY_DURATION;
		day                             = true;
		dusk                            = false;
		night                           = false;
		dawn                            = false;
		lightingCollisionMask.maskBits  = (short)(~IGNORE_LIGHTING);
		lighting.setAmbientLight(0.1f, 0.1f, 0.1f, 0.5f);
	}
	
	/**
	 * Returns the world location currently selected by the cursors position on the screen.
	 */
	public Vector2 getMousePositionInWorld() {
	    final Vector3 screenCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
	    final Vector3 worldCoords  = TGraphics.CAMERA.unproject(screenCoords);
	    return new Vector2(worldCoords.x, worldCoords.y);
	}
	
	/**
	 * Returns the cursors location in world space rounded to the tile grid. Useful in
	 * selecting objects that snap to the world tile grid.
	 */
	public Vector2 roundMousePositionToWorldTileGrid() {
		Vector2 ret = new Vector2().set(getMousePositionInWorld());
		ret.x = Math.round(ret.x / TERRAIN_TILE_WIDTH) * TERRAIN_TILE_WIDTH;
		ret.y = Math.round(ret.y / TERRAIN_TILE_WIDTH) * TERRAIN_TILE_WIDTH;
		return ret;
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
		PointLight ret = new PointLight(lighting, 256, color, r, x, y);
		ret.setContactFilter(lightingCollisionMask);
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
		if(obj instanceof TAnimal) 
			obj.getFixture().getFilterData().categoryBits = IGNORE_LIGHTING;
		else obj.getFixture().getFilterData().categoryBits = ~IGNORE_LIGHTING;
		objects.add(obj);
		return obj;
	}
	
	/**
	 * Steps the world simulation forward one tick.
	 * @param dt the change in time since the last tick.
	 */
	public void update(float dt) {
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
            }
        }
        for(TEntity e : deathrow)
        	e.die();
        objects.removeAll(deathrow, false);
	}
	
	TFrame level[][] = { 
			TTile.GRASS,
			TTile.SAND,
			TTile.WATER
	};
	
	/**
	 * Renders the world's objects, Box2D debug information, and dynamic lighting.
	 */
	public void render() {
		
		if(player != null) {
			
			int padding               = 128;
		    float camWidthWorldUnits  = TGraphics.CAMERA.viewportWidth * TGraphics.CAMERA.zoom;
		    float camHeightWorldUnits = TGraphics.CAMERA.viewportHeight * TGraphics.CAMERA.zoom;
		    int tilesInViewWidth      = Math.round(camWidthWorldUnits / TERRAIN_TILE_WIDTH) / 2;
		    int tilesInViewHeight     = Math.round(camHeightWorldUnits / TERRAIN_TILE_HEIGHT) / 2;
		    int playerTileX           = Math.round(player.getRenderX() / TERRAIN_TILE_WIDTH);
		    int playerTileY           = Math.round(player.getRenderY() / TERRAIN_TILE_HEIGHT);
		    int xs                    = playerTileX - (tilesInViewWidth + padding);
		    int xe                    = playerTileX + (tilesInViewWidth + padding);
		    int ys                    = playerTileY - (tilesInViewHeight + padding);
		    int ye                    = playerTileY + (tilesInViewHeight + padding);
		   
		    for (int i = xs; i <= xe; i++) {
		        for (int j = ys; j <= ye; j++) {
		        	int[] dat = TTile.tileLocationData(512, i, j, 0.01f, 0.01f);
		        	TGraphics.draw(level[dat[0]][0],
		                           i * TERRAIN_TILE_WIDTH, 
		                           j * TERRAIN_TILE_HEIGHT,
		                           dat[0],
		                           TERRAIN_TILE_WIDTH, 
		                           TERRAIN_TILE_HEIGHT);
		        	// Check and draw appropriate transitions...
		        	for(int d = 1; d < 9; d++) {
		        		int xx = i + TTile.DX[d];
		        		int yy = j - TTile.DY[d];
		        		if(dat[d] == 1 && dat[0] == 0) {
		        			// Transition from grass to sand...
		        			TGraphics.draw(level[0][d],
			                           xx * TERRAIN_TILE_WIDTH, 
			                           yy * TERRAIN_TILE_HEIGHT, 
			                           dat[0],
			                           TERRAIN_TILE_WIDTH, 
			                           TERRAIN_TILE_HEIGHT);
		        		} else if(dat[d] == 2 && dat[0] == 1) {
		        			// transitions from sand to water...
		        			TGraphics.draw(level[1][d],
			                           xx * TERRAIN_TILE_WIDTH, 
			                           yy * TERRAIN_TILE_HEIGHT, 
			                           dat[0],
			                           TERRAIN_TILE_WIDTH, 
			                           TERRAIN_TILE_HEIGHT);
		        		}
		        	}
		        }
		    }
		}
		
		for(final TObject obj : objects)
			TGraphics.draw(obj);
		
		if(debug) debugRenderer.render(world, TGraphics.CAMERA.combined);
		lighting.setCombinedMatrix(TGraphics.CAMERA);
		lighting.updateAndRender();
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
	 * Updates dynamic lighting to represent the world time. Cycles through day, dusk, night, and dawn.
	 */
	private void updateDayNightCycle(float dt) {
	    worldTime += dt;
	    if (worldTime > DAY_DURATION) 
	    	worldTime = 0.0f;
	    final float progress    = worldTime / DAY_DURATION;
	    float sunlightIntensity = 0.5f * ((float)Math.cos(2 * Math.PI * progress) + 1.0f);
	    sunlightIntensity       = TMath.clamp(sunlightIntensity, 0.1f, 0.9f);
	    day                     = (progress >= 0.0f && progress < 0.25f) || (progress >= 0.75f && progress <= 1.0f);
	    dusk                    = (progress >= 0.25f && progress < 0.375f);
	    night                   = (progress >= 0.375f && progress < 0.625f);
	    dawn                    = (progress >= 0.625f && progress < 0.75f);
	    lighting.setAmbientLight(0.1f, 0.1f, 0.1f, sunlightIntensity);
	}

	@Override
	public void dispose() {
		lighting.dispose();
		world.dispose();
		debugRenderer.dispose();
	}

}
