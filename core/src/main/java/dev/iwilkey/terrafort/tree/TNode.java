package dev.iwilkey.terrafort.tree;

/**
 * A Knowledge Node. Used to represent some attainable knowledge that can be used in-game.
 * @author Ian Wilkey (iwilkey)
 */
public final class TNode {
	
	/**
	 * The name of this Knowledge Node.
	 */
	public final String name;
	
	/**
	 * The level of it on the tree. (Calculated internally.)
	 */
	public int   level = 0;
	
	/**
	 * The Knowledge this Knowledge requires.
	 */
	public TNode requires = null;
	
	/**
	 * Creates a new node with given name.
	 */
	public TNode(String name) {
		this.name = name;
	}
	
}
