package dev.iwilkey.terrafort.obj.entity.lifeform;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A more specific, yet still abstract {@link TEntity}. Has special properties and functions that all Terrafort 
 * intelligent beings share. Ignores lighting.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TLifeform extends TEntity {
	
	private TLifeformAnimationArray         movementAnimationArray;
	private Vector2 		                movementVector;
	private String 		                    lastNonZeroDirection;
	private boolean 		                isMoving;
	private boolean                         isInWater;
	private float                           regHeight;
	private float   		                requestedMoveSpeed;
	private float                           actualMoveSpeed;
	private float                           attackCooldownAmt;
	private float                           attackTimer;
	protected int                           directionFace;
	
	public TLifeform(TWorld  world, 
			       boolean                         isDynamic, 
			       float                           x, 
			       float                           y, 
			       int                             z, 
			       float                           width, 
			       float                           height,
			       float                           colliderWidth, 
			       float                           colliderHeight, 
			       int                             dataOffsetX, 
			       int                             dataOffsetY, 
			       int                             dataSelectionSquareWidth,
			       int                             dataSelectionSquareHeight, 
			       Color                           renderTint, 
			       int                             maxHP,
			       TLifeformAnimationArray array) {
		super(world, 
			  isDynamic, 
			  x, 
			  y, 
			  z, 
			  width, 
			  height,
			  colliderWidth, 
			  colliderHeight, 
			  dataOffsetX, 
			  dataOffsetY,
			  dataSelectionSquareWidth, 
			  dataSelectionSquareHeight, 
			  renderTint, 
			  maxHP);
		movementAnimationArray = array;
		movementAnimationArray.addToAnimationController(animationController);
		movementVector         = new Vector2();
		regHeight              = height;
		requestedMoveSpeed     = 0.0f;
		actualMoveSpeed        = 0.0f;
		lastNonZeroDirection   = TLifeformAnimationArray.LABELS[ThreadLocalRandom.current().nextInt(0, 8)].replace("move_", "idle_");
		attackCooldownAmt      = 0.25f;
		attackTimer            = attackCooldownAmt;
	}

	@Override
	public void task(float dt) {
		
		// Resets the movement vector, polls for changes, and sets the appropriate velocity.
		movementVector.setZero();
		movementProcedure();
		setVelocity(movementVector.x, movementVector.y);
		
		// Updates global flags about TLifeform's state.
		isMoving  = (movementVector.x != 0 || movementVector.y != 0);
		
		// Water mechanics...
		isInWater = world.getTileHeightAt(getCurrentTileX(), getCurrentTileY()) == TTerrainRenderer.TERRAIN_LEVELS - 1;
		if(isInWater) {
			actualMoveSpeed           = requestedMoveSpeed / 3f;
			dataSelectionSquareHeight = 1;
			height                    = regHeight / 2f;
		} else {
			actualMoveSpeed           = requestedMoveSpeed;
			dataSelectionSquareHeight = 2;
			height                    = regHeight;
		}
		
		// Calculates the sprite version of the TLifeform to render based on state.
		calculateFacingDirection();
		calculateGraphics(dt);
		
	}
	
	/**
	 * Method that returns true when a {@link TLifeform} wants to attack. Might be ignored based on internal 
	 * attack timer.
	 */
	public abstract boolean requestAttack();
	
	/**
	 * Function called when a {@link TLifeform}'s attack request has been accepted and will attack this frame.
	 */
	public abstract void attackProcedure();
	
	/**
	 * Function called to calculate the movement of a {@link TLifeform}.
	 * 
	 * <p>
	 * The idea is that this function will only contain calls to the following functions based
	 * on function procedure:
	 * </p>
	 * 
	 * <p>
	 * moveLeft()<br>
	 * moveRight()<br>
	 * moveUp()<br>
	 * moveDown()<br>
	 * </p>
	 */
	public abstract void movementProcedure();
	
	public final void moveLeft() {
		movementVector.add(-actualMoveSpeed, 0);
	}
	
	public final void moveRight() {
		movementVector.add(actualMoveSpeed, 0);
	}
	
	public final void moveUp() {
		movementVector.add(0, actualMoveSpeed);
	}
	
	public final void moveDown() {
		movementVector.add(0, -actualMoveSpeed);
	}
	
	public final void setMoveSpeed(float moveSpeed) {
		this.requestedMoveSpeed = moveSpeed;
	}
	
	/**
	 * Sets the {@link TLifeform} attack cooldown time.
	 */
	public final void setAttackCooldownTime(float time) {
		attackCooldownAmt = time;
	}
	
	/**
	 * Return the object from the collision manifold that is most likely to match the {@link TLifeform}s expectations.
	 */
	public final TObject getNextCollisionFromManifold() {
		if(getCollisionManifold().size == 0)
			return this;
		// Otherwise, pick based on directional criteria.
		for(final TObject o : getCollisionManifold()) {
			final float ox = o.getRenderX();
			final float oy = o.getRenderY();
			final int dx = Math.round(ox - x) / (TTerrainRenderer.TERRAIN_TILE_WIDTH / 2);
			final int dy = Math.round(oy - y) / (TTerrainRenderer.TERRAIN_TILE_HEIGHT / 2);
			switch(directionFace) {
				case TMath.SOUTH:
					if(dx == 0 && dy == -1)
						return o;
					break;
				case TMath.SOUTH_EAST:
					if(dx == 1 && dy == -1)
						return o;
					break;
				case TMath.EAST:
					if(dx == 1 && dy == 0)
						return o;
					break;
				case TMath.NORTH_EAST:
					if(dx == 1 && dy == 1)
						return o;
					break;
				case TMath.NORTH:
					if(dx == 0 && dy == 1)
						return o;
					break;
				case TMath.NORTH_WEST:
					if(dx == -1 && dy == 1)
						return o;
					break;
				case TMath.WEST:
					if(dx == -1 && dy == 0)
						return o;
					break;
				case TMath.SOUTH_WEST:
					if(dx == -1 && dy == -1)
						return o;
					break;
			}
		}
		return getCollisionManifold().get(0);
	}
	
	/**
	 * Calculates what animation should play based on {@link TLifeform} state.
	 */
	private final void calculateGraphics(float dt) {
		
		// Animates a {@link TLifeform} moving in any of the 8 possible directions based on the animations provided by the
		// {@link TLifeformAnimationArray}...
		getAnimationController().setTargetFrameRate(requestedMoveSpeed / 6);
		String animationLabel = TLifeformAnimationArray.LABELS[directionFace];
        lastNonZeroDirection = animationLabel.replace("move_", "idle_");
        if(!shouldDraw) 
        	shouldDraw = true;
        
        // Handles the attack clock and animations...
        if(attackTimer < attackCooldownAmt) {
        	attackTimer += dt;
        	shouldDraw = false;
        	if(attackTimer <= (attackCooldownAmt / 2f))
        		TGraphics.draw(movementAnimationArray.getAttackFrame(directionFace, 0), 
        				(int)getRenderX(), 
        				(int)getRenderY(), 
        				0, 
        				(int)width, 
        				(int)height,
        				renderTint,
        				false);
        	else TGraphics.draw(movementAnimationArray.getAttackFrame(directionFace, 1), 
        				(int)getRenderX(), 
        				(int)getRenderY(), 
        				0, 
        				(int)width,
        				(int)height,
        				renderTint,
        				false);
        } else attackTimer = attackCooldownAmt;
        
        // Facilitates fair attack requesting...
    	if(requestAttack() && attackTimer == attackCooldownAmt && !isInWater) {
    		attackProcedure();
    		attackTimer = 0.0f;
    	}
    	
    	// Queues idle animations if not moving...
        if(!isMoving)
        	animationLabel = lastNonZeroDirection;
        
        // Sets final deduced state...
	    getAnimationController().setAnimation(animationLabel);
	}
	
	/**
	 * Updates {@link TLifeform}'s direction to look toward the direction in which they are moving.
	 */
	protected void calculateFacingDirection() {
        if (movementVector.x > 0)
            if (movementVector.y > 0) directionFace = TMath.NORTH_EAST;
            else if (movementVector.y < 0) directionFace = TMath.SOUTH_EAST;
            else directionFace = TMath.EAST;
        else if (movementVector.x < 0)
            if (movementVector.y > 0) directionFace = TMath.NORTH_WEST;
            else if (movementVector.y < 0) directionFace = TMath.SOUTH_WEST;
            else directionFace = TMath.WEST;
        else if (movementVector.y > 0) directionFace = TMath.NORTH;
        else if (movementVector.y < 0) directionFace = TMath.SOUTH;
	}

	/**
	 * Returns true if the TLifeform is currently in water.
	 */
	public final boolean isInWater() {
		return isInWater;
	}
	
	/**
	 * Returns true if the TLifeform is currently moving.
	 */
	public final boolean isMoving() {
		return isMoving;
	}
	
}
