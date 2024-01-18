package dev.iwilkey.terrafort.gui.container;

import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.TUserInterface;

/**
 * A {@link TContainer} that exists in anchored screen-space.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TStaticContainer extends TContainer {
	
	private TAnchor anchor            = TAnchor.CENTER_CENTER;
	private int     externalPadTop    = 0;
	private int     externalPadBottom = 0;
	private int     externalPadRight  = 0;
	private int     externalPadLeft   = 0;
	
	/**
	 * Initiates a new static container. Will not be added to the scene until explicity done so with {@link TUserInterface}{@code .mAllocContainer(this)}.
	 */
	public TStaticContainer(Object... objReference) {
		super(objReference);
	}
	
	/**
	 * Sets the external padding of the container, used during the anchor calculation. This external padding 
	 * affects the container's position on the screen, providing a buffer space between the container and 
	 * other UI elements or screen edges.
	 *
	 * @param top The size of the top padding.
	 * @param bottom The size of the bottom padding.
	 * @param right The size of the right padding.
	 * @param left The size of the left padding.
	 */
	public final void setExternalPadding(final int top, final int bottom, final int right, final int left) {
		externalPadTop    = top;
		externalPadBottom = bottom;
		externalPadRight  = right;
		externalPadLeft   = left;
	}
	
	/**
	 * Sets the anchor of the TStaticContainer, positioning it in relative screen space.
	 * 
	 * @param anchor the anchor.
	 */
	public final void setAnchor(TAnchor anchor) {
		this.anchor = anchor;
	}
	
	/**
	 * Ensures the container maintains its anchored position according to the desired UI design.
	 */
	public final void anchor() {
	    int screenWidth  = Gdx.graphics.getWidth();
	    int screenHeight = Gdx.graphics.getHeight();
	    int padX         = externalPadLeft - externalPadRight;
	    int padY         = -externalPadTop + externalPadBottom;
	    float x, y;
	    switch(anchor) {
	        case TOP_RIGHT:
	            x = screenWidth - get().getWidth();
	            y = screenHeight - get().getHeight();
	            break;
	        case TOP_CENTER:
	            x = (screenWidth - get().getWidth()) / 2;
	            y = screenHeight - get().getHeight();
	            break;
	        case TOP_LEFT:
	            x = 0;
	            y = screenHeight - get().getHeight();
	            break;
	        case CENTER_RIGHT:
	            x = screenWidth - get().getWidth();
	            y = (screenHeight - get().getHeight()) / 2;
	            break;
	        case CENTER_CENTER:
	            x = ((screenWidth - get().getWidth()) / 2);
	            y = ((screenHeight - get().getHeight()) / 2);
	            break;
	        case CENTER_LEFT:
	            x = 0;
	            y = (screenHeight - get().getHeight()) / 2;
	            break;
	        case BOTTOM_RIGHT:
	            x = screenWidth - get().getWidth();
	            y = 0;
	            break;
	        case BOTTOM_CENTER:
	            x = (screenWidth - get().getWidth()) / 2;
	            y = 0;
	            break;
	        case BOTTOM_LEFT:
	            x = 0;
	            y = 0;
	            break;
	        default:
	            throw new IllegalStateException("Unexpected TAnchor in TContainer: " + hashCode());
	    }
	    get().setPosition(x + padX, y + padY);
	}
	
}
