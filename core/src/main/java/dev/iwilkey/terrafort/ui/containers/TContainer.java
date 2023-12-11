package dev.iwilkey.terrafort.ui.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.ui.TAnchor;

/**
 * Represents a specialized UI container that provides a static, anchored space for holding various widgets.
 * This abstract class extends the functionality of a standard container by introducing anchoring capabilities,
 * ensuring it stays in a fixed position. It utilizes a VisWindow for the actual container functionality, 
 * while abstracting away some specifics to provide a cleaner interface for a statically positioned UI element.
 *
 * <p>Padding can be controlled both internally and externally. Internal padding affects the spacing 
 * within the {@code VisWindow}, between the content and the edges. External padding is used when 
 * calculating the anchor position of the container, influencing its position relative to the edges 
 * of the screen or another container.
 * </p>
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TContainer implements Disposable {

	private VisWindow window            = null;
	private TAnchor   anchor            = TAnchor.CENTER_CENTER;
	private int       externalPadTop    = 0;
	private int       externalPadBottom = 0;
	private int       externalPadRight  = 0;
	private int       externalPadLeft   = 0;
	
	public TContainer() {
		window = new VisWindow(Integer.toString(hashCode()), false);
		window.setMovable(false);
		window.getTitleLabel().remove();
		window.getTitleTable().remove();
		setInternalPadding(4, 4, 8, 8);
		anchor();
	}
	
	/**
	 * Must be called to pack for the first time.
	 */
	public void init() {
		pack(window);
		window.pack();
	}
	
	/**
	 * Abstract method that is called when a new {@code TContainer} is created. Subclasses must implement this 
	 * method to add widgets to the {@code VisWindow} that constitutes the container.
	 *
	 *<p>
	 *NOTE: {@code window.pack()} is called outside of this function, so do not call it.
	 *</p>
	 *
	 * @param window The {@code VisWindow} instance associated with this container, to which widgets should be added.
	 */
	 public abstract void pack(final VisWindow window);
	 
	 /**
	 * Called every frame to update the state of the UI container and its widgets.
	 * This method is ideal for handling dynamic content that changes over time,
	 * such as displaying real-time statistics, player health, or interactive elements.
	 * Implementations should override this method to provide specific update logic 
	 * for the individual container's contents.
	 *
	 * <p>Example use cases might include:
	 * <ul>
	 *     <li>Updating a health bar UI element to reflect the player's current health.</li>
	 *     <li>Changing the text of a status display based on the player's in-game actions.</li>
	 *     <li>Altering the visibility or availability of UI elements based on game state.</li>
	 * </ul>
	 * </p>
	 *
	 * Note: This method is called frequently (typically once per frame), 
	 * and thus should be optimized for performance, especially if the 
	 * UI container contains a large number of widgets or is processing 
	 * complex logic.
	 */
	public abstract void update();
	
	/**
	 * Retrieves the underlying {@code VisWindow} instance that holds all the widgets added to this container.
	 *
	 * @return The {@code VisWindow} instance used by this container.
	 */
	public final VisWindow get() {
		return window;
	}
	
	/**
	 * Sets the internal padding of the container, which dictates the spacing between the container's 
	 * edges and its content. This method adjusts the padding on all sides of the container.
	 *
	 * @param top The size of the top padding.
	 * @param bottom The size of the bottom padding.
	 * @param right The size of the right padding.
	 * @param left The size of the left padding.
	 */
	public final void setInternalPadding(final int top, final int bottom, final int right, final int left) {
		window.padTop(top);
		window.padBottom(bottom);
		window.padRight(right);
		window.padLeft(left);
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
	 * Sets the anchor of the TContainer, positioning it in screen space.
	 * 
	 * @param anchor the anchor.
	 */
	public final void setAnchor(final TAnchor anchor) {
		this.anchor = anchor;
		anchor();
	}
	
	/**
	 * Method inherited from the {@code Anchorable} interface, intended to reposition the container when 
	 * the screen size changes. The implementation should ensure the container maintains its anchored 
	 * position according to the desired UI design.
	 */
	public final void anchor() {
	    int screenWidth = Gdx.graphics.getWidth();
	    int screenHeight = Gdx.graphics.getHeight();
	    float x, y;
	    switch(anchor) {
	        case TOP_RIGHT:
	            x = screenWidth - window.getWidth() - externalPadRight;
	            y = screenHeight - window.getHeight() - externalPadTop;
	            break;
	        case TOP_CENTER:
	            x = (screenWidth - window.getWidth()) / 2;
	            y = screenHeight - window.getHeight() - externalPadTop;
	            break;
	        case TOP_LEFT:
	            x = externalPadLeft;
	            y = screenHeight - window.getHeight() - externalPadTop;
	            break;
	        case CENTER_RIGHT:
	            x = screenWidth - window.getWidth() - externalPadRight;
	            y = (screenHeight - window.getHeight()) / 2;
	            break;
	        case CENTER_CENTER:
	            x = (screenWidth - window.getWidth()) / 2;
	            y = (screenHeight - window.getHeight()) / 2;
	            break;
	        case CENTER_LEFT:
	            x = externalPadLeft;
	            y = (screenHeight - window.getHeight()) / 2;
	            break;
	        case BOTTOM_RIGHT:
	            x = screenWidth - window.getWidth() - externalPadRight;
	            y = externalPadBottom;
	            break;
	        case BOTTOM_CENTER:
	            x = (screenWidth - window.getWidth()) / 2;
	            y = externalPadBottom;
	            break;
	        case BOTTOM_LEFT:
	            x = externalPadLeft;
	            y = externalPadBottom;
	            break;
	        default:
	            throw new IllegalStateException("Unexpected TAnchor in TContainer: " + hashCode());
	    }
	    window.setPosition(x, y);
	}
	
	/**
	 * Clears and repacks the {@code TContainer}. Typically called when a global resize or scale occurs.
	 * Do not call this often, as it can be performance intensive.
	 */
	public void reset() {
		window.clear();
		pack(window);
	}
	
	@Override
	public final void dispose() {
		window.clear();
		window.remove();
		window = null;
	}
	
}
