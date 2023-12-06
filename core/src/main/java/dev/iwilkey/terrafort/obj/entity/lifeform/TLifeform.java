package dev.iwilkey.terrafort.obj.entity.lifeform;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.gfx.anim.TMovementAnimationArray;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A more specific, yet still abstract {@link TEntity}. Has special properties and functions that all Terrafort 
 * intelligent beings share. Ignores lighting.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TLifeform extends TEntity {

	public static final float HURT_HEAL_ANIMATION_TIMER = 0.1f;
	public static final float ATTACK_COOLDOWN           = 0.2f;
	
	private TMovementAnimationArray         movementAnimationArray;
	private Vector2 		                movementVector;
	private String 		                    lastNonZeroDirection;
	private boolean 		                isMoving;
	private boolean                         isInWater;
	private int                             directionFace;
	private float   		                moveSpeed;
	private float                           hurtTimer;
	private float                           healTimer;
	private float                           attackTimer;
	
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
			       TMovementAnimationArray array) {
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
		moveSpeed              = 0.0f;
		lastNonZeroDirection   = TMovementAnimationArray.LABELS[ThreadLocalRandom.current().nextInt(0, 8)].replace("move_", "idle_");
		hurtTimer              = HURT_HEAL_ANIMATION_TIMER;
		healTimer              = HURT_HEAL_ANIMATION_TIMER;
		attackTimer            = ATTACK_COOLDOWN;
	}
	
	@Override
	public boolean shouldUseAdditiveBlending() {
		if(healTimer < HURT_HEAL_ANIMATION_TIMER)
			return true;
		return false;
	}
	
	@Override
	public void hurt(int amt) {
		super.hurt(amt);
		hurtTimer = 0.0f;
	}
	
	@Override
	public void heal(int amt) {
		int ab = getCurrentHP();
		super.heal(amt);
		if(ab != getCurrentHP())
			healTimer = 0.0f;
	}
	
	@Override
	public void task(float dt) {
		// Resets the movement vector, polls for changes, and sets the appropriate velocity.
		movementVector.setZero();
		movementProcedure();
		setVelocity(movementVector.x, movementVector.y);
		
		// Updates global flags about TLifeform's state.
		isMoving = (movementVector.x != 0 || movementVector.y != 0);
		isInWater = world.getTileHeightAt(getCurrentTileX(), getCurrentTileY()) == TTerrainRenderer.TERRAIN_LEVELS - 1;
		
		// Calculates the sprite version of the TLifeform to render based on state.
		calculateFacingDirection();
		calculateGraphics(dt);
	}
	
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
		movementVector.add(-moveSpeed, 0);
	}
	
	public final void moveRight() {
		movementVector.add(moveSpeed, 0);
	}
	
	public final void moveUp() {
		movementVector.add(0, moveSpeed);
	}
	
	public final void moveDown() {
		movementVector.add(0, -moveSpeed);
	}
	
	public final void setMoveSpeed(float moveSpeed) {
		this.moveSpeed = moveSpeed;
	}
	
	/**
	 * Calculates what animation should play based on {@link TLifeform} state.
	 */
	private final void calculateGraphics(float dt) {
		// Animates the event of a {@link TLifeform} getting hurt or healed.
		if(hurtTimer < HURT_HEAL_ANIMATION_TIMER) {
			setRenderTint(Color.RED);
			hurtTimer += dt;
		} else if(healTimer < HURT_HEAL_ANIMATION_TIMER) {
			healTimer += dt;
		} else {
			setRenderTint(Color.WHITE);
			hurtTimer = HURT_HEAL_ANIMATION_TIMER;
		}
		
		// Animates a {@link TLifeform} moving in any of the 8 possible directions based on the animations provided by the
		// {@link TLifeformMovementAnimationArray}.
		getAnimationController().setTargetFrameRate(moveSpeed / 6);
		String animationLabel = TMovementAnimationArray.LABELS[directionFace];
        lastNonZeroDirection = animationLabel.replace("move_", "idle_");
        if(!shouldDraw) 
        	shouldDraw = true;
        
        // Handles the attack clock and animations.
        if(attackTimer < ATTACK_COOLDOWN) {
        	attackTimer += dt;
        	if(attackTimer <= (ATTACK_COOLDOWN / 2f)) {
        		shouldDraw = false;
        		TGraphics.draw(TMovementAnimationArray.ATTACK[directionFace], (int)getRenderX(), (int)getRenderY(), 0, (int)width, (int)height, false);
        	}
        } else attackTimer = ATTACK_COOLDOWN;
    	if(TInput.attack && attackTimer == ATTACK_COOLDOWN && !isInWater) {
    		// Attack!
    		TInput.attack = false;
    		attackTimer = 0.0f;
    	}
    	
    	// Queues idle animations if not moving.
        if(!isMoving)
        	animationLabel = lastNonZeroDirection;
        
        // Sets final deduced state.
	    getAnimationController().setAnimation(animationLabel);
	}
	
	/**
	 * Updates {@link TLifeform}'s direction to look toward the direction in which they are moving.
	 */
	private final void calculateFacingDirection() {
        if (movementVector.x > 0)
            if (movementVector.y > 0) directionFace = TMovementAnimationArray.NORTH_EAST;
            else if (movementVector.y < 0) directionFace = TMovementAnimationArray.SOUTH_EAST;
            else directionFace = TMovementAnimationArray.EAST;
        else if (movementVector.x < 0)
            if (movementVector.y > 0) directionFace = TMovementAnimationArray.NORTH_WEST;
            else if (movementVector.y < 0) directionFace = TMovementAnimationArray.SOUTH_WEST;
            else directionFace = TMovementAnimationArray.WEST;
        else if (movementVector.y > 0) directionFace = TMovementAnimationArray.NORTH;
        else if (movementVector.y < 0) directionFace = TMovementAnimationArray.SOUTH;
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
