package dev.iwilkey.terrafort;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.state.TSinglePlayer;
import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * The TEngine class serves as the central entry point and controller for Terrafort.
 * It manages the game's state, input, clock, and rendering system, managing the overall game software processing.
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
	
	public static final String VERSION = "0.0.0.9";
	
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

	private static TState           state       = null;
	private static InputMultiplexer multiplexer = null;
	private static TInput 		    input       = null;
	private static TClock           clock       = null;
	private static TGraphics        renderer    = null;
	private static TUserInterface   ui          = null;
	
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
		state.start();
		TGraphics.fadeIn(0.5f);
	}
	
    @Override
    public void create() {
    	clock    = new TClock();
    	input    = new TInput();
    	renderer = new TGraphics();
    	ui       = new TUserInterface();
    	multiplexer = new InputMultiplexer();
    	multiplexer.addProcessor(TUserInterface.getParent());
    	multiplexer.addProcessor(input);
    	Gdx.input.setInputProcessor(multiplexer);
    	setState(new TSinglePlayer());
    }

    @Override
    public void render() {
    	clock.tick();
    	renderer.render(ui);
    	if(state != null)
    		state.render();
    	input.tick();
    	clock.tock();
    	ui.render();
    	
    	mFrameProcessTime = (float)TClock.pt();
    	mDeltaTime = (float)TClock.dt();
    	
    }
    
    @Override
    public void resize(int newWidth, int newHeight) {
    	renderer.resize(newWidth, newHeight);
    	ui.resize(newWidth, newHeight);
    }

    @Override
    public void dispose() {
    	ui.dispose();
    	setState(null);
    	renderer.dispose();
    }
    
}
