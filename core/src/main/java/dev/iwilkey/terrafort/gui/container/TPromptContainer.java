package dev.iwilkey.terrafort.gui.container;

import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.clk.TEvent;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.widgets.TTextButtonWidget;

/**
 * A container that, like a {@link TPopupContainer}, begins positioned at the cursor location. Unlike a Popup, it will stay static in the place it was created until the
 * cursor clicks outside of it's bounds, destroying it. It can be packed with any widget group. Great for simple actions that require additional user input.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TPromptContainer extends TContainer {
	
	private final float cursorX;
	private final float cursorY;
	
	public TPromptContainer(Object... objReference) {
		super(objReference);
		internal.add(new TTextButtonWidget("x", new TEvent() {
			@Override
			public boolean fire() {
				TUserInterface.mFreePrompt();
				return false;
			}
		})).right();
		internal.row();
		internal.addSeparator().pad(4f);
		cursorX = TInput.cursorX;
		cursorY = Gdx.graphics.getHeight() - TInput.cursorY;
	}
	
	/**
	 * The update action of the container. Called every frame it is active.
	 */
	public abstract void behavior(float dt);
	
	@Override
	public void update(float dt) {
	    final float py = cursorY / Gdx.graphics.getHeight();
	    final float x  = (cursorX - (window.getWidth() / 2));
	    final float y  = (cursorY + 16) - ((py < 0.65f) ? 0f : window.getHeight() + 32f);
	    window.setPosition(x, y);
	    window.toFront();
	    behavior(dt);
	}

}
