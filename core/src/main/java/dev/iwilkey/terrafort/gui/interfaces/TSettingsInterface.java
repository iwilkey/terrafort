package dev.iwilkey.terrafort.gui.interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.TEngine;
import dev.iwilkey.terrafort.clk.TEvent;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.TDrawable;
import dev.iwilkey.terrafort.gui.TPopable;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;
import dev.iwilkey.terrafort.gui.lang.TLanguage;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.gui.widgets.TIconButtonWidget;
import dev.iwilkey.terrafort.gui.widgets.TKnowledgeTreeWidget;
import dev.iwilkey.terrafort.gui.widgets.TTextButtonWidget;
import dev.iwilkey.terrafort.gui.widgets.TTextWidget;

/**
 * An interface for general Terrafort game and engine settings.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSettingsInterface extends TStaticContainer {
	
	public static final Drawable GEAR_ICON   = TDrawable.fromSpriteSheet("sheets/items-icons.png", 1, 2, 1, 1);
	public static final Drawable CAMERA_ICON = TDrawable.fromSpriteSheet("sheets/items-icons.png", 3, 2, 1, 1);
	public static final Drawable BACK_ICON   = TDrawable.fromSpriteSheet("sheets/items-icons.png", 2, 2, 1, 1);
	
	private boolean currentState = true;
	
	public TSettingsInterface(Object... objReference) {
		super(objReference);
		pack(internal);
	}
	
	@Override
	public void pack(VisTable internal, Object... objReference) {
		setAnchor(TAnchor.TOP_RIGHT);
		setExternalPadding(4, 0, 4, 0);
		setInternalPadding(8, 8, 8, 8);
		window.add(internal);
		setState(true);
	}

	@Override
	public void update(float dt) {
		
	}
	
	public boolean getState() {
		return currentState;
	}
	
	/**
	 * Set the state of the settings menu. Minimized just renders a gear.
	 */
	public void setState(boolean minimized) {
		internal.clear();
		internal.reset();
		internal.remove();
		if(minimized) {
			setAnchor(TAnchor.TOP_RIGHT);
			setExternalPadding(4, 0, 4, 0);
			setInternalPadding(8, 8, 8, 8);
			final TIconButtonWidget settingsButton = new TIconButtonWidget(GEAR_ICON, new TEvent() {
				@Override
				public boolean fire() {
					TGraphics.requestBlurState(true, 1.0f);
					setState(false);
					return false;
				}
			});
			settingsButton.addListener(new TPopable(TLocale.getLine(43), TLocale.getLine(44)));			
			final TIconButtonWidget screenshotButton = new TIconButtonWidget(CAMERA_ICON, new TEvent() {
				@Override
				public boolean fire() {
					TGraphics.requestScreenshot();
					return false;
				}
			});
			screenshotButton.addListener(new TPopable(TLocale.getLine(40), TLocale.getLine(41) + " \n\n" 
					+ Gdx.files.internal("TerrafortPersistent/screenshots/").file().getAbsolutePath()));
			internal.add(screenshotButton).center().prefSize(24, 24).padRight(4f);
			internal.add(settingsButton).center().prefSize(24, 24);
		} else {
			setAnchor(TAnchor.CENTER_CENTER);
			setExternalPadding(0, 32, 0, 0);
			setInternalPadding(8, 8, 8, 8);
			internal.add(new TIconButtonWidget(BACK_ICON, new TEvent() {
				@Override
				public boolean fire() {
					TGraphics.requestBlurState(false, 1.0f);
					setState(true);
					return false;
				}
			})).right().prefSize(24, 24);
			internal.row();
			internal.addSeparator().padBottom(2f).padTop(6f);
			internal.row();
			final VisTable      settings = new VisTable();
			final VisScrollPane pane     = new VisScrollPane(settings);
			pane.getStyle().background = TKnowledgeTreeWidget.SOLID_BLACK;
			pane.setFadeScrollBars(false);
			settings.add(new TTextWidget("[YELLOW]LOCALE: []" + TEngine.getPref().locale.name())).pad(8f);
			settings.row();
	        for(TLanguage lang : TLanguage.values()) {
	        	final TTextButtonWidget togLang = new TTextButtonWidget(lang.name(), new TEvent() {
	        		final TLanguage l = lang;
					@Override
					public boolean fire() {
						TEngine.getPref().locale = l;
						setState(false);
						return false;
					}
	        	});
	        	togLang.addListener(new TPopable("[RED]" + TLocale.getLine(38) + "[]", TLocale.getLine(36)));
	        	settings.add(togLang).pad(2f).fillX();
	        	settings.row();
	        }
	        settings.addSeparator().pad(6f);
	        
	        for(int i = 0; i < 64; i++) {
	        	settings.add(new TTextWidget("Other settings..."));
	        	settings.row();
	        }
	        internal.add(pane).fill().expand().prefSize(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
		}
		internal.pack();
		window.add(internal);
		window.pack();
		currentState = minimized;
	}

}
