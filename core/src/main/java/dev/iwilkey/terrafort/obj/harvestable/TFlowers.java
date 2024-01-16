package dev.iwilkey.terrafort.obj.harvestable;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.THarvestable;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.world.TWorld;

public final class TFlowers extends THarvestable {
	
	public transient static final int   MAX_HEALTH        = 0x1;
	public transient static final float HEIGHT            = TWorld.TILE_SIZE;
	public transient static final float WIDTH             = TWorld.TILE_SIZE;
	public transient static final float COLLIDER_WIDTH    = TWorld.HALF_TILE_SIZE;
	public transient static final float COLLIDER_HEIGHT   = TWorld.HALF_TILE_SIZE;
	
	private static final long serialVersionUID = -4046288203518036084L;
	
	/**
	 * Creates new flowers at given tile coordinate. 
	 */
	public TFlowers(int tileX, int tileY) {
		super(tileX, tileY);
		name                         = "flowers";
		spriteSheet                  = "sheets/natural.png";
		worldX                       = tileX * TWorld.TILE_SIZE;
		worldY                       = tileY * TWorld.TILE_SIZE;
		worldWidth                   = WIDTH;
		worldHeight                  = HEIGHT;
		colliderWidth                = COLLIDER_WIDTH;
		colliderHeight               = COLLIDER_HEIGHT;
		colliderOffX                 = 0;
		colliderOffY                 = 0;
		rotationRadians              = 0;
		dataX                        = 11;
		dataY                        = (int)TMath.equalPick(0, 1, 2, 3, 4, 5);
		dataWidth                    = 1;
		dataHeight                   = 1;
		naturalTint                  = 0xffffffff;
		depth                        = 129;
		shouldUseAdditiveBlending    = false;
		definesOwnPhysics            = false;
		isDynamic                    = false;
		isSensor                     = true;
		maxHealthPoints              = MAX_HEALTH;
		currentHealthPoints          = MAX_HEALTH;
		debrisColor                  = (Math.random() > 0.5f) ? 0xffc0cbff : 0xffff00ff;
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
		return ThreadLocalRandom.current().nextInt(2, 4);
	}

	@Override
	public int getValue() {
		return ThreadLocalRandom.current().nextInt(5, 8);
	}

	@Override
	public String getShakeSoundPath() {
		return "sound/leaves_hit.wav";
	}

}
