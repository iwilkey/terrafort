package dev.iwilkey.terrafort.obj.particulate;

import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;

/**
 * A physical representation of a {@link TItem}. Can be collected by {@link TPlayer}s.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemDrop extends TParticulate {
	
	public static final float PICKUP_TIME_BEST_CASE  = 60.0f * 3; // 3 minutes!
	public static final float PICKUP_TIME_WORST_CASE = 50.0f * 3;
	public static final int   WORLD_SIZE             = (int)(TTerrainRenderer.TERRAIN_TILE_WIDTH / 1.5f);
	
	private final TItem item;
	private final float bulgeTimeMultipler;

	private       float dimTimer = 0.0f;

	/**
	 * Spawns a {@link TItemDrop} at specified location with random impulse direction.
	 */
	public TItemDrop(TSinglePlayerWorld world, float x, float y, TItem item) {
		super(world, x, y, WORLD_SIZE, WORLD_SIZE, TMath.nextFloat(PICKUP_TIME_WORST_CASE, PICKUP_TIME_BEST_CASE));
		this.item          = item;
		bulgeTimeMultipler = TMath.nextFloat(1.5f, 3.0f);
		init();
		randomImpulseForce(0, 1024.0f);
		randomLinearAndAngularDamping(32f, 33f);
	}
	
	/**
	 * Spawns a {@link TItemDrop} at specified location with specific impulse direction. Likely used for throwing.
	 */
	public TItemDrop(TSinglePlayerWorld world, float x, float y, float throwDirX, float throwDirY, TItem item) {
		super(world, x, y, WORLD_SIZE, WORLD_SIZE, TMath.nextFloat(PICKUP_TIME_WORST_CASE, PICKUP_TIME_BEST_CASE));
		this.item          = item;
		bulgeTimeMultipler = TMath.nextFloat(1.5f, 3.0f);
		init();
		randomDirectedImpulseForce(64, 1024, new Vector2(throwDirX, throwDirY), 30);
		randomLinearAndAngularDamping(10f, 11f);
	}
	
	private void init() {
		this.dataOffsetX               = item.getIcon().getDataOffsetX();
		this.dataOffsetY               = item.getIcon().getDataOffsetY();
		this.dataSelectionSquareWidth  = item.getIcon().getDataSelectionWidth();
		this.dataSelectionSquareHeight = item.getIcon().getDataSelectionHeight();
		shouldFade                     = false;
	}

	/**
	 * Returns the item that this {@link TItemDrop} represents; ends the {@link TParticulates} life such that it can only be collected once.
	 * Nothing happens if the {@link TPlayer}s inventory is full. Called internally by the {@link TCollisionManifold}.
	 */
	public void transferTo(TPlayer player) {
		// an item cannot collected if it is dead.
		if(isDone())
			return;
		if(player.giveItem(item))
			// only kill it if it has been successfully been picked up.
			setDone();
	}

	@Override
	public void behavior(float dt) {
		// "bulge" the item periodically to catch attention from player
		dimTimer += dt * bulgeTimeMultipler;
		width    = WORLD_SIZE + ((1.5f * (float)Math.sin(dimTimer) + 1f));
		height   = WORLD_SIZE + ((1.5f * (float)Math.sin(dimTimer) + 1f));
		if(dimTimer >= 2 * Math.PI)
			dimTimer = 0.0f;
	}
	
}
