package dev.iwilkey.terrafort.gui.interfaces;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;
import dev.iwilkey.terrafort.gui.widgets.TKnowledgeTreeWidget;

/**
 * Provides an interface for the player to interact with the Terrafort tech tree.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeTreeInterface extends TStaticContainer {
	
	public TKnowledgeTreeWidget tree;
	
	private boolean up   = false;
	private float   time = 0.0f;
	
	public TKnowledgeTreeInterface(Object... objReference) {
		super(objReference);
		pack(internal);
	}
	
	@Override
	public void pack(VisTable internal, Object... objReference) {
		setAnchor(TAnchor.CENTER_CENTER);
		setExternalPadding(0, 32, 0, 0);
		tree = new TKnowledgeTreeWidget();
		internal.add(tree).center();
		window.add(internal);
		get().setVisible(false);
		window.addListener(new ClickListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				TUserInterface.mFreePrompt();
				return false;
			}
		});
	}

	@Override
	public void update(float dt) {
		if(!up) {
			time += dt;
			if(time > 0.5f) {
				get().setVisible(true);
				 time = 0.0f;
				 up = true;
			}
		}
	}
}
