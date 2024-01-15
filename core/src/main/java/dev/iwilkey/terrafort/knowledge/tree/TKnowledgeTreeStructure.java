package dev.iwilkey.terrafort.knowledge.tree;

import java.util.ArrayList;

import dev.iwilkey.terrafort.knowledge.TAdvancedMathematicsKnowledge;
import dev.iwilkey.terrafort.knowledge.TCarpentryKnowledge;
import dev.iwilkey.terrafort.knowledge.TCommonSenseKnowledge;
import dev.iwilkey.terrafort.knowledge.TEngineeringKnowledge;
import dev.iwilkey.terrafort.knowledge.TGeneralMathematicsKnowledge;
import dev.iwilkey.terrafort.knowledge.TSelfDefenseKnowledge;

/**
 * The abstract structure of the Terrafort Knowledge Tree.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeTreeStructure {
	
	/**
	 * The abstract structure of the Terrafort Knowledge Tree.
	 */
	public static final ArrayList<TKnowledgeTreeNode> STRUCTURE;
	
	static {
		
		final TKnowledgeTreeNode commonSense         = new TKnowledgeTreeNode(new TCommonSenseKnowledge());
		final TKnowledgeTreeNode generalMathematics  = new TKnowledgeTreeNode(new TGeneralMathematicsKnowledge());
		final TKnowledgeTreeNode advancedMathematics = new TKnowledgeTreeNode(new TAdvancedMathematicsKnowledge());
		final TKnowledgeTreeNode generalCarpentry    = new TKnowledgeTreeNode(new TCarpentryKnowledge());
		final TKnowledgeTreeNode generalSelfDefense  = new TKnowledgeTreeNode(new TSelfDefenseKnowledge());
		final TKnowledgeTreeNode engineering         = new TKnowledgeTreeNode(new TEngineeringKnowledge()); 
		
		commonSense.learned          = true;
		generalMathematics.learned   = true;
		advancedMathematics.learned  = true;
		generalMathematics.requires  = commonSense;
		advancedMathematics.requires = generalMathematics;
		generalSelfDefense.requires  = commonSense;
		generalCarpentry.requires    = generalMathematics;
		engineering.requires         = advancedMathematics;
		
		STRUCTURE = new ArrayList<>();
		STRUCTURE.add(commonSense);
		STRUCTURE.add(generalSelfDefense);
		STRUCTURE.add(generalMathematics);
		STRUCTURE.add(advancedMathematics);
		STRUCTURE.add(generalCarpentry);
		STRUCTURE.add(engineering);
		
		for(TKnowledgeTreeNode s : STRUCTURE) 
			if(s.requires != null) 
				s.level = s.requires.level + 1;
		
		/*
		final TKnowledgeTreeNode node0 = new TKnowledgeTreeNode("parent"); // parent.
		final TKnowledgeTreeNode node1 = new TKnowledgeTreeNode("node1");
		final TKnowledgeTreeNode node2 = new TKnowledgeTreeNode("node2");
		final TKnowledgeTreeNode node3 = new TKnowledgeTreeNode("node3");
		final TKnowledgeTreeNode node4 = new TKnowledgeTreeNode("node4");
		final TKnowledgeTreeNode node5 = new TKnowledgeTreeNode("node5");
		final TKnowledgeTreeNode node6 = new TKnowledgeTreeNode("node6");
		final TKnowledgeTreeNode node7 = new TKnowledgeTreeNode("node7");
		final TKnowledgeTreeNode node8 = new TKnowledgeTreeNode("node8");
		
		node0.learned = true;
		node1.requires = node0;
		node2.requires = node0;
		node3.requires = node1;
		node4.requires = node2;
		node5.requires = node3;
		node6.requires = node2;
		node7.requires = node2;
		node8.requires = node3;
		
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
		 */
	}
	
}
