package dev.iwilkey.terrafort.persistent;

import java.io.Serializable;

/**
 * Any object that can be deconstructed into a stream of bytes, saved in persistent memory or sent over the internet, then reconstructed.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TSerializable implements Serializable {
	private static final long serialVersionUID = 4291467591656537485L;
}
