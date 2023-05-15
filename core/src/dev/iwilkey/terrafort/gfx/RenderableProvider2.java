package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public interface RenderableProvider2 {
	public TextureRegion getBindedRaster();
	public int getX();
	public int getY();
	public int getWidth();
	public int getHeight();
	public Color getTint();
}
