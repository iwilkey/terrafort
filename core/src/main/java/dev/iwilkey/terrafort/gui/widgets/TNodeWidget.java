package dev.iwilkey.terrafort.gui.widgets;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.tree.TNode;

public final class TNodeWidget extends VisTable {
	
	/**
	 * The size of a tech tree node.
	 */
	public static final int NODE_SIZE = 48;
	
	private final VisImageButton  slot;
	private final String          name;
	private final int             level;
	private final TNode           requiresNode;
	
	private       TNodeWidget     requires;
	
	/**
	 * Creates a new null item slot.
	 */
	public TNodeWidget(String name, int level, TNode requiresNode) {
		this.name                   = name;
		this.level                  = level;
		this.requiresNode           = requiresNode;
		slot                        = new VisImageButton((Drawable)null);
		slot.getStyle().focusBorder = null;
		slot.getImage().setTouchable(Touchable.disabled);
		add(slot).center().expand().fill().prefSize(NODE_SIZE * TUserInterface.getGlobalScale(), NODE_SIZE * TUserInterface.getGlobalScale());
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
	public TNode getNodeNameRequirement() {
		return requiresNode;
	}
	
	/**
	 * Returns the node widget requirement.
	 */
	public TNodeWidget getNodeWidgetRequirement() {
		return requires;
	}
	
	/**
	 * Adds a prerequisite to the node widget.
	 */
	public void requires(TNodeWidget widget) {
		requires = widget;
	}
	
}
