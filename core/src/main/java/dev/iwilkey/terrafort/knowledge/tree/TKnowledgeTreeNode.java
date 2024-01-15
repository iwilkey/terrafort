package dev.iwilkey.terrafort.knowledge.tree;

import dev.iwilkey.terrafort.knowledge.TKnowledge;

/**
 * A Knowledge Node. Used to represent some attainable knowledge that can be used in-game.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeTreeNode {
	
	/**
	 * The name of this Knowledge Node.
	 */
	public final TKnowledge knowledge;
	
	/**
	 * The level of it on the tree. Updated internally.
	 */
	public int level = 0;
	
	/**
	 * Whether or not the node is learned. Updated internally.
	 */
	public boolean learned = false;
	
	/**
	 * The Knowledge this Knowledge requires. Updated internally.
	 */
	public TKnowledgeTreeNode requires = null;
	
	/**
	 * Creates a new node with given name.
	 */
	public TKnowledgeTreeNode(TKnowledge knowledge) {
		this.knowledge = knowledge;
	}
	
	/**
	 * Returns the knowledge associated with this node.
	 */
	public TKnowledge getKnowledge() {
		return knowledge;
	}
	
	/**
	 * Quickly returns the name of the knowledge.
	 */
	public String getName() {
		return knowledge.getName();
	}
	
}
