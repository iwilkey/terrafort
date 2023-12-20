package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TEngine;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.state.TDemoState;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.TContainer;

public class TMainMenuInterface extends TContainer {
	
	public enum TOption {
		PLAY,
		EXIT;
	}
	
	public static final float WIDTH  = 1280 * 0.25f;
	public static final float HEIGHT = 720 * 0.25f;
	
	private VisTextButton play;
	private VisTextButton quit;
	
	public TMainMenuInterface() {
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 0, 0, 0);
		setAnchor(TAnchor.CENTER_CENTER);
	}
	
	@Override
	public void pack(VisWindow window) {
		final VisTable title = new VisTable();
		final VisLabel terrafort = new VisLabel();
		terrafort.setStyle(TUserInterface.LABEL_STYLE);
		terrafort.setFontScale(0.45f);
		terrafort.setText("Terrafort!");
		terrafort.setAlignment(Align.center);
		title.add(terrafort).expand().fill().pad(0.5f);
		title.row();
		final VisLabel version = new VisLabel();
		version.setStyle(TUserInterface.LABEL_STYLE);
		version.setFontScale(0.16f);
		version.setText("[GRAY]v " + TEngine.VERSION + "[]");
		version.setAlignment(Align.center);
		title.add(version).expand().fill().padBottom(2);
		title.row();
		title.addSeparator();
		window.add(title).prefSize(WIDTH, HEIGHT / 3f).expand().fill();
		window.row();
		final VisTable main = new VisTable();
		play = new VisTextButton("Play Demo");
		play.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				select(TOption.PLAY);
			}
		});
		play.getLabel().setStyle(TUserInterface.LABEL_STYLE);
		play.getLabel().setFontScale(0.25f);
		play.setFocusBorderEnabled(false);
		main.add(play).prefSize(WIDTH / 2, 32).center().padBottom(4);
		main.row();
		quit = new VisTextButton("Exit");
		quit.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				select(TOption.EXIT);
			}
		});
		quit.getLabel().setStyle(TUserInterface.LABEL_STYLE);
		quit.getLabel().setFontScale(0.25f);
		quit.setFocusBorderEnabled(false);
		main.add(quit).prefSize(WIDTH / 2, 32).center().pad(4);
		window.add(main).prefSize(WIDTH, 2 * HEIGHT / 3).expand().fill();
		style.background = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, (int)WIDTH, (int)HEIGHT, 4, 4);
		window.setStyle(style);
	}
	
	TOption target = null;
	boolean count  = false;
	float   time   = 0.0f;

	@Override
	public void update() {
		if(count) {
			time += TClock.dt();
			if(time >= 1.0f) {
				switch(target) {
					case PLAY:
						TGraphics.forceCameraPosition(0, 0);
						TEngine.setState(new TDemoState());
						break;
					case EXIT:
						System.exit(0);
						break;
					default:;
				}
				time = 0.0f;
			}
		}
	}
	
	public void select(TOption o) {
		TGraphics.fadeOut(1f);
		play.setTouchable(Touchable.disabled);
		play.getLabel().setColor(Color.GRAY);
		quit.setTouchable(Touchable.disabled);
		quit.getLabel().setColor(Color.GRAY);
		target = o;
		count = true;
	}

}
