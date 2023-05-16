package dev.iwilkey.terrafort.state.openworld.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.gfx.RenderableProvider2;
import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.state.State;

public class Crosshair implements RenderableProvider2 {
	
	private State state;
	private TextureRegion crosshair;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Crosshair(State state, int width, int height) {
		this.state = state;
		this.crosshair = new TextureRegion((Texture)state.getAssetManager().get("texture/crosshair.png"));
		this.x = Renderer.getWidth() / 2 - (width / 2);
		this.y = Renderer.getHeight() / 2 - (height / 2);
		this.width = width;
		this.height = height;
		this.state.getProvider2().add(this);
	}
	
	public void dispose() {
		state.getProvider2().removeValue(this, false);
	}
	
	@Override
	public TextureRegion getBindedRaster() {
		return crosshair;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Color getTint() {
		return Color.WHITE;
	}
	
}
