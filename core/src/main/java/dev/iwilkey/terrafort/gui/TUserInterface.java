package dev.iwilkey.terrafort.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.kotcrab.vis.ui.VisUI;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.container.TContainer;
import dev.iwilkey.terrafort.gui.container.TPopupContainer;
import dev.iwilkey.terrafort.gui.container.TPromptContainer;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;
import dev.iwilkey.terrafort.gui.text.TImmediateModeText;
import dev.iwilkey.terrafort.gui.text.TImmediateModeTextParticle;

/**
 * Manages the primary user interface layer in the Terrafort engine, providing methods for rendering 
 * the UI and responding to changes such as screen resizes. This class serves as a central point 
 * for UI interaction, managing a Stage which handles the layout and input events for the 
 * actors within it.
 * @author Ian Wilkey (iwilkey)
 */
public final class TUserInterface implements Disposable {
	
	public static final int               DROP_SHADOW_DISTANCE               = 4;

	public static final Texture           DEFAULT_NINE_PATCH_TEXTURE         = new Texture(Gdx.files.internal("ui/default.9.png"));
	public static final Texture           BUTTON_DEFAULT_NINE_PATCH_TEXTURE  = new Texture(Gdx.files.internal("ui/node.9.png"));
	public static final Texture           BUTTON_DISABLED_NINE_PATCH_TEXTURE = new Texture(Gdx.files.internal("ui/node-disabled.9.png"));
	
	public static final NinePatch         DEFAULT_NINE_PATCH                 = new NinePatch(DEFAULT_NINE_PATCH_TEXTURE, 5, 5, 5, 5);
	public static final NinePatch         BUTTON_DEFAULT_NINE_PATCH          = new NinePatch(BUTTON_DEFAULT_NINE_PATCH_TEXTURE, 6, 6, 6, 6);
	public static final NinePatch         BUTTON_DISABLED_NINE_PATCH         = new NinePatch(BUTTON_DISABLED_NINE_PATCH_TEXTURE, 6, 6, 6, 6);
	
	public static final NinePatchDrawable DEFAULT_BG                         = new NinePatchDrawable(DEFAULT_NINE_PATCH);
	public static final NinePatchDrawable BUTTON_DEFAULT_BG                  = new NinePatchDrawable(BUTTON_DEFAULT_NINE_PATCH);
	public static final NinePatchDrawable BUTTON_DISABLED_BG                 = new NinePatchDrawable(BUTTON_DISABLED_NINE_PATCH);
	
	/**
	 * The graphical batch used for rendering SAFs (Stand-alone fonts.)
	 */
	public static final SpriteBatch FONT_BATCH = new SpriteBatch();
	
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
	 * Pool for immediate text requests. Cleared after each frame.
	 */
	private static final Array<TImmediateModeText> IMMEDIATE_TEXT_REQUESTS = new Array<>();
	
	/**
	 * Managed text particles.
	 */
	private static final Array<TImmediateModeTextParticle> ACTIVE_TEXT_PARTICLES            = new Array<>();
	private static final Array<TImmediateModeTextParticle> TEXT_PARTICLE_GARBAGE_COLLECTION = new Array<>();
	/**
	 * Current active static containers managed by user interface module.
	 */
	private static final Array<TContainer> ACTIVE_CONTAINERS = new Array<>();

	private static BitmapFont      gameFont;
	private static Stage           io;
	private static TPopupContainer currentPopup;
	private static TPromptContainer currentPrompt;
	private static float           scale;
	
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
	 * Begins and renders a new {@link TPopup} until {@link TUserInterface}.endPopup() is called. 
	 * 
	 * <p>
	 * NOTE: This function forces any current rendering {@link TPopup} to be disposed of.
	 * </p>
	 * @param text
	 */
	public static void mAllocPopup(String header, String body) {
		if(currentPopup != null)
			mFreePopup();
		currentPopup = new TPopupContainer(header, body);
		io.addActor(currentPopup.get());
	}
	
	/**
	 * Ends the current {@link TPopup}, if applicable. Nothing happens if there is no active {@link TPopup}.
	 */
	public static void mFreePopup() {
		if(currentPopup == null)
			return;
		if(currentPopup.get() != null) {
			currentPopup.get().remove();
			currentPopup.dispose();
		}
		currentPopup = null;
	}
	
	/**
	 * Allocate a new prompt container. There can only be one active at a time.
	 */
	public static void mAllocPromptContainer(TPromptContainer container) {
		if(currentPrompt != null)
			mFreePrompt();
		currentPrompt = container;
		io.addActor(currentPrompt.get());
	}
	
	/**
	 * Disposes of the current active prompt container.
	 */
	public static void mFreePrompt() {
		if(currentPrompt == null)
			return;
		if(currentPrompt.get() != null) {
			currentPrompt.get().remove();
			currentPrompt.dispose();
		}
		currentPrompt = null;
	}
	
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
	 * Draw text in world or screen-space at (x, y) with given point size and color.
	 */
	public static void drawText(String text, int x, int y, int point, int color, boolean worldSpace, boolean dropShadow) {
		IMMEDIATE_TEXT_REQUESTS.add(new TImmediateModeText() {
			@Override
			public String getData()     { return text; }
			@Override
			public boolean worldSpace() { return worldSpace; }
			@Override
			public boolean dropShadow() { return dropShadow; }
			@Override
			public int getX()           { return x; }
			@Override
			public int getY()           { return y; }
			@Override
			public int getColor()       { return color; }
			@Override
			public int getPoint()       { return point; }
			@Override
			public int getWrapping()    { return 0; }
		});
	}
	
	/**
	 * Submits a text particle to be managed and rendered by the UI system.
	 */
	public static void submitTextParticle(TImmediateModeTextParticle particle) {
		ACTIVE_TEXT_PARTICLES.add(particle);
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
	
	/**
	 * A tool that can calculate the width and height of any given text using the game font.
	 */
	private static GlyphLayout layout = new GlyphLayout();
	public static GlyphLayout getGlyphLayout() {
		return layout;
	}

	/////////////////////////////////////////////////////////
	// END TUSERINTERFACE API
	/////////////////////////////////////////////////////////
	
	/**
	 * The actual rendered color of the immediate mode text...
	 */
	private Color textCol = new Color();
	
	/**
	 * Updates and renders the UI elements within the stage. This method should be called 
	 * each frame to ensure the UI is consistently updated and drawn to the screen.
	 */
	public void render(float dt) {
		// if mutex ref goes below zero then there DEFINITLEY isn't any UI cursor focus.
		if(guiModuleMutexReferences < 0)
			guiModuleMutexReferences = 0;
		if(currentPrompt != null)
			currentPrompt.update(dt);
		if(currentPopup != null)
			currentPopup.update(dt);
		for(final TContainer c : ACTIVE_CONTAINERS) {
			c.update(dt);
			// make sure the containers are positioned right on the screen...
			if(c instanceof TStaticContainer)
				((TStaticContainer)c).anchor();
			c.get().pack();
		}
		renderImmediateModeText(dt);
		io.act(dt);
		io.draw();
	}
	
	/**
	 * Manages and renders immediate mode text requests, including particles.
	 */
	public void renderImmediateModeText(float dt) {
		// Manage text particles...
			for(final TImmediateModeTextParticle textParticle : ACTIVE_TEXT_PARTICLES) {
				textParticle.tick(dt);
				if(textParticle.done()) {
					// add to trash...
					TEXT_PARTICLE_GARBAGE_COLLECTION.add(textParticle);
					continue;
				}
				// submit for rendering (still alive)...
				IMMEDIATE_TEXT_REQUESTS.add(textParticle);
			}
			// Do garbage collection on text particles...
			ACTIVE_TEXT_PARTICLES.removeAll(TEXT_PARTICLE_GARBAGE_COLLECTION, false);
			TEXT_PARTICLE_GARBAGE_COLLECTION.clear();
			// Draw immediate text requests...
			FONT_BATCH.begin();
			for(final TImmediateModeText textRequest : IMMEDIATE_TEXT_REQUESTS) {
				// set metadata...
				gameFont.getData().setScale((float)textRequest.getPoint() / BASE_FONT_SIZE);
				textCol.set(textRequest.getColor());
				gameFont.setColor(textCol);
				// figure out the final position to render the text...
				int x;
				int y;
				if(textRequest.worldSpace()) {
					// project from world-space to screen-space...
					final Vector3 projCoords = new Vector3(textRequest.getX(), textRequest.getY(), 0);
					TGraphics.WORLD_PROJ_MAT.project(projCoords);
					x = Math.round(projCoords.x);
					y = Math.round(projCoords.y);
				} else {
					x = textRequest.getX();
					y = textRequest.getY();
				}
				// Check if the text should be rendered...
		        int   screenWidth  = Gdx.graphics.getWidth();
		        int   screenHeight = Gdx.graphics.getHeight();
		        float textWidth    = textRequest.getDimensions().x;
		        // Check horizontal bounds...
		        if(x + textWidth / 2 < 0 || x - textWidth / 2 > screenWidth)
		            continue;
		        // Check vertical bounds...
		        float textHeight = textRequest.getDimensions().y;
		        if(y + textHeight / 2 < 0 || y - textHeight / 2 > screenHeight)
		             continue;
		        // Render...
		        if(textRequest.dropShadow()) {
		        	// Render drop shadow first...
		        	textCol.set(textRequest.getColor() & 0x000000ff);
		        	gameFont.setColor(textCol);
		        	gameFont.draw(FONT_BATCH, 
		        		      textRequest.getData(), 
		        		      x - (textWidth / 2f) + DROP_SHADOW_DISTANCE, 
		        		      y - (textHeight / 2f) - DROP_SHADOW_DISTANCE, 
		        		      (textRequest.getWrapping() > 0) ? textRequest.getWrapping() : textWidth, 
		        		      textRequest.getAlignment(), 
		        		      textRequest.getWrapping() > 0);
		        	textCol.set(textRequest.getColor());
		        	gameFont.setColor(textCol);
		        }
		        gameFont.draw(FONT_BATCH, 
		        		      textRequest.getData(), 
		        		      x - (textWidth / 2f), 
		        		      y - (textHeight / 2f), 
		        		      (textRequest.getWrapping() > 0) ? textRequest.getWrapping() : textWidth, 
		        		      textRequest.getAlignment(), 
		        		      textRequest.getWrapping() > 0);
			}
			gameFont.getData().setScale(1f);
			gameFont.setColor(Color.WHITE);
			FONT_BATCH.end();
			IMMEDIATE_TEXT_REQUESTS.clear();
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
		FONT_BATCH.getProjectionMatrix().setToOrtho2D(0, 0, nw, nh);
	    io.getViewport().update(nw, nh, true);
	    mFreePrompt();
	    mFreePopup();
	}
	
	@Override
	public void dispose() {
		if(currentPopup != null)
			mFreePopup();
		for(final TContainer c : ACTIVE_CONTAINERS) {
			c.get().remove();
			c.dispose();
		}
		ACTIVE_CONTAINERS.clear();
		FONT_BATCH.dispose();
		ACTIVE_TEXT_PARTICLES.clear();
		IMMEDIATE_TEXT_REQUESTS.clear();
		io.dispose();
		gameFont.dispose();
		VisUI.dispose();
		DEFAULT_NINE_PATCH_TEXTURE.dispose();
		BUTTON_DEFAULT_NINE_PATCH_TEXTURE.dispose();
		BUTTON_DISABLED_NINE_PATCH_TEXTURE.dispose();
	}

}
