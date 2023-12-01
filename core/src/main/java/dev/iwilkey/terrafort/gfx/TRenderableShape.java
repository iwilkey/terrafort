package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * An object that defines and facilitates the drawing of vector geometry on to the screen. Supports
 * both filled and lined render strategies.
 * @author Ian Wilkey (iwilkey)
 */
public interface TRenderableShape {
	
	/**
	 * Instructs the {@link TGraphics} renderer how to use the {@link ShapeRenderer} to draw
	 * the shape, or collection of geometry, the implementing class aims to describe. The strategy
	 * of rendering is with GL20.GL_TRIANGLES, filling the final shape in.
	 * @param camera the projection matrix.
	 * @param renderer the shape renderer, primed to draw geometry in a filled manner.
	 */
	public void drawFilled(final OrthographicCamera camera, final ShapeRenderer renderer);
	
	/**
	 * Instructs the {@link TGraphics} renderer how to use the {@link ShapeRenderer} to draw
	 * the shape, or collection of geometry, the implementing class aims to describe. The strategy
	 * of rendering is with GL20.GL_LINES, only outlining the final shape.
	 * @param camera the projection matrix.
	 * @param renderer the shape renderer, primed to draw geometry in a lined manner.
	 */
	public void drawLined(final OrthographicCamera camera, final ShapeRenderer renderer);
	
}
