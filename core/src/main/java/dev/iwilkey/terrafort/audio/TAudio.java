package dev.iwilkey.terrafort.audio;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * The main audio interface of the Terrafort engine.
 * @author Ian Wilkey (iwilkey)
 */
public final class TAudio implements Disposable {
	
	private static final HashMap<String, Sound> SFX_MEMORY = new HashMap<>();
	
	///////////////////////////////////////////////////////
	// BEGIN API
	///////////////////////////////////////////////////////
	
	/**
	 * Loads a new sound effect into memory. File cannot be over 1 MB.
	 */
	public static void mallocfx(String path) {
		SFX_MEMORY.put(path, Gdx.audio.newSound(Gdx.files.internal(path)));
	}
	
	/**
	 * Unloads a sound effect from memory.
	 */
	public static void freefx(String path) {
		if(!SFX_MEMORY.containsKey(path))
			return;
		SFX_MEMORY.get(path).dispose();
		SFX_MEMORY.remove(path);
	}
	
	/**
	 * Simple function to play a sound effect. Uses default parameters. Sound effect must be loaded using {@link TAudio}.mallocfx(path).
	 */
	public static void playfx(String path) {
		if(!SFX_MEMORY.containsKey(path)) {
			System.err.println("The TAudio system is trying to play a sound effect that doesn't exist: " + path);
			return;
		}
		SFX_MEMORY.get(path).play();
	}
	
	///////////////////////////////////////////////////////
	// END API
	///////////////////////////////////////////////////////
	
	public void tick() {
		
	}

	@Override
	public void dispose() {
		for(final String e : SFX_MEMORY.keySet())
			freefx(e);
	}

}
