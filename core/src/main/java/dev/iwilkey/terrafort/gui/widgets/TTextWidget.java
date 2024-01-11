package dev.iwilkey.terrafort.gui.widgets;

import com.kotcrab.vis.ui.widget.VisLabel;

import dev.iwilkey.terrafort.gui.TUserInterface;

/**
 * A simple text widget.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTextWidget extends VisLabel {
	
	public static final float POINT_SCL = 0.16f;
	
	public TTextWidget(String initialText) {
		setStyle(TUserInterface.TEXT);
		setText(initialText);
		setFontScale(POINT_SCL * TUserInterface.getGlobalScale());
	}
	
}
