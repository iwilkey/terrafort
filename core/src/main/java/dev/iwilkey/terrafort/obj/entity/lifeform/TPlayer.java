package dev.iwilkey.terrafort.obj.entity.lifeform;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.anim.TMovementAnimationArray;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * The player of Terrafort; entity controlled by the user.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPlayer extends TLifeform {
	
	public static final int   PLAYER_MAX_HP     = 10;
	public static final float PLAYER_WALK_SPEED = 48.0f;
	public static final float PLAYER_RUN_SPEED  = 96.0f;
	public static final float PLAYER_WIDTH      = 16.0f;
	public static final float PLAYER_HEIGHT     = 32.0f;
	
	private float   		  moveSpeedWalk;
	private float   		  moveSpeedRun;
	
	public TPlayer(TWorld world) {
		super(world,
			  true,
			  0.0f,
			  0.0f,
			  0,
			  PLAYER_WIDTH,
			  PLAYER_HEIGHT,
			  3.0f,
			  3.0f,
			  0,
			  0,
			  1,
			  2,
			  Color.WHITE.cpy(),
			  PLAYER_MAX_HP,
			  new TMovementAnimationArray(new TFrame(0, 0, 1, 2)));
		setGraphicsColliderOffset(-1, 6);
		moveSpeedWalk = PLAYER_WALK_SPEED;
		moveSpeedRun  = PLAYER_RUN_SPEED;
		setMoveSpeed(PLAYER_WALK_SPEED);
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {
		super.task(dt);
		focusCamera();
		// water mechanics...
		if(isInWater()) {
			moveSpeedWalk             = PLAYER_WALK_SPEED / 3f;
			moveSpeedRun              = PLAYER_RUN_SPEED / 3f;
			dataSelectionSquareHeight = 1;
			height                    = PLAYER_HEIGHT / 2f;
		} else {
			moveSpeedWalk             = PLAYER_WALK_SPEED;
			moveSpeedRun              = PLAYER_RUN_SPEED;
			dataSelectionSquareHeight = 2;
			height                    = PLAYER_HEIGHT;
		}
	}
	
	@Override
	public void movementProcedure() {
		if(TInput.left) moveLeft();
		if(TInput.right) moveRight();
		if(TInput.up) moveUp();
		if(TInput.down) moveDown();
		if(TInput.run) setMoveSpeed(moveSpeedRun);
		else setMoveSpeed(moveSpeedWalk);
	}
	
	/**
	 * Centers the camera view to the current position of the player.
	 */
	private void focusCamera() {
		TGraphics.setCameraTargetPosition(getRenderX(), getRenderY());
	}

	@Override
	public void die() {

	}

}
