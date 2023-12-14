package dev.iwilkey.terrafort.ui.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * A simple UI window that follows the cursor while it is active. Renders a header and body text.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPopup implements Disposable {
	
	private final VisWindow window;
	
	/**
	 * Creates a new {@link TPopup} instance, with given header and body data. 
	 * 
	 * <p>
	 * NOTE: This data is static and cannot be changed; you'll have to call {@link TUserInterface}.beginPopup(new data).
	 * </p>
	 */
	public TPopup(String header, String body) {
		window = new VisWindow(Integer.toString(hashCode()), false);
		window.setMovable(false);
		window.setResizable(false);
		window.getTitleLabel().remove();
		window.getTitleTable().remove();
		setInternalPadding(4, 4, 8, 8);
		final VisTable table = new VisTable();
		final VisLabel label = new VisLabel(header);
		label.setAlignment(Align.center);
		label.getStyle().font = TUserInterface.getGameFont();
		label.setFontScale(0.16f);
		table.add(label).expand().fill();
		table.row();
		table.addSeparator();
		final VisLabel desc = new VisLabel(body);
		desc.setAlignment(Align.left);
		desc.getStyle().font = TUserInterface.getGameFont();
		desc.setFontScale(0.16f);
		table.add(desc).expand().fill();
		window.add(table).expand().fill();
		window.pack();
		window.setTouchable(Touchable.disabled);
		window.setVisible(false);
		final WindowStyle style = new WindowStyle();
		style.titleFont = TUserInterface.getGameFont();
		style.background = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, (int)window.getWidth(), (int)window.getHeight(), 2, 2);
		window.setStyle(style);
	}
	
	/**
	 * Called internally while a {@link TPopup} is active. Instructs the popup how to follow the mouse position.
	 */
	public void update(float dt) {
		if(window == null)
			return;
		final float mouseX = TInput.cursorX;
	    final float mouseY = Gdx.graphics.getHeight() - TInput.cursorY;
	    final float posX = (mouseX - (window.getWidth() / 2));
	    final float posY = (mouseY + 4);
	    window.setPosition((int)posX, (int)posY);
	    window.toFront();
	    window.setVisible(true);
	}
	
	/**
	 * Returns the {@link VisWindow} that encapsulates the popup header and body data.
	 * @return
	 */
	public VisWindow get() {
		return window;
	}
	
	/**
	 * Sets the internal padding of the container, which dictates the spacing between the container's 
	 * edges and its content. This method adjusts the padding on all sides of the container.
	 *
	 * @param top The size of the top padding.
	 * @param bottom The size of the bottom padding.
	 * @param right The size of the right padding.
	 * @param left The size of the left padding.
	 */
	public final void setInternalPadding(final int top, final int bottom, final int right, final int left) {
		window.padTop(top);
		window.padBottom(bottom);
		window.padRight(right);
		window.padLeft(left);
	}
	
	@Override
	public void dispose() {
		window.clear();
		window.remove();
	}

}
