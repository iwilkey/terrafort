package dev.iwilkey.terrafort.gui.interfaces;

import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;
import dev.iwilkey.terrafort.gui.widgets.TTechTreeWidget;

/**
 * Provides an interface for the player to interact with the Terrafort tech tree.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTechTreeInterface extends TStaticContainer {
	
	@Override
	public void pack(VisTable internal, Object... objReference) {
		setAnchor(TAnchor.CENTER_CENTER);
		setExternalPadding(0, 32, 0, 0);
		window.add(new TTechTreeWidget()).center();
		get().setVisible(false);
	}
	
	boolean up = false;
	float t = 0.0f;

	@Override
	public void update(float dt) {
		if(!up) {
			t += dt;
			if(t > 0.1f) {
				get().setVisible(true);
				 t = 0.0f;
				 up = true;
			}
		}
	}

}
