package dev.iwilkey.terrafort.persistent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * An engine module to facilitate the write and read operations of Terrafort persistent data.
 */
public final class TPersistent {
	
	public static final String ROOT = "./persistent/";
	
	/**
	 * Should be called at the invocation of the application. Creates the persistent directory if it does not exist.
	 */
	public TPersistent() {
		establish();
	}
	
	/**
	 * Establishes a {@link FileHandle} to the root Terrafort persistent directory. Creates it if it does not exist.
	 */
	private static FileHandle establish() {
		final FileHandle root = Gdx.files.local(ROOT);
		if(!root.exists()) 
			root.mkdirs();
		return root;
	}
	
	/**
	 * Establishes a link to a child directory or file of the persistent directory. Creates it if it does not exist.
	 */
	public static FileHandle establish(String target, boolean file) {
		if(target.charAt(target.length() - 1) != '/' && !file)
			target += '/';
		else if(target.charAt(target.length() - 1) == '/' && file)
			target = target.substring(0, target.length() - 1);
		final FileHandle handle = Gdx.files.local(ROOT + target);
		if(!handle.exists() && !file) 
			handle.mkdirs();
		return handle; 
	}
	
	/**
	 * Returns whether or not a specified subdirectory exists in the Terrafort persistent file structure.
	 */
	public static boolean directoryExists(String subdir) {
		if(subdir.charAt(subdir.length() - 1) != '/')
			subdir += '/';
		final FileHandle handle = Gdx.files.local(ROOT + subdir);
		return handle.exists();
	}
	
	/**
	 * Save a {@link TSerializable} object to the persistent data directory with path given.
	 */
	public static void save(TSerializable object, String path) {
		try(final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOT + path))) {
	        oos.writeObject(object);
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a {@link TSerializable} object from persistent memory with path given.
	 */
	public static TSerializable load(String path) {
		try(final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ROOT + path))) {
	        try {
				return (TSerializable)ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
