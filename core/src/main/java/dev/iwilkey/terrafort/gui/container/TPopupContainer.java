package dev.iwilkey.terrafort.gui.container;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gui.widgets.TTextWidget;

/**
 * A simple UI {@link TContainer} that follows the cursor while it is active. Renders a header and body text.
 * @author Ian Wilkey (iwilkey)
 */
public class TPopupContainer extends TContainer {
	
	private final String header;
	private final String body;
	
	/**
	 * Creates a new Popup with given header and body data.
	 */
	public TPopupContainer(String header, String body, Object... reference) {
		super(reference);
		this.header = header;
		this.body   = body;
		pack(internal);
	}

	@Override
	public void pack(VisTable internal, Object... objReference) {
		final VisTable all = new VisTable();
		final TTextWidget header = new TTextWidget(this.header);
		header.setAlignment(Align.center);
		all.add(header).expand().fillX();
		all.row();
		all.addSeparator().padBottom(4f).padTop(4f);
		final TTextWidget desc = new TTextWidget(body);
		desc.setWrap(true);
		all.add(desc).prefWidth(256);
		internal.add(all);
		window.add(internal);
		window.pack();
		window.setTouchable(Touchable.disabled);
		window.setVisible(false);
	}
	
	boolean up = false;
	float   t  = 0.0f;

	@Override
	public void update(float dt) {
		if(window == null)
			return;
		if(!up) {
			t += dt;
			if(t > 0.1f) {
				window.setVisible(true);
				up = true;
			}
		}
		final float mouseX = TInput.cursorX;
	    final float mouseY = Gdx.graphics.getHeight() - TInput.cursorY;
	    final float py     = mouseY / Gdx.graphics.getHeight();
	    final float posX   = (mouseX - (window.getWidth() / 2));
	    final float posY   = (mouseY + 16) - ((py < 0.65f) ? 0f : window.getHeight() + 32f);
	    window.setPosition((int)posX, (int)posY);
	    window.toFront();
	    window.setVisible(true);
	}
	
}
