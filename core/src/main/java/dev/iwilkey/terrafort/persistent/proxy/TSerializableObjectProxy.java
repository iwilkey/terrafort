package dev.iwilkey.terrafort.persistent.proxy;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.entity.mob.TBandit;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.entity.tile.TBuildingTile;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.obj.particulate.TParticulate;
import dev.iwilkey.terrafort.persistent.TSerializableProxy;

/**
 * Represents a valid {@link TObject} state.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSerializableObjectProxy extends TSerializableProxy {

	private static final long serialVersionUID = -7331049671235282987L;

	private final String abstraction;     // all
	private final float  x;			      // all
	private final float  y;               // all
	private final float  width;           // all
	private final float  height;          // all
	private final Color  renderColor;     // all
	private final float  hp;              // entities
	private final float  hunger;          // player
	private final float  energy;          // player
	private final float  powerMultiplier; // bandits
	private final TItem  item;            // building tile or itemDrop
	private final float  aliveTime;       // particulate
	
	public TSerializableObjectProxy(final TObject target) {
		super(target);
		// TObject info...
		abstraction = target.getClass().getSimpleName();
		x           = target.getActualX();
		y           = target.getActualY();
		width       = target.getRenderWidth();
		height      = target.getRenderHeight();
		renderColor = target.getRenderTint();
		// TEntity info...
		if(target instanceof TEntity) { 
			hp = ((TEntity)target).getCurrentHP();
		} else {
			hp = -1;
		}
		// TPlayer info...
		if(target instanceof TPlayer) {
			hunger = ((TPlayer)target).getHungerPoints();
			energy = ((TPlayer)target).getEnergyPoints();
		} else {
			hunger = -1;
			energy = -1;
		}
		// TBandit info...
		if(target instanceof TBandit) {
			powerMultiplier = ((TBandit)target).getPowerMultipler();
		} else {
			powerMultiplier = -1;
		}
		// TBuildingTile or TItemDrop info...
		if(target instanceof TBuildingTile) {
			item = ((TBuildingTile)target).getItem();
		} else if(target instanceof TItemDrop){
			item = ((TItemDrop)target).getItem();
		} else {
			item = null;
		}
		// TParticulate info...
		if(target instanceof TParticulate) {
			aliveTime = ((TParticulate)target).getAliveTime();
		} else {
			aliveTime = -1;
		}
	}

	public String getAbstraction() {
		return abstraction;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
	
	public Color getRenderColor() {
		return renderColor;
	}

	public float getHp() {
		return hp;
	}

	public float getHunger() {
		return hunger;
	}

	public float getEnergy() {
		return energy;
	}

	public float getPowerMultiplier() {
		return powerMultiplier;
	}

	public TItem getItem() {
		return item;
	}

	public float getAliveTime() {
		return aliveTime;
	}
	
}
