package dev.iwilkey.terrafort.gfx.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TRenderableShape;

/**
 * A simple circle drawn from (xc, yc) extending r world units.
 * @author Ian Wilkey (iwilkey)
 */
public final class TCircle implements TRenderableShape {

	private final Vector2 center;
	private final Color   color;
	private float         radius;
	private boolean       filled;
	
	/**
	 * Creates a new {@link TCircle} with given parameters.
	 */
	public TCircle(int x, int y, float radius) {
		center      = new Vector2(x, y);
		this.radius = radius;
		color       = Color.WHITE.cpy();
		filled      = true;
	}
	
	public void setCX(float cx) {
		center.x = cx;
	}
	
	public void setCY(float cy) {
		center.y = cy;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public void setColor(Color color) {
		this.color.set(color);
	}
	
	public void setFilled(boolean filled) {
		this.filled = filled;
	}
	
	@Override
	public void drawFilled(OrthographicCamera camera, ShapeRenderer renderer) {
		if(!filled)
			return;
		renderer.setColor(color);
		renderer.circle(center.x, center.y, radius);
	}

	@Override
	public void drawLined(OrthographicCamera camera, ShapeRenderer renderer) {
		if(filled)
			return;
		renderer.setColor(color);
		renderer.circle(center.x, center.y, radius);
	}

}
