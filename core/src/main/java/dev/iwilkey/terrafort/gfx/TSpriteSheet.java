package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.utils.Disposable;

/**
 * A reference to a segment of memory that contains an organized arrangement of Sprites.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSpriteSheet implements Disposable {
	
	private final Texture data;
	
	/**
	 * Creates a new sheet from given internal path.
	 */
	public TSpriteSheet(String internalPath) {
		data = new Texture(Gdx.files.internal(internalPath));
		data.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
	}
	
	/**
	 * Returns the loaded texture.
	 */
	public Texture get() {
		return data;
	}
	
	@Override
	public void dispose() {
		data.dispose();
	}
	
}
