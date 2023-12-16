package dev.iwilkey.terrafort.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TEngine;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.audio.TAudio;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TRenderableRaw;

/**
 * The entry state of the application. Shows the logo of Tessellation, the Indie company responsible for Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTessellationLogoState implements TState {

	public static final float     INITIAL_WAIT     = 1.5f;
	public static final float     DURATION         = 2.0f;
	
	private Texture               tessellationLogo = new Texture(Gdx.files.internal("tessellation-logo.png"));
	private TRenderableRaw logo             = new TRenderableRaw(tessellationLogo);
	
	// various state bits and timers you don't need to worry about
	
	private boolean               inp              = false;
	private boolean               bwp              = false;
	private float                 t                = 0.0f;
	private float                 it               = 0.0f;
	private float                 at               = 0.0f;
	
	@Override
	public void start() {
		TAudio.mallocfx("snd/tessellation.wav");
		logo.x      = 0;
		logo.y      = 0;
		logo.width  = 512;
		logo.height = 512;
		TGraphics.POST_PROCESSING.removeAllEffects();
	}

	@Override
	public void render() {
		logo.x = (Gdx.graphics.getWidth() / 2) - (logo.width / 2);
		logo.y = (Gdx.graphics.getHeight() / 2) - (logo.height / 2);
		if(!bwp) {
			if(it < INITIAL_WAIT) {
				it += TClock.dt();
				return;
			} else {
				TAudio.playfx("snd/tessellation.wav");
				bwp = true;
			}
		}
		t += TClock.dt();
		if(inp) {
			at += TClock.dt() * 1024;
			TGraphics.POST_CHROME_ABER.setMaxDistortion(at);
			if(t > DURATION / 2) {
				TGraphics.POST_PROCESSING.removeAllEffects();
				TEngine.setState(new TMainMenuState());
			}
		}
		TGraphics.draw(logo);
		if(t > DURATION) {
			TGraphics.fadeOut(0.5f);
			TGraphics.POST_PROCESSING.addEffect(TGraphics.POST_CHROME_ABER);
			TGraphics.POST_CHROME_ABER.setMaxDistortion(0.0f);
			inp = true;
			t = 0.0f;
		}
	}

	@Override
	public void stop() {
		TAudio.freefx("snd/tessellation.wav");
		tessellationLogo.dispose();
	}

}
