package dev.iwilkey.terrafort.gui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisImageButton;

import dev.iwilkey.terrafort.gui.TEvent;

/**
 * A simple icon button.
 * @author Ian Wilkey (iwilkey)
 */
public final class TIconButtonWidget extends VisImageButton {
	
	/**
	 * Creates a new icon button with specific callback click event.
	 */
	public TIconButtonWidget(Drawable imageUp, TEvent clickEvent) {
		super(imageUp);
		getImage().setTouchable(Touchable.disabled);
		setFocusBorderEnabled(false);
		addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				clickEvent.fire();
			}
		});
	}

}
