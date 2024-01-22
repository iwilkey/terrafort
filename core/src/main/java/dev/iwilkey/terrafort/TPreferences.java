package dev.iwilkey.terrafort;

import java.io.Serializable;

import dev.iwilkey.terrafort.gui.lang.TLanguage;

/**
 * Client preferences for Terrafort. Non-volitile.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPreferences implements Serializable {

	private static final long serialVersionUID = 4419514984240775610L;
	
	/**
	 * The current locale of Terrafort.
	 */
	public TLanguage locale;
	
	/**
	 * The overall volume.
	 */
	public float masterVolume = 1.0f;
	
	/**
	 * The volume of the sound effects.
	 */
	public float sfxVolume = 1.0f;
	
	/**
	 * The volume of background music.
	 */
	public float musicVolume = 1.0f;
	
	/**
	 * Creates default preferences (if not found and loaded in persistent data.)
	 */
	public TPreferences() {
		locale       = TLanguage.English;
		masterVolume = 1.0f;
	}
	
}
