package dev.iwilkey.terrafort.obj.harvestable;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.THarvestable;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A bush.
 * @author Ian Wilkey (iwilkey)
 */
public final class TBush extends THarvestable {

	public transient static final int   MAX_HEALTH        = 0x2;
	public transient static final float HEIGHT            = TWorld.TILE_SIZE * 3;
	public transient static final float WIDTH             = TWorld.TILE_SIZE * 3;
	public transient static final float COLLIDER_WIDTH    = TWorld.TILE_SIZE / 1.5f;
	public transient static final float COLLIDER_HEIGHT   = TWorld.TILE_SIZE / 5f;
	public transient static final float COLLIDER_OFF_Y    = TWorld.HALF_TILE_SIZE;
	
	private static final long serialVersionUID = -4142147005611047921L;
	
	/**
	 * Creates a new bush at given tile coordinate.
	 */
	public TBush(int tileX, int tileY) {
		super(tileX, tileY);
		name                         = "bush";
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
		dataX                        = 12;
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
		debrisColor                  = 0x964b00ff;
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
		return ThreadLocalRandom.current().nextInt(16, 32);
	}

	@Override
	public int getValue() {
		return ThreadLocalRandom.current().nextInt(10, 25);
	}

	@Override
	public String getShakeSoundPath() {
		return "sound/leaves_hit.wav";
	}

}
