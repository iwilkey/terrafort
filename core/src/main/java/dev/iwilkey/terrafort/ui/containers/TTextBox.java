package dev.iwilkey.terrafort.ui.containers;

import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.ui.TWidgets;

/**
 * A basic text box.
 * @author Ian Wilkey (iwilkey)
 */
public class TTextBox extends TContainer {
	
	private String   output;
	private VisLabel text;
	
	public TTextBox(String output) {
		this.output = output;
	}
	
	public void setText(String text) {
		output = text;
	}
	
	@Override
	public void pack(VisWindow window) {
		final VisTable textSection = new VisTable();
		text                       = TWidgets.label();
		text.setAlignment(Align.center);
		textSection.add(text);
		window.add(textSection).expand();
	}

	@Override
	public void update() {
		text.setText(output);
		text.setFontScale(0.2f);
	}

}
