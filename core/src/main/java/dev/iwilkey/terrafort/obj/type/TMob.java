package dev.iwilkey.terrafort.obj.type;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;
import dev.iwilkey.terrafort.world.terrain.TBiome;

/**
 * A more complex {@link TEntity} with interaction, movement, and distinct states. This class serves as an abstract 
 * representation of the mob's state, rather than it's physical presence.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TMob extends TEntity {

	/**
	 * The only sheet that is allowed to render a {@link TMob}.
	 */
	public transient static final String MOB_SHEET = "sheets/mob.png";
	
	/**
	 * The base speed of the mob animation sequences. Seconds per frame.
	 */
	public transient static final float BASE_MOVEMENT_ANIMATION_FRAME_RATE = 0.12f;
	
	/**
	 * The base speed of the mob action animation sequences. Seconds per frame.
	 */
	public transient static final float BASE_ACTION_ANIMATION_FRAME_RATE = BASE_MOVEMENT_ANIMATION_FRAME_RATE  * 0.95f;
	
	/**
	 * The frames of the movement animation sequence.
	 */
	public transient static final int[] MOVEMENT_ANIMATION_SEQUENCE = new int[] { 0, 1, 0, 2 };
	
	/**
	 * The frames of the attack animation sequence.
	 */
	public transient static final int[] ATTACK_ANIMATION_SEQUENCE = new int[] { 3, 4 };
	
	/**
	 * The set of frames that outline the location of every bare body frame, organized by direction.
	 */
	public transient static final TFrame[][] BODY_ANIMATION_ARRAY = new TFrame[8][5];
	
	/**
	 * The set of frames that outline the location of every clothing frame, organized by direction.
	 */
	public transient static final TFrame[][] CLOTHING_ANIMATION_ARRAY = new TFrame[8][5];
	
	/**
	 * True if the physical velocity of the mob is not zero. This has nothing to do with it's {@link TState}. Managed internally. Doesn't need to be serialized.
	 */
	private transient boolean isMovingPhysically;
	
	/**
	 * Static section populates the animation arrays with the correct graphical mappings.
	 */
	static {
		for(int i = 0; i < 8; i++) {
			// body...
			for(int j = 0; j < 5; j++) {
				int dataX = j;
				int dataY = i * 2;
				BODY_ANIMATION_ARRAY[i][j] = new TFrame(dataX, dataY, 1, 2);
			}
			// clothing...
			for(int j = 5; j < 10; j++) {
				int dataX = j;
				int dataY = i * 2;
				CLOTHING_ANIMATION_ARRAY[i][j - 5] = new TFrame(dataX, dataY, 1, 2);
			}
		}
	}
	
	/**
	 * The current state the {@link TMob} is in, which dictates what animation is playing.
	 * @author Ian Wilkey (iwilkey)
	 */
	public enum TState implements Serializable {
		MOVING,
		IDLE,
		ACTION
	}

	/**
	 * The unique serialization ID.
	 */
	private static final long serialVersionUID = -7159028835046881979L;
	
	/**
	 * What is the current state of the mob?
	 */
	public TState currentState;
	
	/**
	 * The regular height of the mob, such for when it needs to change.
	 */
	public float regularHeight;
	
	/**
	 * Updated if the entity requests to move or interact. Basically enumerates the last
	 * non-ambiguous facing direction.
	 */
	public int facingDirection;
	
	/**
	 * What is the speed the mob will move next time it requests to move?
	 */
	public float potentialMoveSpeed;
	
	/**
	 * What is the mobs move speed multipied by when in water?
	 */
	public float waterMovementMultiplier;
	
	/**
	 * What multiplier will be added to the potential move speed for the mob? This is good for sprinting, swimming, etc.
	 * It will also change the animation frame rate relative to the base rate.
	 */
	public float potentialMoveSpeedMultiplier;
	
	/**
	 * What is the color of this mob's clothing? RGBA hex format.
	 */
	public int clothingColor;
	
	/**
	 * The amount of time required between each attack to register a new one.
	 */
	public float actionCooldown;
	
	// attributes that are internal but still should be serialized.
	private float actionTimer;
	private float movementVectorX;
	private float movementVectorY;
	private float animationTimer;
	private float animationTargetFrameRate;
	private int   animationSequenceIndex;
	private int   arrayFrame;

	@Override
	public void tick(final TObjectRuntime concrete, float dt) {
		super.tick(concrete, dt);
		spriteSheet = MOB_SHEET;
		movementProcedure(concrete, dt);
		handleAction(concrete, dt);
		handleMovementAndState(concrete);
		handleAnimation(concrete, dt);
		isMovingPhysically = !concrete.getPhysical().getLinearVelocity().isZero(0.1f);
	}
	
	/**
	 * A function that, when returns true, indicates that the mob wants to attack. This is just a request and can be denied by the attack cooldown system.
	 */
	public abstract boolean requestAction();
	
	/**
	 * Returns the direction the next accepted action should occur in. Should be an integer [0, 8], as per the {@link TMath} direction enumerations.
	 */
	public abstract int actionDirection();
	
	/**
	 * The procedure to run during an accepted action event. Gives the last requested action direction.
	 */
	public abstract void actionProcedure(final TObjectRuntime concrete, int direction);
	
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
	public abstract void movementProcedure(final TObjectRuntime concrete, float dt);
	
	public final void moveLeft() {
		movementVectorX -= potentialMoveSpeed;
	}
	
	public final void moveRight() {
		movementVectorX += potentialMoveSpeed;
	}
	
	public final void moveUp() {
		movementVectorY += potentialMoveSpeed;
	}
	
	public final void moveDown() {
		movementVectorY -= potentialMoveSpeed;
	}
	
	/**
	 * Instructs the mob to move in any of the 8 {@link TMath} directions.
	 */
	public final void move(int direction) {
		movementVectorX += TMath.DX[direction] * potentialMoveSpeed;
		movementVectorY -= TMath.DY[direction] * potentialMoveSpeed;
	}
	
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
	public final TObject sense(final TObjectRuntime concrete, float length, float foa, int spread) {
		int     cdir;
		int     dx;
		int     dy;
		Vector2 origin;
		Vector2 centerVec;
		Vector2 supportVec;
		float   supportAngle;
		cdir      = facingDirection;
		dx        = TMath.DX[cdir];
		dy        = -TMath.DY[cdir];
		origin    = new Vector2(worldX, worldY);
		centerVec = origin.cpy().add(dx * length, dy * length);
		for(int i = 0; i <= spread * 2; i++) {
			supportAngle      = ((i & 1) == 0) ? foa * i : -foa * i;
			supportVec        = centerVec.cpy().rotateAroundRad(origin, supportAngle);
			final TObject res = concrete.ray(origin, supportVec);
			// TGraphics.draw(new TLine(origin.x, origin.y, supportVec.x, supportVec.y));
			if(res != null)
				return res;
		}
		return null;
	}

	/**
	 * Handles the process of interacting.
	 */
	private void handleAction(final TObjectRuntime concrete, float dt) {
		actionTimer += (actionTimer < actionCooldown) ? dt : 0;
		if(requestAction() && (actionTimer >= actionCooldown)) {
			currentState             = TState.ACTION;
			animationTimer           = 0.0f;
			animationSequenceIndex   = 0;
			arrayFrame               = 0;
			animationTargetFrameRate = (actionCooldown / ATTACK_ANIMATION_SEQUENCE.length) / 1.5f;
			facingDirection          = actionDirection();
			actionTimer              = 0.0f;
			actionProcedure(concrete, facingDirection);
		}
	}
	
	private void handleMovementAndState(final TObjectRuntime concrete) {
		// Mobs should have a fixed rotation (don't react to torque.)
		concrete.getPhysical().setFixedRotation(true);
		// set the linear velocity to whatever the movement vector dictates it should be.
		if(inWater()) {
			movementVectorX *= waterMovementMultiplier;
			movementVectorY *= waterMovementMultiplier;
		}
		concrete.getPhysical().setLinearVelocity(movementVectorX * potentialMoveSpeedMultiplier, movementVectorY * potentialMoveSpeedMultiplier);
		if((movementVectorX != 0 || movementVectorY != 0) && currentState != TState.ACTION) {
			currentState = TState.MOVING;
			// the mob is moving (and not attacking) so, we should update the facing direction...
			if(movementVectorX > 0)
	            if(movementVectorY > 0)      facingDirection = TMath.NORTH_EAST;
	            else if(movementVectorY < 0) facingDirection = TMath.SOUTH_EAST;
	            else                         facingDirection = TMath.EAST;
	        else if(movementVectorX < 0)
	            if(movementVectorY > 0)      facingDirection = TMath.NORTH_WEST;
	            else if(movementVectorY < 0) facingDirection = TMath.SOUTH_WEST;
	            else                         facingDirection = TMath.WEST;
	        else if(movementVectorY > 0)     facingDirection = TMath.NORTH;
	        else if(movementVectorY < 0)     facingDirection = TMath.SOUTH;
		} else if(currentState != TState.ACTION) {
			animationTimer = 0.0f;
			currentState   = TState.IDLE;
		}
		// reset movement vector.
		movementVectorX = 0.0f;
		movementVectorY = 0.0f;
	}
	
	/**
	 * Handles what animation frame should be rendered.
	 */
	private void handleAnimation(final TObjectRuntime concrete, float dt) {
		if(currentState == TState.MOVING) {
			// animation frame rate should be a factor of move speed multiplier... only change when moving...
			animationTargetFrameRate = BASE_MOVEMENT_ANIMATION_FRAME_RATE / potentialMoveSpeedMultiplier;
			if(inWater()) animationTargetFrameRate = 0.25f;
		}
		switch(currentState) {
			case IDLE:
				arrayFrame = 0; // idle frame is just the first frame of each facing direction...
				break;
			case MOVING:
				progressAnimation(dt, MOVEMENT_ANIMATION_SEQUENCE.length);
				arrayFrame = MOVEMENT_ANIMATION_SEQUENCE[animationSequenceIndex];
				break;
			case ACTION:
				progressAnimation(dt, ATTACK_ANIMATION_SEQUENCE.length);
				arrayFrame = ATTACK_ANIMATION_SEQUENCE[animationSequenceIndex];
				break;
			default: throw new IllegalStateException("[Terrafort Game Engine] A TMob as entered an illegal state!");
		}
		dataX       = BODY_ANIMATION_ARRAY[facingDirection][arrayFrame].getDataOffsetX();
		dataY       = BODY_ANIMATION_ARRAY[facingDirection][arrayFrame].getDataOffsetY();
		dataWidth   = 1;
		dataHeight  = (inWater()) ? 1 : 2;
		worldHeight = (inWater()) ? (regularHeight / 2f) : regularHeight;
	}
	
	/**
	 * The current clothing frame from the {@link TMob's} animation array. Use for rendering.
	 */
	public TFrame getCurrentClothingFrame() {
		return CLOTHING_ANIMATION_ARRAY[facingDirection][arrayFrame];
	}
	
	/**
	 * The current color of the {@link TMob}'s clothing. Use for rendering.
	 * @return
	 */
	public Color getCurrentClothingColor() {
		// clothing color must always have a full alpha channel.
		clothingColor |= 0xff;
		return new Color().set(clothingColor);
	}
	
	/**
	 * Whether or not the {@link TMob} is in water.
	 */
	public boolean inWater() {
		return currentTerrainLevel == TBiome.WATER_LEVEL;
	}
	
	/**
	 * Returns if the mob is currently moving.
	 */
	public final boolean isMovingState() {
		return currentState == TState.MOVING;
	}
	
	/**
	 * Returns true if the current physical velocity of the mob is not zero. Has nothing to do with it's {@link TState}.
	 */
	public final boolean isMovingPhysically() {
		return isMovingPhysically;
	}
	/**
	 * Returns if the mob is currently performing it's action.
	 */
	public final boolean isPerformingActionState() {
		return currentState == TState.ACTION;
	}
	
	/**
	 * Returns if the mob is currently idle.
	 */
	public final boolean isIdleState() {
		return currentState == TState.IDLE;
	}
	
	/**
	 * Progresses the animation frame foward and handles the state change from attack to idle.
	 */
	private void progressAnimation(float dt, int sequenceLength) {
		animationTimer += dt;
		if(animationTimer >= animationTargetFrameRate) {
			animationSequenceIndex++;
			if(currentState == TState.ACTION) {
				if(animationSequenceIndex >= ATTACK_ANIMATION_SEQUENCE.length) {
					// switch back from action because the animation is over.
					currentState = TState.IDLE;
					animationSequenceIndex = ATTACK_ANIMATION_SEQUENCE.length - 1;
					return;
				}
			}
			animationSequenceIndex %= sequenceLength;
			animationTimer         = 0.0f;
		}
	}
}
