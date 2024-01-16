package dev.iwilkey.terrafort.gui.lang;

/**
 * An enumeration of all available languages of Terrafort.
 * @author Ian Wilkey
 */
public enum TLanguage {
	
	English("locale/english.lang"),
	Español("locale/spanish.lang"),
	Français("locale/french.lang"),
	Deutsch("locale/german.lang");
	
	private final String path;
	
	private TLanguage(String path) {
		this.path = path;
	}
	
	/**
	 * The path to the .lang file.
	 */
	public final String getLocalePath() {
		return path;
	}
	
}
