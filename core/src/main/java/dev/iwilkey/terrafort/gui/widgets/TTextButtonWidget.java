package dev.iwilkey.terrafort.gui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTextButton;

import dev.iwilkey.terrafort.gui.TEvent;
import dev.iwilkey.terrafort.gui.TUserInterface;

/**
 * A simple text button.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTextButtonWidget extends VisTextButton {
	
	/**
	 * Create a new text button with specific callback click event.
	 */
	public TTextButtonWidget(String text, TEvent clickEvent) {
		super(text);
		getLabel().setStyle(TUserInterface.TEXT);
		getLabel().setFontScale(0.16f * TUserInterface.getGlobalScale());
		getLabel().setText(text);
		setFocusBorderEnabled(false);
		if(clickEvent != null)
			addListener(new ClickListener() {
				public void clicked(InputEvent event, float x, float y) {
					clickEvent.fire();
				}
			});
	}

}
