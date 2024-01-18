package dev.iwilkey.terrafort.gui.text;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.obj.type.TMob;

/**
 * A text particle that begins above a {@link TMob}'s head and continues up until it fades. Great for asserting changes in a mob's state.
 * @author Ian Wilkey (iwilkey)
 */
public final class TMobTextParticle extends TImmediateModeTextParticle {
	
	/**
	 * The speed at which the particle ascends above the mob's head.
	 */
	public static final float Y_SPEED = 0.1f;
	
	/**
	 * The rendered color of the text. Alpha channel automatically changed.
	 */
	private final int col;
	
	/**
	 * The final rendered y in world-space.
	 */
	private float offY;

	/**
	 * Creates a new mob text particle with given data, owner, color, and whether or not to follow the
	 * mob's x coordinate during it's life.
	 */
	public TMobTextParticle(String text, TMob owner, int col) {
		super(text, 
			 ((int)owner.worldX) + ThreadLocalRandom.current().nextInt(-((int)owner.worldWidth / 8), ((int)owner.worldWidth / 8)), 
			 (int)(owner.worldY + (owner.worldHeight / 2f) + 8f), 
			 12, 
			 (float)ThreadLocalRandom.current().nextDouble(0.5, 2.0));
		this.col = col;
		offY     = y;
	}

	@Override
	public void behavior(float dt) {
		this.color = col;
		offY += Y_SPEED;
		y = Math.round(offY);
	}

	@Override
	public boolean inWorldSpace() {
		return true;
	}

}
