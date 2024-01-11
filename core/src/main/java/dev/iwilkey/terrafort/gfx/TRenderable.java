package dev.iwilkey.terrafort.gfx;

/**
 * A generic structure that references an object that needs to be rendered graphically.
 * @author Ian Wilkey (iwilkey)
 */
public final class TRenderable {
	
	/**
	 * The type of renderer this {@link TRenderable} requires.
	 * @author Ian Wilkey (iwilkey)
	 */
	public enum TRendererType {
		SPRITE,
		SHAPE
	}
	
	/**
	 * What renderer does this renderable require to be drawn to the screen?
	 */
	public TRendererType type   = null;
	
	/**
	 * The renderable's sprite data (Sprite only!)
	 */
	public TRenderableSprite sprite = null;
	
	/**
	 * The renderable's shape data (Geometry only!)
	 */
	public TRenderableShape shape  = null;
	
	/**
	 * A sprite renderable.
	 */
	public TRenderable(TRenderableSprite sprite) {
		this.sprite = sprite;
		type        = TRendererType.SPRITE;
	}
	
	/**
	 * A shape renderable.
	 */
	public TRenderable(TRenderableShape shape) {
		this.shape = shape;
		type       = TRendererType.SHAPE;
	}
	
	/**
	 * The depth level of this {@link TRenderable}.
	 */
	public int getDepth() {
		return ((sprite != null) ? sprite.getDepth() : shape.getDepth());
	}
	
}
