package dev.iwilkey.terrafort.gui.container;

import com.badlogic.gdx.utils.Disposable;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.gui.TUserInterface;

/**
 * A space to encapsulate and operate groups of UI widgets.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TContainer implements Disposable {

	protected VisWindow window   = null;
	protected VisTable  internal = null;
	
	/**
	 * Initiates a new container. Will not be added to the scene until explicity done so with {@link TUserInterface}{@code .mAllocContainer(this)}.
	 */
	public TContainer(Object... objReference) {
		window = new VisWindow(Integer.toString(hashCode()));
		window.setMovable(false);
		window.setResizable(false);
		window.getTitleLabel().remove();
		window.getTitleTable().remove();
		window.setBackground(TUserInterface.DEFAULT_BG);
		setInternalPadding(16, 16, 16, 16);
		internal = new VisTable();
	}
	
	/**
	 * Should outline the design and layout of the container, not functionality.
	 */
	public abstract void pack(final VisTable internal, Object... objReference);
	
	/**
	 * Should outline the ongoing functionality of the container.
	 */
	public abstract void update(float dt);
	
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
	 * Retrieves the underlying {@code VisWindow} instance that holds all the widgets added to this container.
	 *
	 * @return The {@code VisWindow} instance used by this container.
	 */
	public final VisWindow get() {
		return window;
	}
	
	@Override
	public final void dispose() {
		window.clear();
		window.remove();
		window = null;
	}
	
}
