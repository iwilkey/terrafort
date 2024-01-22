package dev.iwilkey.terrafort.gui.widgets;

import java.text.NumberFormat;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.interfaces.TKnowledgeNodeInterface;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.knowledge.tree.TKnowledgeTreeNode;
import dev.iwilkey.terrafort.world.TWorld;

public final class TKnowledgeTreeNodeWidget extends VisTable {
	
	/**
	 * The size of a knowledge tree node.
	 */
	public static final int NODE_SIZE = 32;
	
	private final VisImageButton     slot;
	private final String             name;
	private final int                level;
	private final TKnowledgeTreeNode abstractNode;

	private TKnowledgeTreeNodeWidget requires;
	
	/**
	 * Creates a new null item slot.
	 */
	public TKnowledgeTreeNodeWidget(String name, int level, TKnowledgeTreeNode abstractNode) {
		this.background((abstractNode.learned) ? TUserInterface.BUTTON_DEFAULT_BG : TUserInterface.BUTTON_DISABLED_BG);
		this.name                   = name;
		this.level                  = level;
		this.abstractNode           = abstractNode;
		slot                        = new VisImageButton(abstractNode.getKnowledge().getIcon());
		slot.getStyle().focusBorder = null;
		slot.getImage().setTouchable(Touchable.disabled);
		add(slot).center().expand().fill().prefSize(NODE_SIZE * TUserInterface.getGlobalScale(), NODE_SIZE * TUserInterface.getGlobalScale());
		addListener(new InputListener() {
			@Override
		    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				final boolean practical = abstractNode.getKnowledge().practical();
				final boolean learned = abstractNode.learned;
				final String desc = String.format("%s\n"
						+ "\n[YELLOW]" + TLocale.getLine(26) + ":[] " + ((!learned) ? TLocale.getLine(22) : TLocale.getLine(24))
						+ "\n[YELLOW]" + TLocale.getLine(28) + ":[] " + ((!practical) ? TLocale.getLine(22) : TLocale.getLine(24))
						+ "\n[YELLOW]" + TLocale.getLine(30) + ":[] " + NumberFormat.getNumberInstance().format(abstractNode.getKnowledge().getLearnValue()) + " Funds (F)"
						+ "\n[YELLOW]" + TLocale.getLine(32) + ":[] " + ((!practical) ? "N/A" : NumberFormat.getNumberInstance().format(abstractNode.getKnowledge().getPracticeValue()) + " Funds (F)"),
						abstractNode.getKnowledge().getDescription());
				TUserInterface.mAllocPopup(abstractNode.getName(), desc);
			}
		    @Override
		    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		    	TUserInterface.mFreePopup();
		    }
		});
		addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				TUserInterface.mAllocPromptContainer(new TKnowledgeNodeInterface(abstractNode, TWorld.client));
			}
		});
	}
	
	/**
	 * Gets the node name.
	 */
	public String getNodeName() {
		return name;
	}
	
	/**
	 * Returns the level of the node on the tree.
	 */
	public int getNodeLevel() {
		return level;
	}
	
	/**
	 * Gets the name of the node that this widget depends on.
	 */
	public TKnowledgeTreeNode getAbstractNodeRequirement() {
		return abstractNode.requires;
	}
	
	/**
	 * Returns the abstract node this widget represents.
	 * @return
	 */
	public TKnowledgeTreeNode getAbstractNode() {
		return abstractNode;
	}
	
	/**
	 * Returns the node widget requirement.
	 */
	public TKnowledgeTreeNodeWidget getNodeWidgetRequirement() {
		return requires;
	}
	
	/**
	 * Adds a prerequisite to the node widget.
	 */
	public void requires(TKnowledgeTreeNodeWidget widget) {
		requires = widget;
	}
	
	/**
	 * Sets the node as learned.
	 */
	public void learn() {
		this.background(TUserInterface.BUTTON_DEFAULT_BG);
	}
	
}
