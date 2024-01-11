package dev.iwilkey.terrafort.world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.mob.TPlayer;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.persistent.TPersistent;
import dev.iwilkey.terrafort.persistent.TSerializable;

/**
 * An abstract definition of the current state of an end-user's Terrafort game world. Serializable.
 * @author Ian Wilkey (iwilkey)
 */
public final class TWorld implements TSerializable, Disposable {
	
	/**
	 * Unique serialization ID.
	 */
	private static final long serialVersionUID = 2436423371266909255L;
	
	////////////////////////////////
	// Non-serializable attributes.
	////////////////////////////////
	
	/**
	 * The size of one tile in a Terrafort world. (TILE_SIZE x TILE_SIZE).
	 */
	public transient static final int TILE_SIZE      = 24;
	public transient static final int HALF_TILE_SIZE = TILE_SIZE / 2;
	
	/**
	 * The amount of of tiles to render in all directions, centered where the player is.
	 */
	public transient static final int RENDER_DISTANCE  = 23;
	
	/**
	 * How many tiles should we try to render outside of the camera's viewport?
	 */
	public transient static final int TILE_VIEWPORT_CULL_PADDING = 4;
	
	/**
	 * Physics engine space. JNI binding. Written in C.
	 */
	private transient World jniSpace = null;
	
	////////////////////////////////
	// Serializable attributes.
	////////////////////////////////
	
	private final String                uniqueWorldName;
	private final long                  seed;
	private final HashMap<Long, TChunk> chunkData;
	
	/**
	 * Creates a new world with given name and seed.
	 */
	public TWorld(String uniqueWorldName, long seed) {
		this.uniqueWorldName = uniqueWorldName;
		this.seed            = seed;
		chunkData            = new HashMap<>();
		initializePhysics();
		// this is a new world, so we need to add a player!
		addObject(new TPlayer());
	}

	@Override
	public void loadFromPersistent() {
		System.out.println("[TerrafortPersistent] Loading world state of " + uniqueWorldName);
		// recreate transients...
		initializePhysics();
		// recreate physical chunks...
		for(final TChunk c : chunkData.values())
			c.loadFromPersistent();
	}
	
	/**
	 * Initializes the physics engine, sets up collision manifold.
	 */
	private void initializePhysics() {
		jniSpace = new World(new Vector2(0, 0), false);
		// collision manifold...
		jniSpace.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				final Body bodyA = contact.getFixtureA().getBody();
				final Body bodyB = contact.getFixtureB().getBody();
				if(bodyA.getFixtureList().get(0).isSensor() && bodyB.getFixtureList().get(0).isSensor())
					return;
				final TObject objA = ((TObjectRuntime)(bodyA.getUserData())).getAbstract();
				final TObject objB = ((TObjectRuntime)(bodyB.getUserData())).getAbstract();
				objA.onPhysicalConvergence(objB);
				objB.onPhysicalConvergence(objA);
			}
			@Override
			public void endContact(Contact contact) {
				final Body bodyA = contact.getFixtureA().getBody();
				final Body bodyB = contact.getFixtureB().getBody();
				if(bodyA.getFixtureList().get(0).isSensor() && bodyB.getFixtureList().get(0).isSensor())
					return;
				final TObject objA = ((TObjectRuntime)(bodyA.getUserData())).getAbstract();
				final TObject objB = ((TObjectRuntime)(bodyB.getUserData())).getAbstract();
				objA.onPhysicalDivergence(objB);
				objB.onPhysicalDivergence(objA);
			}
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
		});
		// TGraphics.setDebug(jniSpace);
	}
	
	/**
	 * Get the world's unique name.
	 */
	public String getUniqueWorldName() {
		return uniqueWorldName;
	}
	
	/**
	 * Get the world's seed.
	 */
	public long getWorldSeed() {
		return seed;
	}
	
	/**
	 * Get the non-serializable physical world.
	 */
	public World getPhysicalWorld() {
		return jniSpace;
	}
	
	/**
	 * Get the worlds defined chunk data.
	 */
	public HashMap<Long, TChunk> getChunkData() {
		return chunkData;
	}
	
	/**
	 * Get or generate a {@link TChunk} at given tile coordinates.
	 */
	public TChunk getOrGenerateChunkThatContains(long tileX, long tileY) {
		final int chunkX = (int)(tileX / TChunk.CHUNK_SIZE);
    	final int chunkY = (int)(tileY / TChunk.CHUNK_SIZE);
    	final long chunkPositionHash = (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
    	if(chunkData.containsKey(chunkPositionHash)) {
    		return chunkData.get(chunkPositionHash);
    	} else {
    		final TChunk chunk = new TChunk(this, chunkX, chunkY);
    		chunkData.put(chunkPositionHash, chunk);
    		return chunk;
    	}
	}
	
	/**
	 * Adds an object to the world. Uses the objects world position to dictate the chunk it is added to.
	 */
	public void addObject(TObject object) {
		getOrGenerateChunkThatContains((long)(object.worldX / TILE_SIZE), (long)(object.worldY / TILE_SIZE)).addObject(object);
	}
	
	/**
	 * Removes an object from the world. Uses the objects world position to dictate the chunk it is currently in.
	 */
	public void removeObject(TObject object) {
		getOrGenerateChunkThatContains((long)(object.worldX / TILE_SIZE), (long)(object.worldY / TILE_SIZE)).removeObject(object);
	}

	/**
	 * Steps the physics engine fowards and instructs chunks to manage themselves.
	 */
	public void updatePhysics(float dt) {
		jniSpace.step(1 / 60f, 4, 4);	
	}
	
	/**
	 * Render the world to the screen from the camera's perspective.
	 */
	public void render(float dt) {
		// update state of world...
		updatePhysics(dt);
		// render and tick chunks in the most optimized way possible...
		final float  centerX            = TGraphics.WORLD_PROJ_MAT.position.x;
		final float  centerY            = TGraphics.WORLD_PROJ_MAT.position.y;
		final float camWidthWorldUnits  = TGraphics.WORLD_PROJ_MAT.viewportWidth * TGraphics.WORLD_PROJ_MAT.zoom;
	    final float camHeightWorldUnits = TGraphics.WORLD_PROJ_MAT.viewportHeight * TGraphics.WORLD_PROJ_MAT.zoom;
	    final int   tilesInViewWidth    = (int)(Math.round(camWidthWorldUnits / TILE_SIZE) / 2f);
	    final int   tilesInViewHeight   = (int)(Math.round(camHeightWorldUnits / TILE_SIZE) / 2f);
	    final int   cxTileSpace         = (int)Math.round(centerX / TILE_SIZE);
	    final int   cyTileSpace         = (int)Math.round(centerY / TILE_SIZE);
	    final int   xTileStart          = cxTileSpace - (tilesInViewWidth + TILE_VIEWPORT_CULL_PADDING);
	    final int   xTileEnd            = cxTileSpace + (tilesInViewWidth + TILE_VIEWPORT_CULL_PADDING);
	    final int   yTileStart          = cyTileSpace - (tilesInViewHeight + TILE_VIEWPORT_CULL_PADDING);
	    final int   yTileEnd            = cyTileSpace + (tilesInViewHeight + TILE_VIEWPORT_CULL_PADDING);
	    Set<TChunk> ticked              = new HashSet<>();
	    // loop through in-view tiles and instruct their chunk to tick and render them...
	    for(int i = xTileStart; i <= xTileEnd; i++) {
	        for(int j = yTileStart; j <= yTileEnd; j++) {
	        	int x2 = (i - cxTileSpace) * (i - cxTileSpace);
	        	int y2 = (j - cyTileSpace) * (j - cyTileSpace);
	        	if((x2 + y2) <= (RENDER_DISTANCE * RENDER_DISTANCE)) {
	        		final TChunk c = getOrGenerateChunkThatContains(i, j);
	        		if(!ticked.contains(c)) {
	        			c.update(dt);
	        			ticked.add(c);
	        		}
	        		c.render(dt, i, j);
	        	}
	        }
	    }
	}

	@Override
	public void dispose() {
		TPersistent.save(this, "world/" + uniqueWorldName + ".dat");
		for(final TChunk chunk : getChunkData().values())
			chunk.dispose();
		jniSpace.dispose();
	}

}
