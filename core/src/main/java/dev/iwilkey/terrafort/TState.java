package dev.iwilkey.terrafort;

/**
 * An object that leverages the Terrafort engine components to manage a level, menu, or other interactive experience.
 * @author Ian Wilkey (iwilkey)
 */
public interface TState {
	
	/**
	 * Called right when the state becomes the main focus of the engine.
	 */
	public void start();
	
	/**
	 * Called as the state is the main focus of the engine.
	 */
	public void render(float dt);
	
	/**
	 * Called right as the engine is switching to another state.
	 */
	public void stop();	
	
	/**
	 * Called when the screen resizes itself.
	 */
	public void resize(int nw, int nh);
	
}
