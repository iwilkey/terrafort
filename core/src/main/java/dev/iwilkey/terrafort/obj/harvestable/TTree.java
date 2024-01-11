package dev.iwilkey.terrafort.obj.harvestable;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.THarvestable;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A beautiful tree.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTree extends THarvestable {
	
	public transient static final int   MAX_HEALTH        = 0xf;
	public transient static final float HEIGHT            = TWorld.TILE_SIZE * 4;
	public transient static final float WIDTH             = TWorld.TILE_SIZE * 4;
	public transient static final float COLLIDER_WIDTH    = TWorld.HALF_TILE_SIZE;
	public transient static final float COLLIDER_HEIGHT   = TWorld.TILE_SIZE / 3.5f;
	public transient static final float COLLIDER_OFF_Y    = TWorld.TILE_SIZE * 1.75f;
	
	private static final long serialVersionUID = -4142147005611047921L;

	/**
	 * Creates a new tree at given tile coordinate.
	 */
	public TTree(int tileX, int tileY) {
		super(tileX, tileY);
		name                         = "tree";
		spriteSheet                  = "sheets/natural.png";
		worldX                       = tileX * TWorld.TILE_SIZE;
		worldY                       = tileY * TWorld.TILE_SIZE;
		worldWidth                   = WIDTH;
		worldHeight                  = HEIGHT;
		colliderWidth                = COLLIDER_WIDTH;
		colliderHeight               = COLLIDER_HEIGHT;
		colliderOffX                 = 0;
		colliderOffY                 = COLLIDER_OFF_Y;
		rotationRadians              = 0;
		dataX                        = 14;
		dataY                        = (int)TMath.equalPick(0, 2);
		dataWidth                    = 2;
		dataHeight                   = 2;
		naturalTint                  = 0xffffffff;
		depth                        = 128;
		shouldUseAdditiveBlending    = false;
		definesOwnPhysics            = false;
		isDynamic                    = false;
		isSensor                     = false;
		maxHealthPoints              = MAX_HEALTH;
		currentHealthPoints          = MAX_HEALTH;
		debrisColor                  = 0x964B00ff;
	}
	
	@Override
	public void task(TObjectRuntime concrete, float dt) {

	}

	@Override
	public void death(final TObjectRuntime concrete) {
		super.death(concrete);
	}

	@Override
	public void onPhysicalConvergence(TObject convergingBody) {

	}

	@Override
	public void onPhysicalDivergence(TObject divergingBody) {

	}

	@Override
	public int getParticleCountAtDeath() {
		return ThreadLocalRandom.current().nextInt(64, 72);
	}

}
