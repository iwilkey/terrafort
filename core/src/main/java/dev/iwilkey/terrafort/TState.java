package dev.iwilkey.terrafort;

/**
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
	public void render();
	
	/**
	 * Called right as the engine is switching to another state.
	 */
	public void stop();	
	
}
