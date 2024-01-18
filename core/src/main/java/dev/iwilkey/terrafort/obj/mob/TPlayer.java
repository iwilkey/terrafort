package dev.iwilkey.terrafort.obj.mob;

import java.text.NumberFormat;
import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TAudio;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.text.TScreenTextParticle;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.TEntity;
import dev.iwilkey.terrafort.obj.type.THarvestable;
import dev.iwilkey.terrafort.obj.type.TMob;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * An {@link TMob} that is controlled by the client.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPlayer extends TMob {
	
	public transient static final int   MAX_HEALTH        = 0xff;
	public transient static final float ACTION_COOLDOWN   = 0.25f;
	public transient static final float WATER_MOVE_MULT   = 0.25f;
	public transient static final float POT_MOVE_SPEED    = 64.0f;
	public transient static final float HEIGHT            = 64;
	public transient static final float WIDTH             = 32;
	public transient static final float COLLIDER_WIDTH    = TWorld.HALF_TILE_SIZE;
	public transient static final float COLLIDER_HEIGHT   = 4;
	public transient static final float COLLIDER_OFFSET_Y = 12;
	
	private static final long serialVersionUID = -5927861991543806097L;
	
	/**
	 * The current zoom of the game camera that follows this mob.
	 */
	private byte zoomLevel;
	
	public long funds;
	
	/**
	 * Creates a new player at (0, 0).
	 */
	public TPlayer() {
		name                         = "player";
		// players shall always spawn 0, 0 in a new world.
		worldX                       = 0;
		worldY                       = 0;
		worldWidth                   = WIDTH;
		worldHeight                  = HEIGHT;
		regularHeight                = HEIGHT;
		colliderWidth                = COLLIDER_WIDTH;
		colliderHeight               = COLLIDER_HEIGHT;
		colliderOffX                 = 0;
		colliderOffY                 = COLLIDER_OFFSET_Y;
		rotationRadians              = 0;
		mass                         = 128.0f;
		dataX                        = 0;
		dataY                        = 0;
		dataWidth                    = 1;
		dataHeight                   = 2;
		naturalTint                  = (Math.random() > 0.5f) ? 0xffffffff : 0x6d3800ff;
		depth                        = 128;
		shouldUseAdditiveBlending    = false;
		definesOwnPhysics            = false;
		isDynamic                    = true;
		isSensor                     = false;
		maxHealthPoints              = MAX_HEALTH;
		currentHealthPoints          = MAX_HEALTH;
		currentState                 = TState.IDLE;
		facingDirection              = TMath.NORTH;
		potentialMoveSpeed           = POT_MOVE_SPEED;
		waterMovementMultiplier      = WATER_MOVE_MULT;
		potentialMoveSpeedMultiplier = 1.0f;
		actionCooldown               = ACTION_COOLDOWN;
		clothingColor                = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE - 1);
		// players, in a new game, start with 0 funds.
		funds                        = 0L;
	}
	
	@Override
	public void movementProcedure(final TObjectRuntime concrete, float dt) {
		// follow user's instructions for movement...
		if(TInput.moveLeft)  moveLeft();
		if(TInput.moveRight) moveRight();
		if(TInput.moveUp)    moveUp();
		if(TInput.moveDown)  moveDown();
		potentialMoveSpeedMultiplier = (TInput.run) ? 2.0f : 1.0f;
		if(TInput.zoomIn) {
			zoomLevel--;
			zoomLevel     = (byte)TMath.clamp(zoomLevel, -2, 0);
			TInput.zoomIn = false;
		}
		if(TInput.zoomOut) {
			zoomLevel++;
			zoomLevel      = (byte)TMath.clamp(zoomLevel, -2, 0);
			TInput.zoomOut = false;
		}
	}
	
	float t = 0.0f;
	
	@Override
	public void task(final TObjectRuntime concrete, float dt) {
		rotationRadians = 0;
		
		t += dt;
		if(t > Math.random()) {
			if(Math.random() > 0.5f)
				giveFunds(ThreadLocalRandom.current().nextInt(32, 2024));
			else takeFunds(ThreadLocalRandom.current().nextInt(32, 2024));
			t = 0.0f;
		}
		
		// Camera follows the player...
		TGraphics.setCameraTargetPosition(concrete.getX(), concrete.getY());
		TGraphics.setZoomLevel(zoomLevel);
	}
	
	@Override
	public boolean requestAction() {
		return TInput.interact;
	}
	
	@Override
	public int actionDirection() {
		// if you are standing still, you will attack in the direction of the cursor...
		if(!isMovingPhysically()) {
			float ang = TMath.radialAngleOfCursorDeg();
			ang += 110f; // domain change to zero-point of partition to desired orientation.
			ang %= 360f;
			return TMath.partition(ang / 360f, 8);
		}
		// otherwise, you attack in the direction you're moving towards...
		return facingDirection;
	}
	
	@Override
	public void actionProcedure(final TObjectRuntime concrete, int direction) {
		TObject obj = sense(concrete, 16f, (float)Math.PI / 16f, 2);
		if(obj != null) {
			// interact.
			if(obj instanceof TEntity)
				((TEntity)obj).hurt(1);
			if(obj instanceof THarvestable) {
				((THarvestable)obj).shake(concrete);
				if(((THarvestable)obj).currentHealthPoints <= 0) {
					giveFunds(((THarvestable)obj).getValue());
				}
			}
		}
	}

	@Override
	public void death(final TObjectRuntime concrete) {

	}

	@Override
	public void onPhysicalConvergence(TObject convergingBody) {

	}

	@Override
	public void onPhysicalDivergence(TObject divergingBody) {

	}
	
	/**
	 * Formats given a funds amount to a renderable string.
	 */
	public String fundsToString(long amount) {
		return NumberFormat.getNumberInstance().format(amount);
	}
	
	/**
	 * Get the amount of funds that belong to this player.
	 */
	public long getFunds() {
		return funds;
	}
	
	/**
	 * Give this player a certain amount of funds.
	 */
	public void giveFunds(long amount) {
		funds += amount;
		if(TInput.focused)
			TUserInterface.submitTextParticle(new TScreenTextParticle("+ " + fundsToString(amount) + " F", 
					TAnchor.BOTTOM_LEFT, 
					ThreadLocalRandom.current().nextInt(32, 128), 
					48, 
					16, 
					0x3eff35ff));
		TAudio.playFx("sound/give_funds.wav", true);
	}
	
	/**
	 * Tries to take a specified amount of funds from the player. Returns false if insufficient funds.
	 */
	public boolean takeFunds(long amount) {
		if(funds - amount < 0L)
			return false;
		if(TInput.focused)
			TUserInterface.submitTextParticle(new TScreenTextParticle("- " + fundsToString(amount) + " F", 
					TAnchor.BOTTOM_LEFT, 
					ThreadLocalRandom.current().nextInt(32, 128), 
					48, 
					16, 
					0xff0000ff));
		funds -= amount;
		return true;
	}

}
