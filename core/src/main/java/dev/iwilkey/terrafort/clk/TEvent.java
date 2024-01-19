package dev.iwilkey.terrafort.clk;

/**
 * A simple callback to fire at a specified time. 
 * @author Ian Wilkey (iwilkey)
 */
public interface TEvent {
	
	/**
	 * Called at a later time by some internal infrastructure. If returns true, the event will never fire again (until it is scheduled again.)
	 */
	public boolean fire();
	
}
