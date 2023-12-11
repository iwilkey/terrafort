package dev.iwilkey.terrafort.obj.entity.lifeform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemStackCollection;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.TInventoryInterface;

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
	
	private TItemStackCollection inventory;
	private TInventoryInterface  inventoryInterface;
	
	public TPlayer(TSinglePlayerWorld world) {
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
			  Color.GREEN.cpy(),
			  PLAYER_MAX_HP,
			  new TLifeformAnimationArray(new TFrame(0, 0, 1, 2), new TFrame(14, 0, 1, 2)));
		setGraphicsColliderOffset(-1, 8);
		setMoveSpeed(PLAYER_WALK_SPEED);
		setAttackCooldownTime(0.16f);
	}
	
	public TItemStackCollection getInventory() {
		return inventory;
	}

	@Override
	public void spawn() {
		inventory = new TItemStackCollection(8);
		for(int i = 0; i < (256 * 2) + 2; i++)
			inventory.addItem(TItem.TEST_ITEM);
		inventoryInterface = new TInventoryInterface(this);
		inventoryInterface.init();
		TUserInterface.addContainer(inventoryInterface);
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
		if(TInput.run) setMoveSpeed((TInput.slide) ? (PLAYER_RUN_SPEED / 2f) : PLAYER_RUN_SPEED);
		else setMoveSpeed((TInput.slide) ? (PLAYER_WALK_SPEED / 2f) : PLAYER_WALK_SPEED);
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
		Array<TObject> manifold = getCollisionManifold();
		if(manifold.size != 0) {
			TEntity picked = (TEntity)getNextCollisionFromManifold();
			picked.onInteraction(this);
		}
	}
	
	@Override
	public void onInteraction(TLifeform interactee) {
		
	}
	
	@Override
	protected void calculateFacingDirection() {
		// check if "sliding"
		if(TInput.slide)
			return;
		super.calculateFacingDirection();
	}
	
	/**
	 * Centers the camera view to the current position of the player.
	 */
	private void focusCamera() {
		TGraphics.setCameraTargetPosition(getRenderX(), getRenderY());
	}

	@Override
	public void die() {
		TUserInterface.removeContainer(inventoryInterface);
	}

}
