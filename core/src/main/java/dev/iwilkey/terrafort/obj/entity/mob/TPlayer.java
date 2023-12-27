package dev.iwilkey.terrafort.obj.entity.mob;

import java.util.concurrent.ThreadLocalRandom;

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
import dev.iwilkey.terrafort.item.TItemStack;
import dev.iwilkey.terrafort.item.TItemStackCollection;
import dev.iwilkey.terrafort.math.TInterpolator;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.obj.particulate.TParticle;
import dev.iwilkey.terrafort.obj.world.TBuilding;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.THUDInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TMinimapInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TShopInterface;

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
	
	private TInterpolator                currency;
	private int                          hunger;
	private int                          energy;
	private float                        energyRepletionTime;
	private float                        hungerDepletionTime;
	
	private TItemStackCollection         inventory;
	private TItemStack                   equipped;
	private THUDInterface                inventoryInterface;
	private TShopInterface               shopInterface;
	private TMinimapInterface            minimapInterface;
	
	public TPlayer(TWorld world) {
		super(world,
			  true,
			  ThreadLocalRandom.current().nextInt(-65536, 65536),
			  ThreadLocalRandom.current().nextInt(-65536, 65536),
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
		TGraphics.forceCameraPosition(getActualX(), getActualY());
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
	 * Gives the {@link TPlayer} currency, provided in cents.
	 * @param amtInCents
	 */
	public void giveCurrency(long amtInCents) {
		currency.set(currency.getTarget() + amtInCents);
		if(f) shopInterface.sync();
	}
	
	/**
	 * Requests to take some currency (in cents) from the {@link TPlayer}. If the player doesn't have enough, nothing will happen and the function
	 * will return false.
	 */
	public boolean takeCurrency(long amtInCents) {
		if(currency.getTarget() - amtInCents < 0)
			return false;
		currency.set(currency.getTarget() - amtInCents);
		if(f) shopInterface.sync();
		return true;
	}
	
	/**
	 * Returns the current value of the player.
	 */
	public long getNetWorth() {
		return (long)currency.getTarget();
	}
	
	/**
	 * Returns a formatted string that represents the player's current currency, ready to be rendered.
	 * Keep in note that the instantaneous value returned may still be in the process of interpolation
	 * and should not be trusted as the actual current worth of the player.
	 * 
	 * <p>
	 * To get the actual value of the player, use {@link TPlayer}.getNetWorth().
	 * </p>
	 * @return
	 */
	public String currencyRenderString() {
		long   vos  = (long)currency.get();
		long   d    = vos / 100;
		long   c    = vos % 100;
		String rend = String.format("$%,d.%02d", d, c);
		return rend;
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
		// try to add to equipped item stack...
		if(equipped != null) 
			if(equipped.getItem() == item) 
				if(equipped.inc())
					return true;
		// otherwise, try to add to the inventory.
		boolean ret = inventory.addItem(item);
		return ret;
	}

	@Override
	public void spawn() {
		// give the abstract inventory.
		inventory = new TItemStackCollection(5);
		currency  = new TInterpolator(0);
		// give a way to see and interact with the inventory.
		inventoryInterface = new THUDInterface(this);
		inventoryInterface.init();
		TUserInterface.mallocon(inventoryInterface);
		// give the minimap utility.
		minimapInterface = new TMinimapInterface(this);
		minimapInterface.init();
		TUserInterface.mallocon(minimapInterface);
		shopInterface = new TShopInterface(this);
		shopInterface.init();
	}
	
	float   ht = 0.0f;
	float   et = 0.0f;
	boolean f  = false;

	@Override
	public void task(float dt) {
		super.task(dt);
		focusCamera();
		currency.update();
		// check for items to pick up...
		final Array<TObject> manifold = getCollisionManifold();
		for(final TObject o : manifold)
			if(o instanceof TItemDrop)
				((TItemDrop)o).transferTo(this);
		// you cannot open or close the shop while a drag is going on because that can cause an item dupe glitch.
		if(Gdx.input.isKeyJustPressed(Keys.F) && !THUDInterface.dragMutex()) {
			f = !f;
			if(f) TUserInterface.mallocon(shopInterface);
			else TUserInterface.freecon(shopInterface);
		}
		// get hungry =3.
		ht += dt;
		if(ht > hungerDepletionTime) {
			takeHungerPoints(1);
			if(hunger == 0) {
				// EAT!
				hurt(5);
			}
			ht = 0.0f;
		}
		energyRepletionTime = BASE_ENERGY_REPL - ((BASE_ENERGY_REPL / 1.25f) * ((float)hunger / PLAYER_MAX_HUNGER));
		if(equipped != null) {
			if(equipped.getItem().is().getFunction() == TItemFunction.FORT) {
				// Render a square based on the the tile the player is looking at.
				final TRect   rect = new TRect(0, 0, TTerrain.TILE_WIDTH, TTerrain.TILE_HEIGHT);
				final TCircle circ = new TCircle(0, 0, TTerrain.TILE_WIDTH * 4.1f);
				final Vector2 tsm  = TMath.translateScreenToTileCoordinates(Gdx.input.getX(), Gdx.input.getY());
				int tx             = (int)tsm.x;
				int ty             = (int)tsm.y;
				int ax             = tx * TTerrain.TILE_WIDTH;
				int ay             = ty * TTerrain.TILE_HEIGHT;
				rect.setFilled(false);
				circ.setFilled(false);
				rect.setCX(ax);
				rect.setCY(ay);
				circ.setCX(getActualX());
				circ.setCY(getActualY());
				if(TBuilding.canPlaceAt(this, TBuilding.getTypeOfFortFunction(equipped.getItem()), TBuilding.cursorTileSelection(this))) {
					TGraphics.draw(equipped.getItem().is().getIcon(), ax, ay, ax, ay, 0, TTerrain.TILE_WIDTH, TTerrain.TILE_HEIGHT, new Color().set(0xffffff99), false);
					TGraphics.draw(rect, false);
					circ.setColor(new Color().set(0xffffffff));
				} else circ.setColor(new Color().set(0x5D6855ff));
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
		et += TClock.dt();
		et %= energyRepletionTime * 2;
		if(!actuallyMoving) {
			if(et > energyRepletionTime) {
				giveEnergyPoints(((float)hunger / PLAYER_MAX_HUNGER) > 0.25f ? 4 : 1);
				et = 0.0f;
			}
			hungerDepletionTime = REST_HUNGER_DEPL;
		} else {
			hungerDepletionTime = REST_HUNGER_DEPL / 2;
			if(TInput.run && energy >= 3) {
				hungerDepletionTime = REST_HUNGER_DEPL / 4;
				setMoveSpeed((TInput.slide) ? (PLAYER_RUN_SPEED / 2f) : PLAYER_RUN_SPEED);
				if(et > 0.5f && actuallyMoving) {
					takeEnergyPoints(3);
					et = 0.0f;
				}
			} else {
				setMoveSpeed((TInput.slide) ? (PLAYER_WALK_SPEED / 2f) : PLAYER_WALK_SPEED);
				if(et > energyRepletionTime) {
					giveEnergyPoints(1);
					et = 0.0f;
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
		final TObject hit;
		hit = sense(8f, (float)Math.PI / 32f, 4);
		if(hit != null) {
			if(hit instanceof TEntity) {
				final TEntity e = (TEntity)hit;
				e.onInteraction(this);
			}
		}
	}
	
	@Override
	public void onInteraction(TMob interactee) {
		if(interactee instanceof TBandit) 
			hurt(ThreadLocalRandom.current().nextInt(1, 16));	
	}
	
	@Override
	protected void calculateFacingDirection() {
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
		for(int i = 0; i < 128; i++)
			world.addObject(new TParticle(world, getActualX(), getActualY(), Color.RED.cpy()));
		TUserInterface.freecon(inventoryInterface);
		TUserInterface.freecon(minimapInterface);
		TUserInterface.freecon(shopInterface);
		TGraphics.fadeOut(0.1f);
	}
	
	public void resize(int nw, int nh) {
		TUserInterface.freecon(shopInterface);
		if(f) TUserInterface.mallocon(shopInterface);
		inventoryInterface.resize(nw, nh);
	}

}
