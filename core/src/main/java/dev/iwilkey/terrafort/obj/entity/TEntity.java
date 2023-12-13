package dev.iwilkey.terrafort.obj.entity;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.gfx.anim.TAnimationController;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.mob.TMob;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * An abstract {@link TObject} with animations and monitored health, tasks, and death functionality.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TEntity extends TObject {
	
	public static final float      HURT_HEAL_ANIMATION_TIMER = 0.1f;
	
	protected TAnimationController animationController;
	
	private boolean                alive;
	private int                    maxHP;
	private int                    currentHP;
	private float                  hurtTimer;
	private float                  healTimer;
	
	public TEntity(TWorld   world, 
			       boolean isDynamic, 
			       float   x, 
			       float   y, 
			       int     z, 
			       float   width, 
			       float   height,
			       float   colliderWidth,
			       float   colliderHeight,
			       int     dataOffsetX,
			       int     dataOffsetY, 
			       int     dataSelectionSquareWidth, 
			       int     dataSelectionSquareHeight, 
			       Color   renderTint,
			       int maxHP) {
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
			  renderTint);
		this.maxHP          = maxHP;
		this.currentHP      = maxHP;
		alive               = true;
		hurtTimer           = HURT_HEAL_ANIMATION_TIMER;
		healTimer           = HURT_HEAL_ANIMATION_TIMER;
		animationController = new TAnimationController(this);
		initAnimations(animationController);
		spawn();
	}
	
	/**
	 * Called at construction. Utility to include animation controller to add animations.
	 */
	public void initAnimations(TAnimationController anim) {}
	
	/**
	 * Called right as the entity becomes active in the world.
	 */
	public abstract void spawn();
	
	/**
	 * Called during the entities life.
	 */
	public abstract void task(float dt);
	
	/**
	 * Called when a {@link TMob} chooses to interact with this {@link TEntity}.
	 * @param interactee the {@link TMob} interacting.
	 */
	public abstract void onInteraction(TMob interactee);
	
	/**
	 * Called right as the entity dies.
	 */
	public abstract void die();
	
	/**
	 * Called every engine frame that the entity is alive.
	 */
	public void tick(float dt) {
		animationController.tick(dt);
		// Animates the event of a {@link TEntity} getting hurt or healed...
		if(hurtTimer < HURT_HEAL_ANIMATION_TIMER) {
			setRenderTint(new Color().set(0xC41E3Aff));
			hurtTimer += dt;
		} else if(healTimer < HURT_HEAL_ANIMATION_TIMER) {
			healTimer += dt;
		} else {
			setRenderTint(Color.WHITE.cpy());
			hurtTimer = HURT_HEAL_ANIMATION_TIMER;
			healTimer = HURT_HEAL_ANIMATION_TIMER;
		}
		task(dt);
	}
	
	/**
	 * Called from the entity's {@link TAnimationController}.
	 */
	public void updateRenderingSprite(final TFrame frame) {
		dataOffsetX               = frame.getDataOffsetX();
		dataOffsetY               = frame.getDataOffsetY();
		dataSelectionSquareWidth  = frame.getDataSelectionWidth();
		dataSelectionSquareHeight = frame.getDataSelectionHeight();
	}
	
	@Override
	public boolean shouldUseAdditiveBlending() {
		if((healTimer < HURT_HEAL_ANIMATION_TIMER))
			return true;
		return false;
	}
	
	/**
	 * Deal a given amount of damage to the entity.
	 */
	public void hurt(int amt) {
		hurtTimer = 0.0f;
		if(currentHP - amt <= 0) {
			currentHP = 0;
			alive = false;
			return;
		}
		currentHP -= amt;
	}
	
	/**
	 * Gives the entity a given amount of health.
	 */
	public void heal(int amt) {
		int ab = getCurrentHP();
		if(currentHP + amt > maxHP) {
			currentHP = maxHP;
			return;
		}
		currentHP += amt;
		if(ab != getCurrentHP())
			healTimer = 0.0f;
	}
	
	public TAnimationController getAnimationController() {
		return animationController;
	}

	public boolean isAlive() {
		return alive;
	}
	
	public int getCurrentHP() {
		return currentHP;
	}
	
	public int getMaxHP() {
		return maxHP;
	}
	
	public int getCurrentTileX() {
		return Math.round(x / TTerrainRenderer.TERRAIN_TILE_WIDTH);
	}
	
	public int getCurrentTileY() {
		return Math.round(y / TTerrainRenderer.TERRAIN_TILE_HEIGHT);
	}
	
	/**
	 * Returns the entity's current health as a percentage of its maximum health.
	 */
	public float getHealthPercentage() {
		return ((float)currentHP / maxHP) * 100.0f;
	}

}
