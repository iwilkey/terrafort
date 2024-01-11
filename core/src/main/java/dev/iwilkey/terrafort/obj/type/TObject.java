package dev.iwilkey.terrafort.obj.type;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.world.TChunk;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A distict and purposeful thing that exists in the game world. This class serves as an abstract representation of the object's state, rather
 * than it's physical presence.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TObject implements Serializable {
	
	/**
	 * The current chunk jurisdiction the object exists in. Managed internally; doesn't need to be serialized.
	 */
	protected transient long currentChunkJurisdiction;
	
	/**
	 * The current terrain level the mob is on. Managed internally; doesn't need to be serialized.
	 */
	protected transient int currentTerrainLevel;
	
	/**
	 * Current x position in tile-space. Managed internally; doesn't need to be serialized.
	 */
	public transient int currentTileX;
	
	/**
	 * Current y position in tile-space. Managed internally; doesn't need to be serialized.
	 */
	public transient int currentTileY;
	
	/**
	 * The unique serialization ID.
	 */
	private static final long serialVersionUID = -1704547445757768378L;
	
	/**
	 * What is this object called?
	 */
	public String name;
	
	/**
	 * What {@link TSpriteSheet} path should this object reference during render time? Must be registered with the {@link TGraphics} module!
	 */
	public String spriteSheet;
	
	/**
	 * Where is this object's x position in world-space?
	 */
	public float worldX;
	
	/**
	 * Where is this object's y position in world-space?
	 */
	public float worldY;

	/**
	 * Where, local to the object, should its collider's x position be?
	 */
	public float colliderOffX;
	
	/**
	 * Where, local to the object, should its collider's y position be?
	 */
	public float colliderOffY;
	
	/**
	 * How wide is the object's collider?
	 */
	public float worldWidth;
	
	/**
	 * How tall is the object's collider?
	 */
	public float worldHeight;
	
	/**
	 * How wide is the object's collider?
	 */
	public float colliderWidth;
	
	/**
	 * How tall is the object's collider?
	 */
	public float colliderHeight;
	
	/**
	 * What is the rotation of the object, in radians.
	 */
	public float rotationRadians;
	
	/**
	 * How dense/massive is this object? How should it behave in the physics engine?
	 */
	public float mass;
	
	/**
	 * What is the object's x cell on specified sprite data sheet?
	 */
	public int dataX;
	
	/**
	 * What is the object's y cell on specified sprite data sheet?
	 */
	public int dataY;
	
	/**
	 * What is the object's cell width on specified sprite data sheet?
	 */
	public int dataWidth;
	
	/**
	 * What is the object's cell height on specified sprite data sheet?
	 */
	public int dataHeight;
	
	/**
	 * What color should the object be tinted with during rendering? RGBA hex format.
	 */
	public int tint;
	
	/**
	 * What render layer should the object be rendered on? 0 is highest priority.
	 */
	public int depth;
	
	/**
	 * Should the object use additive blending when rendered?
	 */
	public boolean shouldUseAdditiveBlending;
	
	/**
	 * Does the object define its own physics, or does it follow the physics engine's simulation of it?
	 */
	public boolean definesOwnPhysics;
	
	/**
	 * Should the object be treated like a dynamic or kinematic body in the physics engine?
	 */
	public boolean isDynamic;
	
	/**
	 * Should the object react to external forces, or simply just listen for collisions?
	 */
	public boolean isSensor;
	
	/**
	 * Called every frame it is active in the game world.
	 */
	public void tick(final TObjectRuntime concrete, float dt) {
		handleTransform(concrete);
	}
	
	/**
	 * Handles the absolute transform of the object.
	 */
	private void handleTransform(final TObjectRuntime concrete) {
		if(!definesOwnPhysics) {
			// Abstract follows simulated physical implementation.
			worldX          = concrete.getPhysical().getPosition().x;
			worldY          = concrete.getPhysical().getPosition().y;
			refuseGraphicalArtifacts();
			rotationRadians = (float)concrete.getPhysical().getAngle();
		} else {
			refuseGraphicalArtifacts();
			// Forces the physical representation of the object to follow this abstract one.
			concrete.getPhysical().setTransform(new Vector2(worldX, worldY), rotationRadians);
		}
		calculateTileLocation(concrete);
		calculateChunkJurisdiction(concrete);
		if(concrete.getWorld().getChunkData().containsKey(currentChunkJurisdiction))
			currentTerrainLevel = concrete.getWorld().getChunkData().get(currentChunkJurisdiction).getOrGenerateTile(currentTileX, currentTileY);
	}
	
	/**
	 * Callback from physics engine indicating that another object is just began contacting this object in space.
	 */
	public abstract void onPhysicalConvergence(TObject convergingBody);
	
	/**
	 * Callback from physics engine indicating that another object is no longer contacting this object in space.
	 */
	public abstract void onPhysicalDivergence(TObject divergingBody);
	
	/**
	 * Return the non-serializable concrete version of this object such that it can exist in the
	 * actual game world.
	 */
	public final TObjectRuntime create(TWorld world) {
		return new TObjectRuntime(world, this);
	}
	
	/**
	 * Returns the chunk that the object should currently belong to based on the (worldX, worldY) attributes of a {@link TObject}.
	 */
	public final long getChunkJurisdiction() {
		return currentChunkJurisdiction;
	}
	
	/**
	 * Calculates where the object currently is in tile-space.
	 */
	private void calculateTileLocation(final TObjectRuntime concrete) {
		currentTileX = Math.round(worldX / TWorld.TILE_SIZE);
		currentTileY = Math.round(worldY / TWorld.TILE_SIZE);
	}
	
	/**
	 * Returns the position hash of the chunk that should have jurisdiction of the object.
	 */
	private void calculateChunkJurisdiction(final TObjectRuntime concrete) {
		final int chunkX         = currentTileX / TChunk.CHUNK_SIZE;
		final int chunkY         = currentTileY / TChunk.CHUNK_SIZE;
		currentChunkJurisdiction = (((long)chunkX) << 32) | (chunkY & 0xffffffffL);
	}
	
	/**
	 * Algorithm to snap the true position of the object to the camera such that graphical artifacts are
	 * avoided.
	 */
	private void refuseGraphicalArtifacts() {
		float z                            = TGraphics.CAMERA_ZOOM.getTarget();
	    float effectivePixelsPerUnit       = 1f / z;
	    float screenWidthInWorldUnits      = Gdx.graphics.getWidth() / effectivePixelsPerUnit;
	    float screenHeightInWorldUnits     = Gdx.graphics.getHeight() / effectivePixelsPerUnit;
	    float halfScreenWidthInWorldUnits  = screenWidthInWorldUnits / 2f;
	    float halfScreenHeightInWorldUnits = screenHeightInWorldUnits / 2f;
	    float x                            = worldX;
	    float y                            = worldY;
	    float rx                           = TMath.roundTo(x + halfScreenWidthInWorldUnits, effectivePixelsPerUnit) - halfScreenWidthInWorldUnits;
	    float ry                           = TMath.roundTo(y + halfScreenHeightInWorldUnits, effectivePixelsPerUnit) - halfScreenHeightInWorldUnits;
	    worldX                             = rx;
	    worldY                             = ry;
	}
	
}
