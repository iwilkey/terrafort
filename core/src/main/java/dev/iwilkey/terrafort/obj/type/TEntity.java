package dev.iwilkey.terrafort.obj.type;

import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;

/**
 * A more complex {@link TObject} with monitored health, tasks, and death functionality. This class serves as an abstract 
 * representation of the entity's state, rather than it's physical presence.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TEntity extends TObject {
	
	public transient static final float HURT_HEAL_ANIMATION_TIMER = 0.1f;
	public transient static final int   HURT_TINT                 = 0xc41e3aff;
	
	/**
	 * The unique serialization ID.
	 */
	private static final long serialVersionUID = -8577971047624033916L;
	
	/**
	 * The original specified tint of the entity. Returns after any hurt/heal animation.
	 */
	public int naturalTint;
	
	/**
	 * How many health points is this entity allowed to have?
	 */
	public int maxHealthPoints;
	
	/**
	 * What is the current health of this entity?
	 */
	public int currentHealthPoints;
	
	// internal timers. should still be serialized...
	private float hurtTimer = HURT_HEAL_ANIMATION_TIMER;
	private float healTimer = HURT_HEAL_ANIMATION_TIMER;
	
	@Override
	public void tick(final TObjectRuntime concrete, float dt) {
		super.tick(concrete, dt);
		task(concrete, dt);
		indicateHurtHeal(dt);
	}
	
	/**
	 * Called every frame that the entity is alive.
	 */
	public abstract void task(final TObjectRuntime concrete, float dt);

	
	/**
	 * Called the moment the engine recognizes the entity's health is zero.
	 */
	public abstract void death(final TObjectRuntime concrete);
	
	/**
	 * Deals a given amount of damage to the entity.
	 */
	public void hurt(int amount) {
		hurtTimer = 0.0f;
		if(currentHealthPoints - amount <= 0) {
			currentHealthPoints = 0;
			return;
		}
		currentHealthPoints -= amount;
	}
	
	/**
	 * Gives a given amount of health to the entity. Does not count as a heal if the health is already maxed out.
	 */
	public void heal(int amount) {
		// heal doesn't count...
		if(currentHealthPoints == maxHealthPoints)
			return;
		// heal counts...
		healTimer = 0.0f;
		if(currentHealthPoints + amount > maxHealthPoints) {
			currentHealthPoints = maxHealthPoints;
			return;
		}
		currentHealthPoints += amount;
	}
	
	/**
	 * Applies a graphical indication if the entity is currently being hurt or healed.
	 */
	private void indicateHurtHeal(float dt) {
		if(hurtTimer < HURT_HEAL_ANIMATION_TIMER) hurtTimer += dt;
		if(healTimer < HURT_HEAL_ANIMATION_TIMER) healTimer += dt;
		if(hurtTimer < HURT_HEAL_ANIMATION_TIMER) {
		    tint = HURT_TINT;
		    shouldUseAdditiveBlending = false;
		} else if(healTimer < HURT_HEAL_ANIMATION_TIMER) {
		    shouldUseAdditiveBlending = true;
		} else {
		    tint = naturalTint;
		    hurtTimer = healTimer = HURT_HEAL_ANIMATION_TIMER;
		    shouldUseAdditiveBlending = false;
		}
	}

}
