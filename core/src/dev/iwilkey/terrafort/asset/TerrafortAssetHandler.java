package dev.iwilkey.terrafort.asset;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.TerrafortEngine;

public final class TerrafortAssetHandler implements Runnable, Disposable {
	
	private static final HashMap<String, Model> MODEL_MEMORY = new HashMap<>();
	private static final HashMap<String, Texture> TEXTURE_MEMORY = new HashMap<>();
	
	private final TerrafortEngine engine;
	
	private long totalAssets = 0L;
	private long loadedAssets = 0L;
	private boolean isFinished = false;
	
	public TerrafortAssetHandler(final TerrafortEngine engine) {
		this.engine = engine;
	}
	
	public TerrafortEngine getEngine() {
		return engine;
	}
	
	private Array<FileHandle> registerTextures() {
		Array<FileHandle> textures = new Array<>();
		textures.add(Gdx.files.internal("texture/crosshair.png"));
		return textures;
	}
	
	private Array<FileHandle> registerVoxelModels() {
		Array<FileHandle> voxels = new Array<>();
		voxels.add(Gdx.files.internal("voxel/cube.txt"));
		return voxels;
	}

	@Override
	public void run() {
		System.out.println("[Terrafort Engine] Beginning Terrafort Asset Manager loadtime.");
		Array<FileHandle> textures = registerTextures();
		Array<FileHandle> voxels = registerVoxelModels();
		totalAssets = textures.size + voxels.size;
		// Load textures.
		for(FileHandle t : textures) {
			if(TEXTURE_MEMORY.containsKey(t.name())) {
				System.out.println("[Terrafort Engine] You cannot load two Textures of the same name! \"" + t.name() + "\"");
				System.exit(-1);
			}
			TEXTURE_MEMORY.put(t.name(), new Texture(t));
			System.out.println("[Terrafort Engine] Loaded Texture \"" + t.name() + "\".");
			loadedAssets++;
		}
		// Process and load Voxel models.
		for(FileHandle v : voxels) {
			if(MODEL_MEMORY.containsKey(v.name())) {
				System.out.println("[Terrafort Engine] You cannot load two Voxel Models of the same name! \"" + v.name() + "\"");
				System.exit(-1);
			}
			MODEL_MEMORY.put(v.name(), VoxelModelLoader.buildModelFromRaw(v.name(), v.readString()));
			System.out.println("[Terrafort Engine] Loaded Voxel Model \"" + v.name() + "\".");
			loadedAssets++;
		}
		isFinished = true;
		System.out.println("[Terrafort Engine] Terrafort Asset Manager loadtime successful.");
	}
	
	public long getTotalNumAssets() {
		return totalAssets;
	}
	
	public long getLoadedNumAssets() {
		return loadedAssets;
	}
	
	public float getPercentageDone() {
		return ((float)loadedAssets / totalAssets) * 100.0f;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public static Model getVoxelModel(String name) {
		if(!MODEL_MEMORY.containsKey(name)) {
			System.out.println("[Terrafort Engine] There is no loaded Terrafort Voxel Model by the name \"" + name + "\"");
			System.out.println("\tLoaded Voxel Models:");
			for(Map.Entry<String, Model> mod : MODEL_MEMORY.entrySet()) 
				System.out.println("\t\t-> " + mod.getKey());
			System.exit(-1);
		}
		return MODEL_MEMORY.get(name);
	}
	
	public static Texture getTexture(String name) {
		if(!TEXTURE_MEMORY.containsKey(name)) {
			System.out.println("[Terrafort Engine] There is no loaded Terrafort Texture by the name \"" + name + "\"");
			System.out.println("\tLoaded Textures:");
			for(Map.Entry<String, Texture> tex : TEXTURE_MEMORY.entrySet()) 
				System.out.println("\t\t-> " + tex.getKey());
			System.exit(-1);
		}
		return TEXTURE_MEMORY.get(name);
	}

	@Override
	public void dispose() {
		for(Map.Entry<String, Model> mod : MODEL_MEMORY.entrySet())
			mod.getValue().dispose();
		for(Map.Entry<String, Texture> tex : TEXTURE_MEMORY.entrySet()) 
			tex.getValue().dispose();
	}
	
}
