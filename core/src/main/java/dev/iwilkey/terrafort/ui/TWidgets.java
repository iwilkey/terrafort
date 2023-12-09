package dev.iwilkey.terrafort.ui;

import com.kotcrab.vis.ui.widget.VisLabel;

/**
 * A collection of static methods that return ready-made UI widgets in the Terrafort style.
 */
public final class TWidgets {
	
	/**
	 * A label widget renders text using the Terrafort game font.
	 */
	public static VisLabel label() {
		final VisLabel ret = new VisLabel();
		ret.getStyle().font = TUserInterface.getGameFont();
		return ret;
	}
	
}
