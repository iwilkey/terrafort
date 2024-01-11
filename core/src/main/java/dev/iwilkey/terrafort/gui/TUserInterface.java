package dev.iwilkey.terrafort.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

import dev.iwilkey.terrafort.gui.container.TContainer;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;

/**
 * Manages the primary user interface layer in the Terrafort engine, providing methods for rendering 
 * the UI and responding to changes such as screen resizes. This class serves as a central point 
 * for UI interaction, managing a Stage which handles the layout and input events for the 
 * actors within it.
 * @author Ian Wilkey (iwilkey)
 */
public final class TUserInterface implements Disposable {
	
	/**
	 * The nine-patch texture used to render the background of some UI components.
	 */
	public static final Texture           DEFAULT_NINE_PATCH_TEXTURE = new Texture(Gdx.files.internal("ui/default.9.png"));
	public static final NinePatch         DEFAULT_NINE_PATCH         = new NinePatch(DEFAULT_NINE_PATCH_TEXTURE, 5, 5, 5, 5);
	public static final NinePatchDrawable DEFAULT_BG                 = new NinePatchDrawable(DEFAULT_NINE_PATCH);
	
	/**
	 * The global text style (the game font.)
	 */
	public static final LabelStyle TEXT = new LabelStyle();
	
	/**
	 * The font point of the glyphs stored in RAM.
	 */
	public static final int BASE_FONT_SIZE = 72;
	
	/**
	 * Any value > 0 means that the cursor is currently operating inside of a gui module and so no in-game world action should be made.
	 */
	public static int guiModuleMutexReferences = 0;
	
	/**
	 * Current active static containers managed by user interface module.
	 */
	private static final Array<TContainer> ACTIVE_CONTAINERS = new Array<>();

	private static BitmapFont gameFont;
	private static Stage      io;
	private static float      scale;
	
	public TUserInterface() {
		VisUI.load();
		io                                     = new Stage(new ScreenViewport());
		final FreeTypeFontGenerator generator  = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
		final FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameters.size                        = BASE_FONT_SIZE;
		gameFont                               = generator.generateFont(parameters);
		gameFont.getData().markupEnabled       = true;
		scale                                  = 1.0f;
		generator.dispose();
		TEXT.font = gameFont;
	}
	
	/////////////////////////////////////////////////////////
	// BEGIN TUSERINTERFACE API
	/////////////////////////////////////////////////////////
	
	/**
	 * Adds a static container to the current managed user interface. Nothing happens if the container is already in context.
	 */
	public static void mAllocContainer(TContainer container) {
		if(ACTIVE_CONTAINERS.contains(container, false)) 
			return;
		ACTIVE_CONTAINERS.add(container);
		io.addActor(container.get());
	}
	
	/**
	 * Removes a static container from UI context. Nothing happens if it is not in context.
	 */
	public static void mFreeContainer(TContainer container) {
		if(!ACTIVE_CONTAINERS.contains(container, false))
			return;
		container.get().remove();
		ACTIVE_CONTAINERS.removeValue(container, false);
	}
	
	/**
	 * The stage responsible for updating and rendering the UI.
	 * @return
	 */
	public static Stage getIO() {
		return io;
	}
	
	/**
	 * Returns the current scale preference.
	 */
	public static float getGlobalScale() {
		return scale;
	}
	
	/**
	 * Returns the global game font for UI widgets.
	 */
	public static BitmapFont getGameFont() {
		return gameFont;
	}
	
	/**
	 * Returns whether or not the cursor is currently focused on a gui module.
	 */
	public static boolean guiFocused() {
		return guiModuleMutexReferences != 0;
	}
		
	/////////////////////////////////////////////////////////
	// END TUSERINTERFACE API
	/////////////////////////////////////////////////////////
	
	/**
	 * Updates and renders the UI elements within the stage. This method should be called 
	 * each frame to ensure the UI is consistently updated and drawn to the screen.
	 */
	public void render(float dt) {
		for(final TContainer c : ACTIVE_CONTAINERS) {
			c.update(dt);
			// make sure the containers are positioned right on the screen...
			if(c instanceof TStaticContainer)
				((TStaticContainer)c).anchor();
			c.get().pack();
		}
		io.act(dt);
		io.draw();
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
	    io.getViewport().update(nw, nh, true);
	}
	
	@Override
	public void dispose() {
		for(final TContainer c : ACTIVE_CONTAINERS) {
			c.get().remove();
			c.dispose();
		}
		ACTIVE_CONTAINERS.clear();
		io.dispose();
		gameFont.dispose();
		VisUI.dispose();
		DEFAULT_NINE_PATCH_TEXTURE.dispose();
	}

}
