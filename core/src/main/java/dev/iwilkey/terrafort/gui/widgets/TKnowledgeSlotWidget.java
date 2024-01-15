package dev.iwilkey.terrafort.gui.widgets;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TUserInterface;

/**
 * Holds a reference to a {@link TItem}.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeSlotWidget extends VisTable {
	
	/**
	 * The size of an inventory slot.
	 */
	public static final int SLOT_SIZE = 36;

	private final VisImageButton slot;
	
	/**
	 * Whether or not this Knowledge slot is the selected slot.
	 */
	private boolean selected = false;
	
	/**
	 * Creates a new null item slot.
	 */
	public TKnowledgeSlotWidget() {
		slot                        = new VisImageButton((Drawable)null);
		slot.getStyle().focusBorder = null;
		selected                    = false;
		slot.getImage().setTouchable(Touchable.disabled);
		add(slot).center().expand().fill().prefSize(SLOT_SIZE * TUserInterface.getGlobalScale(), SLOT_SIZE * TUserInterface.getGlobalScale());
	}
	
	/**
	 * Whether or not this slot is the current selected slot.
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Select the current knowledge slot for use. Sets graphics accordingly.
	 */
	public void select() {
		background(TUserInterface.BUTTON_DISABLED_BG);
		selected = true;
	}
	
	/**
	 * Set the knowledge slot as not selected. Sets graphics accordingly.
	 */
	public void reset() {
		background((Drawable)null);
		selected = false;
	}
	
}
