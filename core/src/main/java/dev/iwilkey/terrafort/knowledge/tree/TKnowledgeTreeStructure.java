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
	}
	
}
