package dev.iwilkey.terrafort.obj.entity;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.shape.TRect;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A more specific, yet still abstract {@link TEntity}. Has special properties and functions that all Terrafort 
 * animals share. Ignores lighting.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TAnimal extends TEntity {
	
	public static final Color HEALTH_BAR_COLOR = new Color().set(0x00ff0000);
	public static final float HURT_HEAL_ANIMATION_TIMER = 0.1f;
	public static final float HEALTH_BAR_MAX_WIDTH = 8f;
	public static final float HEALTH_BAR_HEIGHT = 0.75f;
	
	private boolean           renderHealthBar;
	private float             healthBarXOffset;
	private float             healthBarYOffset;
	private TRect             healthBarOutline;
	private TRect             healthBar;
	private float             hurtTimer;
	private float             healTimer;
	
	public TAnimal(TWorld  world, 
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
		renderHealthBar  = true;
		healthBarXOffset = 0f;
		healthBarYOffset = 0f;
		healthBarOutline = new TRect(0f, 0f, HEALTH_BAR_MAX_WIDTH, HEALTH_BAR_HEIGHT);
		healthBar        = new TRect(0f, 0f, HEALTH_BAR_MAX_WIDTH, HEALTH_BAR_HEIGHT - 0.1f);
		hurtTimer        = HURT_HEAL_ANIMATION_TIMER;
		healTimer        = HURT_HEAL_ANIMATION_TIMER;
		healthBarOutline.setFilled(false);
	}
	
	public final void setHealthBarOffset(float xOff, float yOff) {
		healthBarXOffset = xOff;
		healthBarYOffset = yOff;
	}
	
	public final void shouldRenderHealthBar(boolean healthBar) {
		renderHealthBar = healthBar;
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
		if(renderHealthBar) {
			// calculate width and color based on how much health entity has.
			float p = getHealthPercentage() / 100f;
			float w = p * HEALTH_BAR_MAX_WIDTH;
			if(p > 0.5f) {
				healthBarOutline.setColor(HEALTH_BAR_COLOR.cpy().mul(0.8f));
				healthBar.setColor(HEALTH_BAR_COLOR);
			} else if(p > 0.25f) {
				healthBarOutline.setColor(Color.YELLOW.cpy().mul(0.8f));
				healthBar.setColor(Color.YELLOW);
			} else {
				healthBarOutline.setColor(Color.RED.cpy().mul(0.8f));
				healthBar.setColor(Color.RED);
			}
			healthBarOutline.setCX(getRenderX() + healthBarXOffset);
			healthBarOutline.setCY(getRenderY() + healthBarYOffset);
			healthBar.setCX(getRenderX() + healthBarXOffset);
			healthBar.setCY(getRenderY() + healthBarYOffset);
			healthBar.setWidth(w);
			// TGraphics.draw(healthBar);
			// TGraphics.draw(healthBarOutline);
		}
	}
}
