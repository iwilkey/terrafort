package dev.iwilkey.terrafort.gui;

/**
 * A simple callback to fire at a specified time. 
 * @author Ian Wilkey (iwilkey)
 */
public interface TEvent {
	
	/**
	 * Called at a later time by some internal infrastructure.
	 */
	public void fire();
	
}
