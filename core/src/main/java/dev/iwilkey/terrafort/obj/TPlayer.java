package dev.iwilkey.terrafort.obj;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TTileTerrainRenderer;
import dev.iwilkey.terrafort.gfx.anim.TAnimation;
import dev.iwilkey.terrafort.gfx.anim.TAnimationController;

public final class TPlayer extends TAnimal {
	
	public static final TAnimation PLAYER_IDLE_SOUTH      = new TAnimation("idle_south", 
            												new TFrame(0, 0, 1, 2));
	public static final TAnimation PLAYER_IDLE_SOUTH_EAST = new TAnimation("idle_south_east", 
            												new TFrame(0, 2, 1, 2));
	public static final TAnimation PLAYER_IDLE_EAST       = new TAnimation("idle_east", 
            												new TFrame(0, 4, 1, 2));
	public static final TAnimation PLAYER_IDLE_NORTH_EAST = new TAnimation("idle_north_east", 
            												new TFrame(0, 6, 1, 2));
	public static final TAnimation PLAYER_IDLE_NORTH      = new TAnimation("idle_north", 
            												new TFrame(0, 8, 1, 2));
	public static final TAnimation PLAYER_IDLE_NORTH_WEST = new TAnimation("idle_north_west", 
            												new TFrame(0, 10, 1, 2));
	public static final TAnimation PLAYER_IDLE_WEST       = new TAnimation("idle_west", 
            												new TFrame(0, 12, 1, 2));
	public static final TAnimation PLAYER_IDLE_SOUTH_WEST = new TAnimation("idle_south_west", 
            												new TFrame(0, 14, 1, 2));
	public static final TAnimation PLAYER_MOVE_SOUTH      = new TAnimation("move_south", 
													        new TFrame(0, 0, 1, 2),
													        new TFrame(1, 0, 1, 2),
													        new TFrame(0, 0, 1, 2),
													        new TFrame(2, 0, 1, 2));
	public static final TAnimation PLAYER_MOVE_SOUTH_EAST = new TAnimation("move_south_east", 
												            new TFrame(0, 2, 1, 2),
												            new TFrame(1, 2, 1, 2),
												            new TFrame(0, 2, 1, 2),
												            new TFrame(2, 2, 1, 2));
	public static final TAnimation PLAYER_MOVE_EAST       = new TAnimation("move_east", 
	           												new TFrame(0, 4, 1, 2),
	           												new TFrame(1, 4, 1, 2),
	           												new TFrame(0, 4, 1, 2),
	           												new TFrame(2, 4, 1, 2));
	public static final TAnimation PLAYER_MOVE_NORTH_EAST = new TAnimation("move_north_east", 
	           												new TFrame(0, 6, 1, 2),
	           												new TFrame(1, 6, 1, 2),
	           												new TFrame(0, 6, 1, 2),
	           												new TFrame(2, 6, 1, 2));
	public static final TAnimation PLAYER_MOVE_NORTH      = new TAnimation("move_north", 
	           												new TFrame(0, 8, 1, 2),
	           												new TFrame(1, 8, 1, 2),
	           												new TFrame(0, 8, 1, 2),
	           												new TFrame(2, 8, 1, 2));
	public static final TAnimation PLAYER_MOVE_NORTH_WEST = new TAnimation("move_north_west", 
	           												new TFrame(0, 10, 1, 2),
	           												new TFrame(1, 10, 1, 2),
	           												new TFrame(0, 10, 1, 2),
	           												new TFrame(2, 10, 1, 2));
	public static final TAnimation PLAYER_MOVE_WEST       = new TAnimation("move_west", 
	           												new TFrame(0, 12, 1, 2),
	           												new TFrame(1, 12, 1, 2),
	           												new TFrame(0, 12, 1, 2),
	           												new TFrame(2, 12, 1, 2));
	public static final TAnimation PLAYER_MOVE_SOUTH_WEST = new TAnimation("move_south_west", 
	           												new TFrame(0, 14, 1, 2),
	           												new TFrame(1, 14, 1, 2),
	           												new TFrame(0, 14, 1, 2),
	           												new TFrame(2, 14, 1, 2));
	
	private float   										moveSpeedWalk;
	private float   										moveSpeedRun;
	private float   										currentMoveSpeed;
	private Vector2 										movementVector;
	private boolean 										isMoving;
	private String 		 									lastNonZeroDirection;
	
	public TPlayer(TWorld world) {
		super(world,
			  true,
			  0.0f,
			  0.0f,
			  0,
			  16.0f,
			  32.0f,
			  3.0f,
			  4.0f,
			  0,
			  0,
			  1,
			  2,
			  Color.WHITE.cpy(),
			  10);
		setGraphicsColliderOffset(0, 6);
		setHealthBarOffset(0.30f, 10.25f);
		movementVector       = new Vector2();
		moveSpeedWalk        = 48.0f;
		moveSpeedRun         = 96.0f;
		currentMoveSpeed     = moveSpeedWalk;
		lastNonZeroDirection = "idle_south";
		isMoving             = false;
	}
	
	@Override
	public void initAnimations(TAnimationController anim) {
		anim.setTargetFrameRate(10.0f);
		anim.addAnimation(PLAYER_IDLE_SOUTH);
		anim.addAnimation(PLAYER_IDLE_SOUTH_EAST);
		anim.addAnimation(PLAYER_IDLE_EAST);
		anim.addAnimation(PLAYER_IDLE_NORTH_EAST);
		anim.addAnimation(PLAYER_IDLE_NORTH);
		anim.addAnimation(PLAYER_IDLE_NORTH_WEST);
		anim.addAnimation(PLAYER_IDLE_WEST);
		anim.addAnimation(PLAYER_IDLE_SOUTH_WEST);
		anim.addAnimation(PLAYER_MOVE_SOUTH);
		anim.addAnimation(PLAYER_MOVE_SOUTH_EAST);
		anim.addAnimation(PLAYER_MOVE_EAST);
		anim.addAnimation(PLAYER_MOVE_NORTH_EAST);
		anim.addAnimation(PLAYER_MOVE_NORTH);
		anim.addAnimation(PLAYER_MOVE_NORTH_WEST);
		anim.addAnimation(PLAYER_MOVE_WEST);
		anim.addAnimation(PLAYER_MOVE_SOUTH_WEST);
		// TODO: Make the starting animation random every time.
		anim.setAnimation("idle_south");
	}

	@Override
	public void spawn() {

	}
	
	float t = 0;
	
	@Override
	public void task(float dt) {
		super.task(dt);
		t += dt;
		if(t > 0.25f) {
			if(ThreadLocalRandom.current().nextDouble() <= 0.50f)
				heal(getMaxHP());
			else hurt(ThreadLocalRandom.current().nextInt(1, getMaxHP() / 4));
			t = 0.0f;
		}
		focusCamera();
		calculateMovement();
		calculateGraphics();
		if(Gdx.input.isButtonJustPressed(Buttons.LEFT))
			build();
	}
	
	private void build() {
		Vector2 worldTilePos = world.roundMousePositionToWorldTileGrid();
		world.addObject(new TObject(world, 
									false, 									
									worldTilePos.x, 
									worldTilePos.y, 
									1, 
									TTileTerrainRenderer.TERRAIN_TILE_WIDTH, 
									TTileTerrainRenderer.TERRAIN_TILE_HEIGHT,
									TTileTerrainRenderer.TERRAIN_TILE_WIDTH / 2f,
									TTileTerrainRenderer.TERRAIN_TILE_HEIGHT /2f,
									ThreadLocalRandom.current().nextInt(3, 7), 
									2, 
									1, 
									1, 
									Color.WHITE.cpy()));
	}
	
	/**
	 * Centers the camera view to the current position of the player.
	 */
	private void focusCamera() {
		TGraphics.setCameraTargetPosition(getRenderX(), getRenderY());
	}
	
	/**
	 * Listens to user input and moves player accordingly.
	 */
	private void calculateMovement() {
		movementVector.setZero();
		if(Gdx.input.isKeyPressed(Keys.A))
			movementVector.add(-currentMoveSpeed, 0);
		if(Gdx.input.isKeyPressed(Keys.D))
			movementVector.add(currentMoveSpeed, 0);
		if(Gdx.input.isKeyPressed(Keys.W))
			movementVector.add(0, currentMoveSpeed);
		if(Gdx.input.isKeyPressed(Keys.S))
			movementVector.add(0, -currentMoveSpeed);
		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			currentMoveSpeed = moveSpeedRun;
		else currentMoveSpeed = moveSpeedWalk;
		setVelocity(movementVector.x, movementVector.y);
		isMoving = (movementVector.x != 0 || movementVector.y != 0);
		getAnimationController().setTargetFrameRate(currentMoveSpeed / 6);
	}
	
	/**
	 * Calculates what animation should play based on player state.
	 */
	private void calculateGraphics() {
		String animationLabel = lastNonZeroDirection;
		animationLabel = lookTowardMovementDirection(animationLabel);
        if (isMoving) lastNonZeroDirection = animationLabel.replace("move_", "idle_");
        else {
        	if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
        		// Attack!
        		lookTowardMouse();
        	} else animationLabel = lastNonZeroDirection;
        }
	    getAnimationController().setAnimation(animationLabel);
	}
	
	/**
	 * Update player direction to look toward the direction in which they are moving.
	 */
	private String lookTowardMovementDirection(String animationLabel) {
        if (movementVector.x > 0)
            if (movementVector.y > 0) animationLabel = "move_north_east";
            else if (movementVector.y < 0) animationLabel = "move_south_east";
            else animationLabel = "move_east";
        else if (movementVector.x < 0)
            if (movementVector.y > 0) animationLabel = "move_north_west";
            else if (movementVector.y < 0) animationLabel = "move_south_west";
            else animationLabel = "move_west";
        else if (movementVector.y > 0) animationLabel = "move_north";
        else if (movementVector.y < 0) animationLabel = "move_south";
        return animationLabel;
	}

	/**
	 * Update the direction of the player to look toward the mouse.
	 */
	private void lookTowardMouse() {
	    final Vector2 mousePos = world.getMousePositionInWorld();
	    final Vector2 direction = new Vector2(mousePos.x - getRenderX(), mousePos.y - getRenderY()).nor();
	    final float angle = direction.angleDeg();
	    if (angle >= 22.5 && angle < 67.5) lastNonZeroDirection = "idle_north_east";
	    else if (angle >= 67.5 && angle < 112.5) lastNonZeroDirection = "idle_north";
	    else if (angle >= 112.5 && angle < 157.5) lastNonZeroDirection = "idle_north_west";
	    else if (angle >= 157.5 && angle < 202.5) lastNonZeroDirection = "idle_west";
	    else if (angle >= 202.5 && angle < 247.5) lastNonZeroDirection = "idle_south_west";
	    else if (angle >= 247.5 && angle < 292.5) lastNonZeroDirection = "idle_south";
	    else if (angle >= 292.5 && angle < 337.5) lastNonZeroDirection = "idle_south_east";
	    else lastNonZeroDirection = "idle_east";
	}
	
	public boolean isMoving() {
		return isMoving;
	}

	@Override
	public void die() {
		
	}

}
