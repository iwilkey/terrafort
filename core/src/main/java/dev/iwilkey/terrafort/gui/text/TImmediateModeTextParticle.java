package dev.iwilkey.terrafort.gui.text;

/**
 * A text particle, rendered in world space. Fades over time. Follows strictly-defined physical
 * behavior.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TImmediateModeTextParticle implements TImmediateModeText {
	
	private final String text;
	private final float  lifetime;
	
	protected int x;
	protected int y;
	protected int point;
	protected int color;
	
	
	private float aliveTime;
	
	/**
	 * Creates a new text particle with given data and initial position, size, and lifetime.
	 */
	public TImmediateModeTextParticle(String text, int ix, int iy, int point, float lifetime) {
		this.text     = text;
		this.point    = point;
		this.lifetime = lifetime;
		x             = ix;
		y             = iy;
		color         = 0xffffffff;
		aliveTime     = 0.0f;
	}
	
	/**
	 * Called every tick. Should define the text particle's physical behavior.
	 */
	public abstract void behavior(float dt);
	
	/**
	 * Returns whether or not this text particle is rendered in screen or world-space.
	 */
	public abstract boolean inWorldSpace();
	
	/**
	 * Updates the text particle.
	 */
	public final void tick(float dt) {
		if(done())
			return;
		aliveTime += dt;
		behavior(dt);
		// No matter the color, the particle should fade in alpha over time...
		color &= 0xffffff00;
		color |= Math.round(Math.max(0.001f, 1.0f - getAge()) * 0xff);
	}
	
	/**
	 * Returns if the text particle should be disregarded.
	 */
	public final boolean done() {
		return aliveTime >= lifetime;
	}
	
	/**
	 * Returns a percentage [0, 1] that represents the age of the text particle. 1 indicates
	 * it is the oldest it can be.
	 */
	public final float getAge() {
		return aliveTime / lifetime;
	}
	
	@Override
	public String getData() {
		return text;
	}

	@Override
	public boolean worldSpace() {
		return inWorldSpace();
	}

	@Override
	public boolean dropShadow() {
		return true;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getColor() {
		return color;
	}

	@Override
	public int getPoint() {
		return point;
	}

	@Override
	public int getWrapping() {
		return 0;
	}

}
