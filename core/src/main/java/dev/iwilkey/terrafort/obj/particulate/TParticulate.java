package dev.iwilkey.terrafort.obj.particulate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A light, interactive and dynamic physical object that manages it's own life. Fades overtime until it dies.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TParticulate extends TObject {
	
	private final float   lifespan;
	protected     boolean shouldFade;
	private       boolean done;
	private       float   aliveTime;
	
	/**
	 * Creates a new {@link TParticulate} at (x, y) [world-space!] with given life-span, in seconds. Note that life-span
	 * is final and cannot be changed after construction.
	 */
	public TParticulate(TWorld world, float x, float y, float width, float height, float lifespan) {
			super(world, 
				  true, 
				  x, 
				  y, 
				  3, 
				  width, 
				  height, 
				  width / 2, 
				  height / 2,
				  3, 
				  1,
				  1, 
				  1, 
				  Color.WHITE.cpy());		
			this.lifespan = lifespan;
			aliveTime     = 0.0f;
			done          = false;
			shouldFade    = true;
			// all {@link TParticulates}s shall be very light, unless overriden.
		    getPhysicalFixture().setDensity(0.01f);
			getPhysicalBody().resetMassData();
			setAsSensor();
	}
	
	/**
	 * Called during the {@link TParticulate}s life.
	 */
	public abstract void behavior(float dt);
	
	/**
	 * Applies a random addition to the {@link TParticulate}s width and height between [a, b).
	 */
	public final void randomDimensionalAddition(float a, float b) {
		this.width  += TMath.nextFloat(a, b);
		this.height += TMath.nextFloat(a, b);
	}
	
	/**
	 * Applies a random impulse force between given magnitude [a, b). Force will be centered around given direction,
	 * but will have a random angle offset applied to it.
	 */
	public final void randomDirectedImpulseForce(float a, float b, Vector2 direction, float spread) {
	    final float   angleSpread = TMath.nextFloat(-spread, spread);
	    final Vector2 dir         = new Vector2(direction).rotateDeg(angleSpread);
	    final float   magnitude   = TMath.nextFloat(a, b);
	    getPhysicalBody().applyLinearImpulse(dir.setLength(magnitude), getPhysicalBody().getWorldCenter(), true);
	}
	
	/**
	 * Applies a random impulse force between given magnitude [a, b). Force may be applied at any angle
	 * [0, 2 * pi] radians.
	 */
	public final void randomImpulseForce(float a, float b) {
	    final float   impulseMagnitude = TMath.nextFloat(a, b);
	    final float   angle            = TMath.nextFloat(0, (float)(2 * Math.PI));
	    final Vector2 impulse          = new Vector2((float)(impulseMagnitude * Math.cos(angle)), (float)(impulseMagnitude * Math.sin(angle)));
	    getPhysicalBody().applyLinearImpulse(impulse, getPhysicalBody().getWorldCenter(), true);
	}
	
	/**
	 * Applies a random value between [a, b) as the linear and angular damping constant of the
	 * {@link TParticulate}s physical body. Basically, it controls the time it takes for the
	 * body to come to rest. If damping is 0, the particle moves and spins forever until it hits 
	 * something physical.
	 */
	public final void randomLinearAndAngularDamping(float a, float b) {
		getPhysicalBody().setLinearDamping(TMath.nextFloat(a, b));
	    getPhysicalBody().setAngularDamping(TMath.nextFloat(a, b));
	}
	
	/**
	 * Applies a random alteration of the {@link TParticulate}s current render tint between [-amt, amt] for
	 * each color channel. Amt should be [0, 1] for best results. Leaves alpha alone.
	 */
	public final void randomColorAddition(float amt) {
		final float ramt = TMath.nextFloat(-amt, amt);
		final float gamt = TMath.nextFloat(-amt, amt);
		final float bamt = TMath.nextFloat(-amt, amt);
		renderTint.add(ramt, gamt, bamt, 0.0f);
	}
	
	/**
	 * Steps the {@link TParticulate} through another frame, aging it. Automatically fades
	 * sprite based on age by default.
	 */
	public final void age(float dt) {
		aliveTime += dt;
		if(shouldFade)
			renderTint.a = Math.max(0.001f, 1.0f - getAge());
		done = aliveTime >= lifespan;
		if(!done)
			behavior(dt);
	}
	
	/**
	 * Set when a {@link TParticulate} has lived its full life and should be removed from the world.
	 */
	public final boolean isDone() {
		return done;
	}
	
	/**
	 * Forces the {@link TParticulate} to end its life.
	 */
	public final void setDone() {
		aliveTime = lifespan;
		done      = true;
	}

	/**
	 * Returns a percentage [0, 1] that represents the age of the {@link TParticulate}. 1 indicates
	 * it is the oldest it can be.
	 */
	public final float getAge() {
		return aliveTime / lifespan;
	}
	
	public final float getAliveTime() {
		return aliveTime;
	}

}
