package dev.iwilkey.terrafort;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.state.TDebugState;

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

	private static TState        state    = null;
	private static TInput 		 input    = null;
	private static TClock        clock    = null;
	private static TGraphics     renderer = null;
	
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
    	Gdx.input.setInputProcessor(input);
    	setState(new TDebugState());
    }

    @Override
    public void render() {
    	clock.tick();
    	renderer.render();
    	if(state != null)
    		state.render();
    	input.tick();
    	clock.tock();
    }
    
    @Override
    public void resize(int newWidth, int newHeight) {
    	renderer.resize(newWidth, newHeight);
    }

    @Override
    public void dispose() {
    	setState(null);
    	renderer.dispose();
    }
    
}
