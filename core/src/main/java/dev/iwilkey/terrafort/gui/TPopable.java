package dev.iwilkey.terrafort.gui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Null;

/**
 * A direct implementation of an {@link InputListener} that will automatically facilitate the process of creating a {@link TPopup} with given header and body information.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPopable extends InputListener {
	
	private final String header;
	private final String body;
	
	/**
	 * Uses given header and body data to render when widget is hovered over.
	 */
	public TPopable(String header, String body) {
		this.header = header;
		this.body   = body;
	}
	
	@Override
	public void enter(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
		TUserInterface.mAllocPopup(header, body);
	}
	
	@Override
	public void exit(InputEvent event, float x, float y, int pointer, @Null Actor fromActor) {
		TUserInterface.mFreePopup();
	}
	
}
