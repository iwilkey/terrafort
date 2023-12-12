package dev.iwilkey.terrafort.ui.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.ui.TUserInterface;

public final class TPopup implements Disposable {
	
	private final VisWindow window;
	
	public TPopup(String name, String description) {
		
		window = new VisWindow(Integer.toString(hashCode()), false);
		window.setMovable(false);
		window.setResizable(false);
		window.getTitleLabel().remove();
		window.getTitleTable().remove();
		
		setInternalPadding(4, 4, 8, 8);
		
		final VisTable table = new VisTable();
		
		final VisLabel label = new VisLabel(name);
		label.setAlignment(Align.center);
		label.getStyle().font = TUserInterface.getGameFont();
		label.setFontScale(0.16f);
		table.add(label).expand().fill();
		table.row();
		
		table.addSeparator();
		Colors.put("RED", Color.RED);
		final VisLabel desc = new VisLabel("This is a [RED]popup[] description");
		desc.setColor(Color.GRAY);
		desc.setAlignment(Align.left);
		desc.getStyle().font = TUserInterface.getGameFont();
		desc.setFontScale(0.16f);
		table.add(desc).expand().fill();
		
		
		window.add(table).expand().fill();
		window.pack();
		window.setTouchable(Touchable.disabled);
		window.setVisible(false);
	}
	
	public void update(float dt) {
		if(window == null)
			return;
		final float mouseX = TInput.cursorX + 4;
	    final float mouseY = Gdx.graphics.getHeight() - TInput.cursorY + 4;
	    window.setPosition((int)mouseX, (int)mouseY);
	    window.toFront();
	    window.setVisible(true);
	}
	
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
