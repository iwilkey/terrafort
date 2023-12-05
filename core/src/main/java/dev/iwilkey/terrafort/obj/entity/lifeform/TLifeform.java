package dev.iwilkey.terrafort.obj.entity.lifeform;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.obj.entity.TEntity;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A more specific, yet still abstract {@link TEntity}. Has special properties and functions that all Terrafort 
 * intelligent beings share. Ignores lighting.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TLifeform extends TEntity {

	public static final float HURT_HEAL_ANIMATION_TIMER = 0.1f;

	private float             hurtTimer;
	private float             healTimer;
	
	public TLifeform(TWorld  world, 
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
			       int     maxHP) {
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
			  renderTint, 
			  maxHP);
		hurtTimer        = HURT_HEAL_ANIMATION_TIMER;
		healTimer        = HURT_HEAL_ANIMATION_TIMER;
	}
	
	@Override
	public boolean shouldUseAdditiveBlending() {
		if(healTimer < HURT_HEAL_ANIMATION_TIMER)
			return true;
		return false;
	}
	
	@Override
	public void hurt(int amt) {
		super.hurt(amt);
		hurtTimer = 0.0f;
	}
	
	@Override
	public void heal(int amt) {
		int ab = getCurrentHP();
		super.heal(amt);
		if(ab != getCurrentHP())
			healTimer = 0.0f;
	}
	
	@Override
	public void task(float dt) {
		if(hurtTimer < HURT_HEAL_ANIMATION_TIMER) {
			setRenderTint(Color.RED);
			hurtTimer += dt;
		} else if(healTimer < HURT_HEAL_ANIMATION_TIMER) {
			healTimer += dt;
		} else {
			setRenderTint(Color.WHITE);
			hurtTimer = HURT_HEAL_ANIMATION_TIMER;
		}
	}
}
