package dev.iwilkey.terrafort.gui.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import dev.iwilkey.terrafort.TEngine;

/**
 * Facilitates the parsing of the .lang locale file to provide Terrafort the ability to translate all rendered text into a different language.
 * @author Ian Wilkey
 */
public final class TLocale {
	
	/**
	 * The location of all parsed languages. Populated at runtime. Should be indirectly accessed by public methods of the {@link TLocale} class.
	 */
	private static final HashMap<TLanguage, ArrayList<String>> LOCALE;
	
	static {
		LOCALE = new HashMap<>();
	    for (TLanguage lang : TLanguage.values()) {
	        final FileHandle langFile = Gdx.files.internal(lang.getLocalePath());
	        if(langFile.exists()) {
	            final ArrayList<String> lines = new ArrayList<>();
	            try {
	                String fileContent = langFile.readString("UTF-8");
	                String[] lineArray = fileContent.split("\\r?\\n");
	                lines.addAll(Arrays.asList(lineArray));
	            } catch (Exception e) {
	                Gdx.app.error("TLocale", "Error reading language file for: " + lang, e);
	            }
	            LOCALE.put(lang, lines);
	        } else {
	            Gdx.app.error("TLocale", "Language file does not exist for: " + lang);
	        }
	    }
	}
	
	/**
	 * Returns the line from the current .lang file for rendering or parsing. Use 1-indexing! For example, the first line of the .lang file is 1 NOT 0.
	 */
	public static String getLine(int line) {
		line--;
		final ArrayList<String> lang = LOCALE.get(TEngine.getPref().locale);
		if(line < 0 || line >= lang.size())
			throw new IllegalArgumentException("[Terrafort Game Engine] You are referencing a line number that does not exist in the .lang file! Line: " + line);
		return lang.get(line);
	}

}
