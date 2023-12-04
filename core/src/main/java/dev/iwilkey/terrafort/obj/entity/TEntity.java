package dev.iwilkey.terrafort.obj.entity;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.anim.TAnimationController;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * An abstract {@link TObject} with animations and monitored health, tasks, and death functionality.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TEntity extends TObject {
	
	private boolean                alive;
	private int                    maxHP;
	private int                    currentHP;
	protected TAnimationController animationController;
	
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
		animationController = new TAnimationController(this);
		initAnimations(animationController);
		spawn();
	}
	
	/**
	 * Called at construction. Utility to include animation controller to add animations.
	 */
	public abstract void initAnimations(TAnimationController anim);
	
	/**
	 * Called right as the entity becomes active in the world.
	 */
	public abstract void spawn();
	
	/**
	 * Called during the entities life.
	 */
	public abstract void task(float dt);
	
	/**
	 * Called right as the entity dies.
	 */
	public abstract void die();
	
	/**
	 * Called every engine frame that the entity is alive.
	 */
	public void tick(float dt) {
		animationController.tick(dt);
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
	
	/**
	 * Deal a given amount of damage to the entity.
	 */
	public void hurt(int amt) {
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
		if(currentHP + amt > maxHP) {
			currentHP = maxHP;
			return;
		}
		currentHP += amt;
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
	
	/**
	 * Returns the entity's current health as a percentage of its maximum health.
	 */
	public float getHealthPercentage() {
		return ((float)currentHP / maxHP) * 100.0f;
	}

}
