package dev.iwilkey.terrafort.obj.entity.mob;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.gfx.shape.TLine;
import dev.iwilkey.terrafort.math.TCollisionManifold;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.particulate.TParticulate;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A more specific, yet still abstract {@link TEntity}. Has special properties and functions that all Terrafort 
 * intelligent beings share. Ignores lighting.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TMob extends TEntity {
	
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
	
	public TMob(TWorld  world, 
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
		getPhysicalFixture().getFilterData().categoryBits = TCollisionManifold.IGNORE_GROUP;
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
		isInWater = world.getOrGenerateTileHeightAt(getCurrentTileX(), getCurrentTileY()) == TTerrain.TERRAIN_LEVELS - 1;
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
	 * Method that returns true when a {@link TMob} wants to attack. Might be ignored based on internal 
	 * attack timer.
	 */
	public abstract boolean requestAttack();
	
	/**
	 * Function called when a {@link TMob}'s attack request has been accepted and will attack this frame.
	 */
	public abstract void attackProcedure();
	
	/**
	 * Function called to calculate the movement of a {@link TMob}.
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
	
	@Override
	public void hurt(int amt) {
		super.hurt(amt);
		for(int i = 0; i < ThreadLocalRandom.current().nextInt(8, 16); i++)
			world.addObject(new TParticle(world, getActualX(), getActualY(), Color.RED.cpy()));
	}
	
	TObject rr; // memory that is referenced by the callback below.
	final RayCastCallback cb = new RayCastCallback() {
	    @Override
	    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
	    	rr = (TObject)fixture.getBody().getUserData();
	    	// Don't let particles get in the way...
	    	if(rr instanceof TParticulate) {
	    		rr = null;
	    		return -1;
	    	}
	        return 0;
	    }
	};
	
	/**
	 * Casts a ray with given length from the {@link TMob}s origin in the facing direction of the {@link TMob}. 
	 * Returns the first object it collides with, null if none. This acts as a {@link TMob}s way to "sense" the physical
	 * environment they exist in.
	 * 
	 * <p>
	 * This function also requires a "fov" and "spread".
	 * </p>
	 * 
	 * <p>
	 * The "foa" stands for "Field of Attack" where every "spread" line will extend +- "foa" radians from the last one. 
	 * This implies that, indeed, "spread" is the amount of rays to extend on each side of the center ray.
	 * </p>
	 * 
	 * <p>
	 * This method is optimized to return the first valid hit, so best case it wil only cast one center ray. Worst case, 
	 * (spread * 2) + 1 rays will be casted, all in search for a colliding {@link TObject}.
	 * </p>
	 */
	public final TObject sense(float length, float foa, int spread) {
		int cdir = getFacingDirection();
		int dx   = TMath.DX[cdir];
		int dy   = -TMath.DY[cdir];
		final Vector2 origin = new Vector2(getActualX(), getActualY());
		final Vector2 to     = origin.cpy().add(dx * length, dy * length);
		// only draw the rays if the world is in debug mode...
		if(world.isDebug()) {
			final TLine l = new TLine(origin.x, origin.y, to.x, to.y);
			TGraphics.draw(l, true);
		}
		// try the center ray first...
		rr = null;
		world.getPhysicalWorld().rayCast(cb, origin, to);
		if(rr != null)
			return rr;
		// nothing? let's try "spread" more +- foa * lineNum from the center line...
		final float rad = foa;
		for(int i = 1; i <= spread * 2; i++) {
			final float   arad  = ((i & 1) == 0) ? rad * i : -rad * i;
			final Vector2 rotto = to.cpy().rotateAroundRad(origin, arad);
			if(world.isDebug()) {
				final TLine line = new TLine(origin.x, origin.y, rotto.x, rotto.y);
				TGraphics.draw(line, true);
			}
			rr = null;
			world.getPhysicalWorld().rayCast(cb, origin, rotto);
			if(rr != null)
				return rr;
		}
		return null;
	}
	
	public final void setMoveSpeed(float moveSpeed) {
		this.requestedMoveSpeed = moveSpeed;
	}
	
	/**
	 * Sets the {@link TMob} attack cooldown time.
	 */
	public final void setAttackCooldownTime(float time) {
		attackCooldownAmt = time;
	}
	
	boolean set = false;
	
	/**
	 * Calculates what animation should play based on {@link TMob} state.
	 */
	private final void calculateGraphics(float dt) {
		// Animates a {@link TLifeform} moving in any of the 8 possible directions based on the animations provided by the
		// {@link TLifeformAnimationArray}...
		getAnimationController().setTargetFrameRate(requestedMoveSpeed / 6);
		String label = TLifeformAnimationArray.LABELS[directionFace];
        lastNonZeroDirection = label.replace("move_", "idle_");
        // Handles the attack clock and animations...
        if(attackTimer < attackCooldownAmt) {
        	attackTimer += dt;
        	if(!set) {
        		// getAnimationController().setTargetFrameRate((1f / attackCooldownAmt) * 2);
        		getAnimationController().reset();
        		set = true;
        	}
        	getAnimationController().setTargetFrameRate((1f / attackCooldownAmt) * 1.9f);
        	getAnimationController().setAnimation(label.replace("move_", "attack_"));
        } else {
        	attackTimer = attackCooldownAmt;
        	if(!isMoving)
            	label = lastNonZeroDirection;
        	// Sets final deduced state (if not attacking)...
        	getAnimationController().setAnimation(label);
        	set = false;
        }
        // Facilitates fair attack requesting...
    	if(requestAttack() && attackTimer == attackCooldownAmt && !isInWater) {
    		attackProcedure();
    		attackTimer = 0.0f;
    	}
	}
	
	/**
	 * Updates {@link TMob}'s direction to look toward the direction in which they are moving.
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
	 * Force the attack timer to set, causing the next couple of frames to be the attack animation.
	 */
	protected void requestAttackAnimation() {
		attackTimer = 0.0f;
	}
	
	/**
	 * Returns the direction that the {@link TMob} is currently facing. The enumeration is found in the {@link TMath} module.
	 */
	public final int getFacingDirection() {
		return directionFace;
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
