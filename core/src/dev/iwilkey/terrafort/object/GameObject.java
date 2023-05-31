package dev.iwilkey.terrafort.object;

import dev.iwilkey.terrafort.state.State;

/**
 * An abstract framework for an object that is active within the Terrafort Engine.
 */
public class GameObject {
	
	protected State state;
	protected long id;
	protected boolean shouldRender = true;
	protected boolean shouldDispose = false;
	
	/**
	 * Constructs a new GameObject instance with the specified state.
	 * @param state the state of the game object
	 */
	public GameObject(State state) {
		this.state = state;
	}
	
	/**
	 * Checks if the game object should be rendered.
	 * @return true if the game object should be rendered, false otherwise
	 */
	public boolean shouldRender() {
		return shouldRender;
	}
	
	/**
	 * Checks if the game object should be disposed.
	 * @return true if the game object should be disposed, false otherwise
	 */
	public boolean shouldDispose() {
		return shouldDispose;
	}
	
	/**
	 * Sets the ID of the game object.
	 * @param id the ID to set
	 * @return the game object itself
	 */
	public GameObject setID(long id) {
		this.id = id;
		return this;
	}
	
	/**
	 * Sets the game object to be disposed.
	 * @return the game object itself
	 */
	public GameObject setShouldDispose() {
		shouldDispose = true;
		return this;
	}
	
	/**
	 * Sets whether the game object should be rendered.
	 * @param verdict the verdict to set
	 * @return the game object itself
	 */
	public GameObject setShouldRender(boolean verdict) {
		shouldRender = verdict;
		return this;
	}
	
	/**
	 * Called when the game object is instantiated.
	 */
	public void instantiation() {}
	
	/**
	 * Called during each tick of the game object.
	 */
	public void tick() {}
	
	/**
	 * Called when the game object is being disposed.
	 */
	public void dispose() {}
	
}

