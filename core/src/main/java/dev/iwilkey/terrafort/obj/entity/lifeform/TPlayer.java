package dev.iwilkey.terrafort.obj.entity.lifeform;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
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
			  new TLifeformAnimationArray(new TFrame(0, 0, 1, 2), new TFrame(14, 0, 1, 2)));
		setGraphicsColliderOffset(-1, 8);
		setMoveSpeed(PLAYER_WALK_SPEED);
		setAttackCooldownTime(0.16f);
	}

	@Override
	public void spawn() {

	}

	@Override
	public void task(float dt) {
		super.task(dt);
		focusCamera();
	}
	
	@Override
	public void movementProcedure() {
		if(TInput.left) moveLeft();
		if(TInput.right) moveRight();
		if(TInput.up) moveUp();
		if(TInput.down) moveDown();
		if(TInput.run) setMoveSpeed(PLAYER_RUN_SPEED);
		else setMoveSpeed(PLAYER_WALK_SPEED);
	}
	
	@Override
	public boolean requestAttack() {
		if(TInput.attack) {
			TInput.attack = false;
			return true;
		}
		return false;
	}
	
	@Override
	public void attackProcedure() {
		if(getCollisionManifold().size != 0) {
			// Interact with all entities in manifold? Just one? Based off of direction?
			for(TObject o : getCollisionManifold()) {
				if(o instanceof TEntity) {
					((TEntity)o).onInteraction(this);
					break;
				}
			}
		}
	}
	
	@Override
	public void onInteraction(TLifeform interactee) {
		
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
