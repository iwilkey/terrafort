package dev.iwilkey.terrafort.gfx.shape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.gfx.TRenderableShape;

/**
 * A simple box defined by a center point, extending out with given width and height. Can be filled or outlined and colored.
 * @author Ian Wilkey (iwilkey)
 */
public class TRect implements TRenderableShape {
	
	private final Vector2 center;
	private final Vector2 dimensions;
	private final Color   color;
	
	private boolean       filled;
	
	public TRect(float cx, float cy, float width, float height) {
		center     = new Vector2(cx, cy);
		dimensions = new Vector2(width, height);
		color = Color.WHITE.cpy();
		filled = true;
	}
	
	public void setCX(float cx) {
		center.x = cx;
	}
	
	public void setCY(float cy) {
		center.y = cy;
	}
	
	public void setWidth(float width) {
		dimensions.x = width;
	}
	
	public void setHeight(float height) {
		dimensions.y = height;
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
		float xx = center.x - (dimensions.x / 2f);
		float yy = center.y - (dimensions.y / 2f);
		renderer.setColor(color);
		renderer.rect(xx, yy, dimensions.x, dimensions.y);
	}

	@Override
	public void drawLined(OrthographicCamera camera, ShapeRenderer renderer) {
		if(filled)
			return;
		float xx = center.x - (dimensions.x / 2f);
		float yy = center.y - (dimensions.y / 2f);
		renderer.setColor(color);
		renderer.rect(xx, yy, dimensions.x, dimensions.y);
	}
	
}
