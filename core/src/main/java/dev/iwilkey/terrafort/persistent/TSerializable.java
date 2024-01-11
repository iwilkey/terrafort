package dev.iwilkey.terrafort.persistent;

import java.io.Serializable;

/**
 * An object that can be represented as a stream of bytes and reconstructed back into a runtime state.
 * @author Ian Wilkey (iwilkey)
 */
public interface TSerializable extends Serializable {
	
	/**
	 * Indicates that the object has been created from it's state and persistent memory and must recreate a valid runtime.
	 */
	public void loadFromPersistent();
	
}
