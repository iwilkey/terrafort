package dev.iwilkey.terrafort.obj.type;

import dev.iwilkey.terrafort.obj.runtime.TObjectRuntime;

/**
 * A dynamic physical object that manages it's own life.
 */
public abstract class TParticulate extends TObject {

	/**
	 * The unique serialization ID.
	 */
	private static final long serialVersionUID = 2813724865705656199L;
	
	/**
	 * How long is this particulate allowed to be active?
	 */
	public float lifespan;
	
	/**
	 * How long has this particulate been alive?
	 */
	public float aliveTime;
	
	/**
	 * Should this particulate fade as it ages?
	 */
	public boolean shouldFade;
	
	/**
	 * Is this particulate done?
	 */
	public boolean done;
	
	/**
	 * Creates a simple particulate at given world coordinates. It is considered "simple" because it's sprite
	 * is a simple white square that can be stretched and tinted.
	 */
	public TParticulate(float worldX, float worldY, float width, float height, int tint) {
		super();
		spriteSheet       = "sheets/items-icons.png";
		this.worldX       = worldX;
		this.worldY       = worldY;
		colliderOffX      = 0;
		colliderOffY      = 0;
		worldWidth        = width;
		worldHeight       = height;
		colliderWidth     = width / 2f;
		colliderHeight    = height / 2f;
		dataX             = 0;
		dataY             = 1;
		dataWidth         = 1;
		dataHeight        = 1;
		this.tint         = tint;
		depth             = 129;
		definesOwnPhysics = true;
		isDynamic         = true;
		isSensor          = true;
		aliveTime         = 0.0f;
		done              = false;
	}
	
	@Override
	public void tick(final TObjectRuntime concrete, float dt) {
		super.tick(concrete, dt);
		aliveTime += dt;
		if(shouldFade) {
			float ap = Math.max(0.001f, 1.0f - getAge());
			int   a  = Math.round(ap * 0xff);
			tint     &= 0xffffff00;
			tint     |= a;
		}
		done = aliveTime >= lifespan;
		if(!done)
			behavior(concrete, dt);
	}
	
	/**
	 * Called during the {@link TParticulate}s life.
	 */
	public abstract void behavior(final TObjectRuntime concrete, float dt);
	
	/**
	 * Returns a percentage [0, 1] that represents the age of the {@link TParticulate}. 1 indicates
	 * it is the oldest it can be.
	 */
	public final float getAge() {
		return aliveTime / lifespan;
	}
	
	/**
	 * Set when a {@link TParticulate} has lived its full life and should be removed from the world.
	 */
	public final boolean isDone() {
		return done;
	}

}
