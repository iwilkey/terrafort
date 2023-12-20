package dev.iwilkey.terrafort.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.kotcrab.vis.ui.widget.VisLabel;

import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * A widget that displays information in a {@link TPopup} when the cursor is hovered over it.
 * @author Ian Wilkey (iwilkey)
 */	
public final class TInformationWidget extends VisLabel {
	
	public TInformationWidget(final String header, final String information) {
		setStyle(TUserInterface.LABEL_STYLE);
		setText("[TEAL][?][]");
		setFontScale(0.16f);
		addListener(new InputListener() {
			@Override
		    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				TUserInterface.mallocpop(header, information);
			}
		    @Override
		    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		    	TUserInterface.freepop();
		    }
		});
	}
	
}
