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
public final class TItemSlotWidget extends VisTable {
	
	/**
	 * The size of an inventory slot.
	 */
	public static final int SLOT_SIZE = 48;
	
	private final VisImageButton slot;
	
	/**
	 * Creates a new null item slot.
	 */
	public TItemSlotWidget() {
		slot                        = new VisImageButton((Drawable)null);
		slot.getStyle().focusBorder = null;
		slot.getImage().setTouchable(Touchable.disabled);
		add(slot).center().expand().fill().prefSize(SLOT_SIZE * TUserInterface.getGlobalScale(), SLOT_SIZE * TUserInterface.getGlobalScale());
	}

}
