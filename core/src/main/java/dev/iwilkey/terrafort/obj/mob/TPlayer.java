package dev.iwilkey.terrafort.obj.mob;

import java.text.NumberFormat;
import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TAudio;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.clk.TClock;
import dev.iwilkey.terrafort.clk.TEvent;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.gui.text.TScreenTextParticle;
import dev.iwilkey.terrafort.knowledge.TKnowledge;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.obj.type.TEntity;
import dev.iwilkey.terrafort.obj.type.THarvestable;
import dev.iwilkey.terrafort.obj.type.TMob;
import dev.iwilkey.terrafort.obj.type.TObject;
import dev.iwilkey.terrafort.state.TSinglePlayerWorld;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * An {@link TMob} that is controlled by the client.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPlayer extends TMob {
	
	/**
	 * GRAPHICAL BEHAVIORS
	 */
	
	public transient static final float ZOOM_TIME = 0.5f;
	public transient static final int   ZOOM_MIN  = -2;
	public transient static final int   ZOOM_MAX  = 1;
	
	/**
	 * MOB SPECIFIC BEHAVIORS...
	 */
	
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
			if(zoomLevel - 1 >= ZOOM_MIN) {
				TGraphics.requestDarkState(true, ZOOM_TIME);
				TClock.schedule(new TEvent() {
					@Override
					public boolean fire() {
						zoomLevel--;
						zoomLevel = (byte)TMath.clamp(zoomLevel, ZOOM_MIN, ZOOM_MAX);
						TGraphics.requestDarkState(false, ZOOM_TIME);
						return false;
					}
				}, 1, (ZOOM_TIME / 2f));
			}
			TInput.zoomIn = false;
		}
		if(TInput.zoomOut) {
			if(zoomLevel + 1 <= ZOOM_MAX) {
				TGraphics.requestDarkState(true, ZOOM_TIME);
				TClock.schedule(new TEvent() {
					@Override
					public boolean fire() {
						zoomLevel++;
						zoomLevel = (byte)TMath.clamp(zoomLevel, ZOOM_MIN, ZOOM_MAX);
						TGraphics.requestDarkState(false, ZOOM_TIME);
						return false;
					}
				}, 1, (ZOOM_TIME / 2f));
			}
			TInput.zoomOut = false;
		}
	}
	@Override
	public void task(final TObjectRuntime concrete, float dt) {
		rotationRadians = 0;
		// Camera follows the player...
		TGraphics.setCameraTargetPosition(concrete.getX(), concrete.getY());
		TGraphics.setZoomLevel(zoomLevel);
		// Call the knowledge equipped function if applicable...
		if(TSinglePlayerWorld.getKnowlegeBar().getSelected() != null)
			TSinglePlayerWorld.getKnowlegeBar().getSelected().equipped();
	}
	
	@Override
	public boolean requestAction() {
		// Simple punch request if no knowledge is equipped happens as quickly as possible...
		if(TSinglePlayerWorld.getKnowlegeBar().getSelected() == null)
			return TInput.interact;
		else return TSinglePlayerWorld.getKnowlegeBar().getSelected().requestPractice(); // Do the action request of the knowledge!
		
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
		// Punch stuff if you have no knowledge equipped...
		if(TSinglePlayerWorld.getKnowlegeBar().getSelected() == null) {
			TObject obj = sense(concrete, 16f, (float)Math.PI / 16f, 2);
			if(obj != null) {
				// punch!
				if(obj instanceof TEntity)
					((TEntity)obj).hurt(1);
				if(obj instanceof THarvestable) {
					((THarvestable)obj).shake(concrete);
					if(((THarvestable)obj).currentHealthPoints <= 0) {
						giveFunds(((THarvestable)obj).getValue());
					}
				}
			}
		} else {
			// Do the practice routine of the knowledge (if the player has the funds to do so.)
			final TKnowledge knowledge = TSinglePlayerWorld.getKnowlegeBar().getSelected();
			if(getFunds() - knowledge.getPracticeValue() >= 0)
				knowledge.practice(this, concrete.getWorld());
			else {
				// create a text particle saying that you can't practice the skill!
				TUserInterface.submitTextParticle(new TScreenTextParticle(TLocale.getLine(56), TAnchor.BOTTOM_CENTER, 0, 128, 16, 0xff0000ff));
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
			TUserInterface.submitTextParticle(new TScreenTextParticle("+".repeat(TMath.getFigures(amount)), 
					TAnchor.BOTTOM_LEFT, 
					ThreadLocalRandom.current().nextInt(38, 110), 
					48, 
					14, 
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
			TUserInterface.submitTextParticle(new TScreenTextParticle("-".repeat(TMath.getFigures(amount)), 
					TAnchor.BOTTOM_LEFT, 
					ThreadLocalRandom.current().nextInt(38, 110), 
					48, 
					14, 
					0xff0000ff));
		funds -= amount;
		return true;
	}

}
