package dev.iwilkey.terrafort.tree;

import java.util.ArrayList;

/**
 * The abstract structure of the Terrafort Tech Tree.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTechTree {
	
	/**
	 * The abstract structure of the Terrafort Tech Tree.
	 */
	public static final ArrayList<TNode> STRUCTURE;
	
	static {
		final TNode node0 = new TNode("parent"); // parent.
		final TNode node1 = new TNode("node1");
		final TNode node2 = new TNode("node2");
		final TNode node3 = new TNode("node3");
		final TNode node4 = new TNode("node4");
		final TNode node5 = new TNode("node5");
		final TNode node6 = new TNode("node6");
		final TNode node7 = new TNode("node7");
		final TNode node8 = new TNode("node8");
		
		node1.requires    = node0;
		node2.requires    = node0;
		node3.requires    = node1;
		node4.requires    = node2;
		node5.requires    = node3;
		node6.requires    = node2;
		node7.requires    = node2;
		node8.requires    = node3;
		
		STRUCTURE = new ArrayList<>();
		STRUCTURE.add(node0);
		STRUCTURE.add(node1);
		STRUCTURE.add(node2);
		STRUCTURE.add(node3);
		STRUCTURE.add(node4);
		STRUCTURE.add(node5);
		STRUCTURE.add(node6);
		STRUCTURE.add(node7);
		STRUCTURE.add(node8);
		
		for(TNode s : STRUCTURE) 
			if(s.requires != null) 
				s.level = s.requires.level + 1;
	}
		
}
