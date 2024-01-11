package dev.iwilkey.terrafort;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.persistent.TPersistent;
import dev.iwilkey.terrafort.state.TSinglePlayerWorld;

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
public final class TEngine implements ApplicationListener {
	
	public static final String VERSION = "0.0.1.0";
	
	static TPersistent      persistent;
	static TGraphics        gfx;
	static TUserInterface   ui;
	static TInput           input;
	static TClock           clk;
	static TState           state;
	
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
	}
	
	/**
	 * Return the current operating state of Terrafort.
	 */
	public static TState getState() {
		return state;
	}
	
    @Override
    public void create() {
    	persistent = new TPersistent();
    	// directory to hold single player world persistent data...
    	if(!TPersistent.pathExists("world/"))
    		TPersistent.establish("world", false);
    	clk        = new TClock();
    	input      = new TInput();
    	gfx        = new TGraphics();
    	ui         = new TUserInterface();
    	Gdx.input.setInputProcessor(input);
    	setState(new TSinglePlayerWorld());
    }

    @Override
    public void resize(int width, int height) {
    	if(state != null)
    		state.resize(width, height);
    	ui.resize(width, height);
    	gfx.resize(width, height);
    }
    
    float t = 0.0f;
    
    @Override
    public void render() {
    	clk.tick();
    	float dt = (float)TClock.dt();
    	t += dt;
    	if(t > 1.0f) {
    		System.out.println("Fps: " + (1 / dt));
    		t = 0.0f;
    	}
    	if(state != null)
    		state.render(dt);
    	input.tick();
    	gfx.render(ui, dt);
    	clk.tock();
    }

    @Override
    public void pause() {
    	TInput.focused = false;
    }

    @Override
    public void resume() {
    	TInput.focused = true;
    }

    @Override
    public void dispose() {
    	setState(null);
    	ui.dispose();
    	gfx.dispose();
    }
    
}
