package dev.iwilkey.terrafort.persistent;

import java.io.Serializable;

/**
 * Provides a framework for any non-serializable target object to be represented as serializable fields that represent the current state of the target.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TSerializableProxy extends TSerializable {

	private static final long serialVersionUID = -1253785359880903389L;
	
	/**
	 * Captures the state of a given target object in extending implementation's serializable fields. 
	 */
	public TSerializableProxy(final Object target) {
		if(target instanceof Serializable)
			throw new IllegalArgumentException("[TSerializableObjectState] The provided target object is already fully serializable!");
	}
	
}
