package dev.iwilkey.terrafort.obj.entity.mob;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemStackCollection;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TWorld;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TInventoryInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TMinimapInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TPlayerStatisticsInterface;

/**
 * The player of Terrafort; entity controlled by the user.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPlayer extends TMob {
	
	public static final int   PLAYER_MAX_HP     = 128;
	public static final int   PLAYER_MAX_HUNGER = 128;
	public static final int   PLAYER_MAX_ENERGY = 128;
	public static final float PLAYER_WALK_SPEED = 48.0f;
	public static final float PLAYER_RUN_SPEED  = 96.0f;
	public static final float PLAYER_WIDTH      = 16.0f;
	public static final float PLAYER_HEIGHT     = 32.0f;
	
	private int                        hunger;
	private int                        energy;
	
	private TItemStackCollection       inventory;
	private TInventoryInterface        inventoryInterface;
	private TMinimapInterface          minimapInterface;
	private TPlayerStatisticsInterface statisticsInterface;
	
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
			  Color.GREEN.cpy(),
			  PLAYER_MAX_HP,
			  new TLifeformAnimationArray(new TFrame(0, 0, 1, 2), new TFrame(14, 0, 1, 2)));
		setGraphicsColliderOffset(-1, 8);
		setMoveSpeed(PLAYER_WALK_SPEED);
		setAttackCooldownTime(0.16f);
		hunger = PLAYER_MAX_HUNGER;
		energy = PLAYER_MAX_ENERGY;
	}
	
	public int getHungerPoints() {
		return hunger;
	}
	
	public int getEnergyPoints() {
		return energy;
	}
	
	/**
	 * Gives the player a given amount of hunger points. Resulting hunger will be clamped between
	 * [0, PLAYER_MAX_HUNGER].
	 */
	public void giveHungerPoints(int hunger) {
		this.hunger += hunger;
		this.hunger = (int)TMath.clamp(this.hunger, 0.0f, PLAYER_MAX_HUNGER);
	}
	
	/**
	 * Takes from the player a given amount of hunger points. Resulting hunger will be clamped between
	 * [0, PLAYER_MAX_HUNGER].
	 */
	public void takeHungerPoints(int hunger) {
		this.hunger -= hunger;
		this.hunger = (int)TMath.clamp(this.hunger, 0.0f, PLAYER_MAX_HUNGER);
	}
	
	/**
	 * Gives the player a given amount of energy points. Resulting energy will be clamped between
	 * [0, PLAYER_MAX_ENERGY].
	 */
	public void giveEnergyPoints(int energy) {
		this.energy += energy;
		this.energy = (int)TMath.clamp(this.energy, 0.0f, PLAYER_MAX_ENERGY);
	}
	
	/**
	 * Takes from the player a given amount of energy points. Resulting energy will be clamped between
	 * [0, PLAYER_MAX_ENERGY].
	 */
	public void takeEnergyPoints(int energy) {
		this.energy -= energy;
		this.energy = (int)TMath.clamp(this.energy, 0.0f, PLAYER_MAX_ENERGY);
	}
	
	/**
	 * Gets the current state of the player's inventory.
	 */
	public TItemStackCollection getInventory() {
		return inventory;
	}
	
	/**
	 * Attempts to place a {@link TItem} in the players inventory, returns false if the action cannot be completed.
	 */
	public boolean giveItem(TItem item) {
		return inventory.addItem(item);
	}

	@Override
	public void spawn() {
		// give the abstract inventory.
		inventory = new TItemStackCollection(12);
		// give a way to see and interact with the inventory.
		inventoryInterface = new TInventoryInterface(this);
		inventoryInterface.init();
		TUserInterface.addContainer(inventoryInterface);
		// give the minimap utility.
		minimapInterface = new TMinimapInterface(this);
		minimapInterface.init();
		TUserInterface.addContainer(minimapInterface);
		// give the player statistics
		statisticsInterface = new TPlayerStatisticsInterface(this);
		statisticsInterface.init();
		TUserInterface.addContainer(statisticsInterface);
	}

	@Override
	public void task(float dt) {
		super.task(dt);
		focusCamera();
	}
	
	float energyTime = 0.0f;
	
	@Override
	public void movementProcedure() {
		boolean actuallyMoving = false;
		if(TInput.left) {
			actuallyMoving = true;
			moveLeft();
		}
		if(TInput.right) {
			actuallyMoving = true;
			moveRight();
		}
		if(TInput.up) {
			actuallyMoving = true;
			moveUp();
		}
		if(TInput.down) {
			actuallyMoving = true;
			moveDown();
		}
		energyTime += TClock.dt();
		energyTime %= 1.0f;
		if(TInput.run && energy > 0) {
			setMoveSpeed((TInput.slide) ? (PLAYER_RUN_SPEED / 2f) : PLAYER_RUN_SPEED);
			if(energyTime > 0.5f && actuallyMoving) {
				takeEnergyPoints(3);
				energyTime = 0.0f;
			} else if(energyTime > 0.5f && !actuallyMoving) {
				giveEnergyPoints(ThreadLocalRandom.current().nextInt(2, 4));
				energyTime = 0.0f;
			}
			return;
		} 
		
		setMoveSpeed((TInput.slide) ? (PLAYER_WALK_SPEED / 2f) : PLAYER_WALK_SPEED);
		if(energyTime > 0.5f) {
			giveEnergyPoints(2);
			energyTime = 0.0f;
		}
		
	}
	
	@Override
	public boolean requestAttack() {
		if(TInput.attack && energy > 2) {
			TInput.attack = false;
			takeEnergyPoints(2);			
			return true;
		}
		return false;
	}
	
	@Override
	public void attackProcedure() {
		final Array<TObject> manifold = getCollisionManifold();
		if(manifold.size != 0) {
			final TEntity picked = (TEntity)getNextCollisionFromManifold();
			picked.onInteraction(this);
		}
	}
	
	@Override
	public void onInteraction(TMob interactee) {
		
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
		TUserInterface.removeContainer(minimapInterface);
		TUserInterface.removeContainer(statisticsInterface);
	}

}
