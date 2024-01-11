package dev.iwilkey.terrafort.gui.interfaces;

import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;
import dev.iwilkey.terrafort.gui.widgets.TItemSlotWidget;

/**
 * Provides an interface for the user to interact with equipped knowledge.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeBarInterface extends TStaticContainer {
	
	public static final int SLOT_PADDING = 4;
	
	public void pack(VisTable window, Object... objReference) {
		setAnchor(TAnchor.BOTTOM_CENTER);
		setExternalPadding(0, 4, 0, 0);
		for(int i = 0; i < 4; i++)
			window.add(new TItemSlotWidget()).pad(SLOT_PADDING * TUserInterface.getGlobalScale(), 0, SLOT_PADDING * TUserInterface.getGlobalScale(), 0).center().pad(SLOT_PADDING);
	}

	public void update(float dt) {
		
	}
	
}
