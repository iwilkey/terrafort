package dev.iwilkey.terrafort.asset.registers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Registers all Textures to be loaded and used during the Terrafort Engine runtime.
 * @author iwilkey
 */
public enum TextureRegister {
	
	CROSSHAIR("texture/crosshair.png");
	
	private FileHandle fileHandle;
	
	TextureRegister(String path) {
		fileHandle = Gdx.files.internal(path);
	}
	
	public FileHandle getFileHandle() {
		return fileHandle;
	}
	
}
