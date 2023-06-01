package dev.iwilkey.terrafort.asset.registers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Registers all Textures to be loaded and used during the Terrafort Engine runtime.
 * Each entry in the enum represents a texture file path.
 * 
 * @author iwilkey
 */
public enum Textures {
    
    /**
     * The crosshair texture.
     */
    CROSSHAIR("texture/crosshair.png"),
    PLANET("texture/planet.png");

    private FileHandle fileHandle;

    /**
     * Constructs a TextureRegister entry with the specified file path.
     *
     * @param path the file path of the texture
     */
    Textures(String path) {
        fileHandle = Gdx.files.internal(path);
    }

    /**
     * Retrieves the file handle of the texture.
     *
     * @return the file handle
     */
    public FileHandle getFileHandle() {
        return fileHandle;
    }
}