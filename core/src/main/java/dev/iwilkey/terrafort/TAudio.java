package dev.iwilkey.terrafort;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

/**
 * The main audio interface of the Terrafort engine.
 * @author Ian Wilkey (iwilkey)
 */
public final class TAudio implements Disposable {
	
	private static final HashMap<String, Sound> SFX_MEMORY = new HashMap<>();
	
	/**
	 * Default sounds loaded at beginning of runtime.
	 */
	static {
		
		TAudio.mAllocFx("sound/give_funds.wav");
		TAudio.mAllocFx("sound/leaves_hit.wav");
		
	}
	
	///////////////////////////////////////////////////////
	// BEGIN API
	///////////////////////////////////////////////////////
	
	/**
	 * Loads a new sound effect into memory. File cannot be over 1 MB.
	 */
	public static void mAllocFx(String path) {
		SFX_MEMORY.put(path, Gdx.audio.newSound(Gdx.files.internal(path)));
	}
	
	/**
	 * Unloads a sound effect from memory.
	 */
	public static void mFreeFx(String path) {
		if(!SFX_MEMORY.containsKey(path))
			return;
		SFX_MEMORY.get(path).dispose();
		SFX_MEMORY.remove(path);
	}
	
	/**
	 * Simple function to play a sound effect. Uses default parameters. Sound effect must be loaded using {@link TAudio}.mAllocFx(path).
	 * If "random" is true, the sound effect's pitch will be altered randomly for variety.
	 */
	public static void playFx(String path, boolean random) {
		if(!SFX_MEMORY.containsKey(path)) {
			System.err.println("The TAudio system is trying to play a sound effect that doesn't exist: " + path);
			return;
		}
		final Sound fx = SFX_MEMORY.get(path);
		fx.play(TEngine.getPref().masterVolume * TEngine.getPref().sfxVolume, (!random) ? 1f : (float)ThreadLocalRandom.current().nextDouble(0.8, 1.2), 0.0f);
	}
	
	///////////////////////////////////////////////////////
	// END API
	///////////////////////////////////////////////////////

	@Override
	public void dispose() {
		for(final String e : SFX_MEMORY.keySet())
			SFX_MEMORY.get(e).dispose();
		SFX_MEMORY.clear();
	}

}