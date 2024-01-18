package dev.iwilkey.terrafort.gui.text;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import dev.iwilkey.terrafort.gui.TAnchor;

/**
 * A text particle rendered in screen-space with strict behavior. Anchored to some location.
 * @author Ian Wilkey (iwilkey)
 */
public final class TScreenTextParticle extends TImmediateModeTextParticle {
	
	/**
	 * The speed at which the particle ascends above (or below) the anchored location in screen-space.
	 */
	public static final float Y_SPEED = 1f;
	
	private final TAnchor anchor;
	
	private float bx = -1000;
	private float by = -1000;
	private float offX;
	private float offY;
	
	/**
	 * Creates a new screen text particle with given anchor and offset from that anchor, font size, and color.
	 */
	public TScreenTextParticle(String text, TAnchor anchor, int offX, int offY, int point, int col) {
		super(text, -1000, -1000, point, 3.0f);
		this.anchor = anchor;
		this.offX   = offX;
		this.offY   = offY;
		anchor();
		color = col;
	}
	
	/**
	 * Makes sure the base position of the screen text particle is correct.
	 */
	public void anchor() {
		int screenWidth  = Gdx.graphics.getWidth();
	    int screenHeight = Gdx.graphics.getHeight();
	    Vector2 dim      = getDimensions();
	    switch(anchor) {
	        case TOP_RIGHT:
	            bx = screenWidth - dim.x;
	            by = screenHeight - dim.y;
	            break;
	        case TOP_CENTER:
	            bx = (screenWidth - dim.x) / 2;
	            by = screenHeight - dim.y;
	            break;
	        case TOP_LEFT:
	            bx = 0;
	            by = screenHeight - dim.y;
	            break;
	        case CENTER_RIGHT:
	            bx = screenWidth - dim.x;
	            by = (screenHeight - dim.y) / 2;
	            break;
	        case CENTER_CENTER:
	            bx = ((screenWidth - dim.x) / 2);
	            by = ((screenHeight - dim.y) / 2);
	            break;
	        case CENTER_LEFT:
	            bx = 0;
	            by = (screenHeight - dim.y) / 2;
	            break;
	        case BOTTOM_RIGHT:
	            bx = screenWidth - dim.x;
	            by = 0;
	            break;
	        case BOTTOM_CENTER:
	            bx = (screenWidth - dim.y) / 2;
	            by = 0;
	            break;
	        case BOTTOM_LEFT:
	            bx = 0;
	            by = 0;
	            break;
	        default:
	            throw new IllegalStateException("Unexpected TAnchor in TContainer: " + hashCode());
	    }
	}
	
	@Override
	public int getAlignment() {
		switch(anchor) {
	        case TOP_RIGHT:
	            return Align.topRight;
	        case TOP_CENTER:
	            return Align.top;
	        case TOP_LEFT:
	            return Align.topLeft;
	        case CENTER_RIGHT:
	            return Align.right;
	        case CENTER_CENTER:
	        	return Align.center;
	        case CENTER_LEFT:
	            return Align.left;
	        case BOTTOM_RIGHT:
	            return Align.right;
	        case BOTTOM_CENTER:
	            return Align.center;
	        case BOTTOM_LEFT:
	            return Align.left;
	        default:
	            throw new IllegalStateException("Unexpected TAnchor in TContainer: " + hashCode());
		}
	}

	@Override
	public void behavior(float dt) {
		anchor();
		offY += Y_SPEED;
		y = Math.round(by + offY);
		x = Math.round(bx + offX);
	}

	@Override
	public boolean inWorldSpace() {
		return false;
	}

}
