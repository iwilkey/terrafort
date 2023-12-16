package dev.iwilkey.terrafort.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.kotcrab.vis.ui.VisUI;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.ui.containers.TContainer;
import dev.iwilkey.terrafort.ui.containers.TPopup;

/**
 * Manages the primary user interface layer in the Terrafort engine, providing methods for rendering 
 * the UI and responding to changes such as screen resizes. This class serves as a central point 
 * for UI interaction, managing a Stage which handles the layout and input events for the 
 * actors within it.
 * 
 * @author Ian Wilkey (iwilkey)
 */
public final class TUserInterface implements Disposable {
	
	public static final LabelStyle LABEL_STYLE;
	
	static {
		LABEL_STYLE = new LabelStyle();
	}
	
	private static final Array<TContainer> CURRENT_CONTAINERS = new Array<>();
	
	private static TPopup      currentPopup;
	private static BitmapFont  gameFont;
	private static Stage       mom; // terrible name, but IYKYK
	private static DragAndDrop dad;
	
	/**
	 * Initializes the Terrafort engine's UI capability.
	 */
	public TUserInterface() {
		VisUI.load();
		mom                                                          = new Stage(new ScreenViewport());
		dad                                                          = new DragAndDrop();
		currentPopup                                                 = null;
		final FreeTypeFontGenerator                       generator  = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		final FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameters.size                                              = 72;
		gameFont                                                     = generator.generateFont(parameters);
		gameFont.getData().markupEnabled = true;
		LABEL_STYLE.font = gameFont;
		generator.dispose();
		dad.setDragTime(0);
	}
	
	/////////////////////////////////////////////////////////
	// BEGIN TUSERINTERFACE API
	/////////////////////////////////////////////////////////
	
	/**
	 * Begins and renders a new {@link TPopup} until {@link TUserInterface}.endPopup() is called. 
	 * 
	 * <p>
	 * NOTE: This function forces any current rendering {@link TPopup} to be disposed of.
	 * </p>
	 * @param text
	 */
	public static void beginPopup(String header, String body) {
		if(currentPopup != null)
			endPopup();
		currentPopup = new TPopup(header, body);
		mom.addActor(currentPopup.get());
	}
	
	/**
	 * Ends the current {@link TPopup}, if applicable. Nothing happens if there is no active {@link TPopup}.
	 */
	public static void endPopup() {
		if(currentPopup == null)
			return;
		if(currentPopup.get() != null) {
			currentPopup.get().remove();
			currentPopup.dispose();
		}
		currentPopup = null;
	}
	
	/**
	 * Adds a UI container to the Terrafort user interface and returns the added container.
	 *
	 * @param container the {@code TContainer} instance to be added to the UI system.
	 * @return the {@code TContainer} instance that has been added.
	 */
	public static TContainer addContainer(final TContainer container) {
		container.reset();
		CURRENT_CONTAINERS.add(container);
		mom.addActor(container.get());
		return container;
	}
	
	/**
	 * Removes a specified UI container from the Terrafort user interface.
	 *
	 * @param container the {@code TContainer} instance to be removed from the UI.
	 * @return {@code true} if the container was successfully found and removed; 
	 *         {@code false} otherwise.
	 */
	public static boolean removeContainer(final TContainer container) {
		final boolean in = CURRENT_CONTAINERS.removeValue(container, false);
		if(in)
			container.get().remove();
		return in;
	}
	
	/**
	 * Removes and disposes of a specified UI container from the Terrafort user interface.
	 *
	 * @param container the {@code TContainer} instance to be removed from the UI system.
	 * @return {@code true} if the container was found and successfully removed; {@code false} otherwise.
	 */
	public static boolean disposeContainer(final TContainer container) {
		final boolean in = CURRENT_CONTAINERS.removeValue(container, false);
		if(in) {
			container.get().remove();
			container.dispose();
		}
		return in;
	}
	
	/**
	 * Checks if the specified container is currently active within the internal list of containers.
	 *
	 * @param container The {@code TContainer} object to be checked for presence in the list.
	 *                  This is the target container whose active status is being queried.
	 * @return {@code true} if the container is found in the list; {@code false} otherwise.
	 * @see TContainer
	 */
	public static boolean contains(final TContainer container) {
		return CURRENT_CONTAINERS.contains(container, false);
	}
	
	/**
	 * Returns the global game font for UI widgets.
	 */
	public static BitmapFont getGameFont() {
		return gameFont;
	}
	
	public static Stage getParent() {
		return mom;
	}
	
	public static DragAndDrop getDad() {
		return dad;
	}
	
	/////////////////////////////////////////////////////////
	// END TUSERINTERFACE API
	/////////////////////////////////////////////////////////
	
	/**
	 * Updates and renders the UI elements within the stage. This method should be called 
	 * each frame to ensure the UI is consistently updated and drawn to the screen.
	 */
	public void render() {
		if(currentPopup != null)
			currentPopup.update((float)TClock.dt());
		for(final TContainer c : CURRENT_CONTAINERS) {
			c.update();
			c.anchor();
			c.get().pack();
		}
		mom.act((float)TClock.dt());
		mom.draw();
	}
	
	/**
	 * Adjusts the UI elements within the stage to accommodate a new screen size. This method 
	 * should be called in response to screen size changes to ensure the UI layout and 
	 * elements are adjusted and scaled appropriately.
	 *
	 * @param nw The new width that the screen has been resized to.
	 * @param nh The new height that the screen has been resized to.
	 */
	public void resize(final int nw, final int nh) {
		/*
		final float _orig_width = TGraphics.getOriginalWidth();
	    final float _orig_height = TGraphics.getOriginalHeight();
	    final float _sw = nw / _orig_width;
	    final float _sh = nh / _orig_height;
	    final float _s = Math.min(_sw, _sh);
	    */
	    // setGlobalScale(_s);
	    // _background.setSize(nw, nh);
	    mom.getViewport().update(nw, nh, true);
	}
	
	/**
	 * Releases all resources held by this {@code TUserInterface} instance. This includes the 
	 * stage and the UI toolkit, and should be called when the UI is no longer needed to avoid 
	 * memory leaks. Failing to do so might result in unexpected behavior or performance issues.
	 */
	@Override
	public void dispose() {
		for(final TContainer c : CURRENT_CONTAINERS) {
			c.get().remove();
			c.dispose();
		}
		//_background.clear();
		CURRENT_CONTAINERS.clear();
		mom.dispose();
		gameFont.dispose();
		VisUI.dispose();
	}
	
}
