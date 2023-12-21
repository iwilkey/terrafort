package dev.iwilkey.terrafort;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.state.TMainMenuState;
import dev.iwilkey.terrafort.state.TDemoState;
import dev.iwilkey.terrafort.state.TTessellationLogoState;
import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * The global interface to the Terrafort game engine. It manages the game's state, input, clock, 
 * and rendering system, managing the overall game software processing.
 * 
 * <p>
 * <strong>ENGINE BEST PERFORMANCE JVM ARGUMENTS</strong>: 
 * 
 * -Xms256M -Xmx2G -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -server
 * 
 * </p>
 * @author Ian Wilkey (iwilkey)
 */
public final class TEngine extends ApplicationAdapter {
	
	public static final String VERSION = "0.0.0.13";
	
	// engine metrics (updated internally, even though they are public!) do NOT manually change them!
	
	public static float mFrameProcessTime             = 0.0f;
	public static float mDeltaTime                    = 0.0f;
	public static int   mScreenWidth                  = 0;
	public static int   mScreenHeight                 = 0;
	public static int   mTileBatches                  = 0;
	public static int   mTileDrawCount                = 0;
	public static int   mTileLevelGeometryDrawCount   = 0;
	public static int   mObjectDrawCount              = 0;
	public static int   mObjectLevelGeometryDrawCount = 0;
	public static int   mChunksInMemory               = 0;
	public static int   mChunksDormant                = 0;
	public static int   mPhysicalBodies               = 0;

	private static TState           state             = null;
	private static InputMultiplexer multiplexer       = null;
	private static TInput 		    input             = null;
	private static TClock           clock             = null;
	private static TGraphics        renderer          = null;
	private static TAudio           audio             = null;
	private static TUserInterface   ui                = null;
	
	/**
     * Sets the current state of the game, managing the transition between different states.
     * Stops the current state (if any), clears resources, and starts the new state.
     * @param toState The new game state to transition to, or null to stop the current state without transitioning.
     */
	public static void setState(final TState toState) {
		if(state != null)
			state.stop();
		TGraphics.gc();
		System.gc();
		if(toState == null)
			return;
		state = toState;
		TGraphics.resetFade();
		state.start();
	}
	
	public static TState getState() {
		return state;
	}
	
    @Override
    public void create() {
    	clock    = new TClock();
    	input    = new TInput();
    	renderer = new TGraphics();
    	audio    = new TAudio();
    	ui       = new TUserInterface();
    	multiplexer = new InputMultiplexer();
    	multiplexer.addProcessor(TUserInterface.getMom());
    	multiplexer.addProcessor(input);
    	Gdx.input.setInputProcessor(multiplexer);
    	// setState(new TTessellationLogoState());
    	// setState(new TMainMenuState());
        setState(new TDemoState());
    }

    @Override
    public void render() {
    	clock.tick();
    	renderer.render(ui);
    	if(state != null)
    		state.render();
    	audio.tick();
    	input.tick();
    	clock.tock();
    	ui.render();
    	mFrameProcessTime = (float)TClock.pt();
    	mDeltaTime        = (float)TClock.dt();
    }
    
    @Override
    public void resize(int newWidth, int newHeight) {
    	renderer.resize(newWidth, newHeight);
    	ui.resize(newWidth, newHeight);
    }

    @Override
    public void dispose() {
    	audio.dispose();
    	ui.dispose();
    	setState(null);
    	renderer.dispose();
    }
    
}
