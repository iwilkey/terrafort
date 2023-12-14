package dev.iwilkey.terrafort.idea;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.kotcrab.vis.ui.widget.VisLabel;

import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * One node in the {@link TIdeaTree}.
 * @author Ian Wilkey (iwilkey)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TIdeaNode extends Node {
	
	public static final LabelStyle LABEL_STYLE;
	
	static {
		LABEL_STYLE = new LabelStyle();
		LABEL_STYLE.font = TUserInterface.getGameFont();
	}
	
	public TIdeaNode(final String name) {
		final VisLabel label = new VisLabel();
		label.setText(name);
		label.setStyle(LABEL_STYLE);
		label.setFontScale(0.16f);
		this.setActor(label);
	}

}
