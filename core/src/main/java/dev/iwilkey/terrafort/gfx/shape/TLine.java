package dev.iwilkey.terrafort.gfx.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TRenderableShape;

/**
 * A simple line drawn from (x0, y0) to (x1, y1) with given color.
 * @author Ian Wilkey (iwilkey)
 *
 */
public final class TLine implements TRenderableShape {

	private final Vector2 start;
	private final Vector2 end;
	private final Color   color;
	
	/**
	 * Creates a new {@link TLine} with all parameters initialized and set to 0.
	 */
	public TLine() {
		start = new Vector2(0, 0);
		end   = new Vector2(0, 0);
		color = Color.WHITE.cpy();
	}
	
	/**
	 * Creates a new {@link TLine} with given parameters.
	 */
	public TLine(float x0, float y0, float x1, float y1) {
		start = new Vector2(x0, y0);
		end   = new Vector2(x1, y1);
		color = Color.WHITE.cpy();
	}
	
	public void setX0(float x0) {
		start.x = x0;
	}
	
	public void setX1(float x1) {
		end.x = x1;
	}
	
	public void setY0(float y0) {
		start.y = y0;
	}
	
	public void setY1(float y1) {
		end.y = y1;
	}
	
	public void setColor(Color color) {
		this.color.set(color);
	}
	
	@Override
	public void drawFilled(OrthographicCamera camera, ShapeRenderer renderer) {
		// It's a line, it can't be drawn filled.
	}

	@Override
	public void drawLined(OrthographicCamera camera, ShapeRenderer renderer) {
		renderer.setColor(color);
		renderer.line(start, end);
	}

}
