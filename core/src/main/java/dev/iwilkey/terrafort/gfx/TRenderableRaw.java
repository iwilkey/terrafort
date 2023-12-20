package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Renders an entire {@link Texture} from memory at settable position and dimension.
 * @author Ian Wilkey (iwilkey)
 */
public final class TRenderableRaw implements TRenderableSprite {
	
	public Texture texture;
	public int x;
	public int y;
	public int width;
	public int height;
	
	/**
	 * Creates a new raw texture for TGraphics rendering. Position and dimension are set as zero.
	 */
	public TRenderableRaw(final Texture texture) {
		this.texture = texture;
		x      = 0;
		y      = 0;
		width  = 0;
		height = 0;
	}
	
	@Override
	public boolean shouldCull(final OrthographicCamera camera) {
		return false;
	}
	
	@Override
	public void render(final OrthographicCamera camera, final SpriteBatch batch, boolean... trans) {
		batch.draw(texture, x, y, width, height);
	}
	
	@Override
	public float getRenderX() {
		return x;
	}

	@Override
	public float getRenderY() {
		return y;
	}

	@Override
	public float getActualX() {
		return x;
	}

	@Override
	public float getActualY() {
		return y;
	}

	@Override
	public float getRenderWidth() {
		return width;
	}

	@Override
	public float getRenderHeight() {
		return height;
	}

	@Override
	public float getRotationInRadians() {
		return 0;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public int getDataSelectionOffsetX() {
		return 0;
	}

	@Override
	public int getDataSelectionOffsetY() {
		return 0;
	}

	@Override
	public int getDataSelectionSquareWidth() {
		return 0;
	}

	@Override
	public int getDataSelectionSquareHeight() {
		return 0;
	}

	@Override
	public Color getRenderTint() {
		return null;
	}

}
