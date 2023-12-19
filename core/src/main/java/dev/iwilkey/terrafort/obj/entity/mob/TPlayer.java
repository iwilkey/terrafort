package dev.iwilkey.terrafort.obj.entity.mob;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.anim.TLifeformAnimationArray;
import dev.iwilkey.terrafort.gfx.shape.TCircle;
import dev.iwilkey.terrafort.gfx.shape.TRect;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.item.TItemSpec;
import dev.iwilkey.terrafort.item.TItemStack;
import dev.iwilkey.terrafort.item.TItemStackCollection;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TInventoryAndForgerInterface;
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
	public static final float REST_HUNGER_DEPL  = 10.0f; // at rest, how long does it take for hunger points to deplete?
	public static final float BASE_ENERGY_REPL  = 1.0f;  // without any effect from nutrition, how long does it take to get an energy point?
	public static final float PLAYER_WALK_SPEED = 32.0f;
	public static final float PLAYER_RUN_SPEED  = 128.0f;
	public static final float PLAYER_WIDTH      = 16.0f;
	public static final float PLAYER_HEIGHT     = 32.0f;
	
	private int                          hunger;
	private int                          energy;
	private float                        energyRepletionTime;
	private float                        hungerDepletionTime;
	
	private TItemStackCollection         inventory;
	private TItemStack                   equipped;
	
	private TInventoryAndForgerInterface inventoryInterface;
	private TMinimapInterface            minimapInterface;
	private TPlayerStatisticsInterface   statisticsInterface;
	
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
		hunger              = PLAYER_MAX_HUNGER;
		energy              = PLAYER_MAX_ENERGY;
		hungerDepletionTime = REST_HUNGER_DEPL;
		energyRepletionTime = BASE_ENERGY_REPL;
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
	 * Get the players currently equipped {@link TItemStack}. Could be null.
	 */
	public TItemStack getEquipped() {
		return equipped;
	}
	
	/**
	 * Sets the players current equipped {@link TItemStack}. Could be null.
	 */
	public void setEquipped(TItemStack stack) {
		equipped = stack;
	}
	
	/**
	 * Attempts to place a {@link TItem} in the players inventory, returns false if the action cannot be completed.
	 */
	public boolean giveItem(TItem item) {
		boolean ret = inventory.addItem(item);
		inventoryInterface.forgerShouldSync();
		return ret;
	}
	
	/**
	 * Returns whether or not the player can forge a given item based on the current items in their inventory.
	 */
	public boolean canForgeItem(TItem item) {
		if(item == null)
			return false;
		if(item.is().getRecipe().length == 0)
			return true;
		for(TItemSpec i : item.is().getRecipe())
			if(!has(i))
				return false;
		return true;
	}
	
	/**
	 * Takes {@link TItemSpec}s needed to create the given item. This calls the canForgeItem() once more as a safeguard to make sure that the player indeed can forge the given
	 * {@link TItem}.
	 */
	public void forge(TItem item) {
		if(!canForgeItem(item))
			return;
		for(TItemSpec i : item.is().getRecipe())
			take(i);
		giveItem(item);
	}
	
	/**
	 * Returns whether or not the player's inventory currently contains enough items to satisfy a given {@link TItemSpec}.
	 */
	public boolean has(TItemSpec itemSpec) {
		int needs = itemSpec.amount;
		for(int i = 0; i < getInventory().getItemStackCapacity(); i++) {
			final TItemStack stack = getInventory().getCollection()[i];
			if(stack != null) {
				if(stack.getItem() == itemSpec.item) {
					needs -= stack.getAmount();
					if(needs <= 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Called internally whilst forging an item. Only called when it is factually known that the player contains the proper items in their inventory to satisfy a given {@link TItemSpec}.
	 */
	private boolean take(TItemSpec itemSpec) {
		int needs = itemSpec.amount;
		for(int i = 0; i < getInventory().getItemStackCapacity(); i++) {
			final TItemStack stack = getInventory().getCollection()[i];
			if(stack != null) {
				if(stack.getItem() == itemSpec.item) {
					int has = stack.getAmount();
					int diff = has - needs;
					if(diff <= 0) {
						needs -= has;
						stack.setAmount(0);
						if(diff == 0)
							return true;
					} else {
						stack.setAmount(stack.getAmount() - needs);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void spawn() {
		// give the abstract inventory.
		inventory = new TItemStackCollection(12);
		
		// What items does the player have at spawn?
		for(int i = 0; i < 256; i++)
			inventory.addItem(TItem.HEALTHY_WOOD);
		
		// give a way to see and interact with the inventory.
		inventoryInterface = new TInventoryAndForgerInterface(this, true);
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
	
	float hungerTime = 0.0f;
	float energyTime = 0.0f;
	boolean f = true;

	@Override
	public void task(float dt) {
		super.task(dt);
		focusCamera();
		// you cannot open or close the Forger while a drag is going on because that can cause an item dupe glitch.
		if(Gdx.input.isKeyJustPressed(Keys.F) && !TInventoryAndForgerInterface.dragMutex()) {
			TUserInterface.removeContainer(inventoryInterface);
			f = !f;
			inventoryInterface.dispose();
			inventoryInterface = new TInventoryAndForgerInterface(this, f);
			inventoryInterface.init();
			TUserInterface.addContainer(inventoryInterface);
		}
		// get hungry =3.
		hungerTime += dt;
		if(hungerTime > hungerDepletionTime) {
			takeHungerPoints(1);
			if(hunger == 0) {
				// EAT!
				hurt(5);
			}
			hungerTime = 0.0f;
		}
		energyRepletionTime = BASE_ENERGY_REPL - ((BASE_ENERGY_REPL / 1.25f) * ((float)hunger / PLAYER_MAX_HUNGER));
		if(equipped != null) {
			if(equipped.getItem().is().getFunction() == TItemFunction.STRUCTURE) {
				// Render a square based on the the tile the player is looking at.
				final TRect rect   = new TRect(0, 0, TTerrain.TILE_WIDTH, TTerrain.TILE_HEIGHT);
				final TCircle circ = new TCircle(0, 0, TTerrain.TILE_WIDTH * 4);
				final Vector2 tsm  = TMath.translateScreenToTileCoordinates(Gdx.input.getX(), Gdx.input.getY());
				int tx             = (int)tsm.x;
				int ty             = (int)tsm.y;
				rect.setFilled(false);
				circ.setFilled(false);
				rect.setCX(tx * TTerrain.TILE_WIDTH);
				rect.setCY(ty * TTerrain.TILE_HEIGHT);
				circ.setCX(getActualX());
				circ.setCY(getActualY());
				TGraphics.draw(rect, false);
				TGraphics.draw(circ, false);
			}
		}
	}
	
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
		energyTime %= energyRepletionTime * 2;
		if(!actuallyMoving) {
			if(energyTime > energyRepletionTime) {
				giveEnergyPoints(((float)hunger / PLAYER_MAX_HUNGER) > 0.25f ? 4 : 1);
				energyTime = 0.0f;
			}
			hungerDepletionTime = REST_HUNGER_DEPL;
		} else {
			hungerDepletionTime = REST_HUNGER_DEPL / 2;
			if(TInput.run && energy >= 3) {
				hungerDepletionTime = REST_HUNGER_DEPL / 4;
				setMoveSpeed((TInput.slide) ? (PLAYER_RUN_SPEED / 2f) : PLAYER_RUN_SPEED);
				if(energyTime > 0.5f && actuallyMoving) {
					takeEnergyPoints(3);
					energyTime = 0.0f;
				}
			} else {
				setMoveSpeed((TInput.slide) ? (PLAYER_WALK_SPEED / 2f) : PLAYER_WALK_SPEED);
				if(energyTime > energyRepletionTime) {
					giveEnergyPoints(1);
					energyTime = 0.0f;
				}
			}
		}

	}
	
	@Override
	public boolean requestAttack() {
		// You cannot to request an attack or task if you have no energy.
		if(energy < 1)
			return false;
		if(equipped != null) {
			if(TInput.attack) {
				if(equipped.getItem().is().use(this)) {
					requestAttackAnimation();
					equipped.dec();
					if(equipped.isEmpty())
						equipped = null;
				}
				TInput.attack = false;
			}
			return false;
		}
		if(TInput.attack) {
			TInput.attack = false;
			takeEnergyPoints(1);			
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
