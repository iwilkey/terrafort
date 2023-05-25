package dev.iwilkey.terrafort.asset;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.asset.registers.TextureRegister;
import dev.iwilkey.terrafort.asset.registers.VoxelRegister;
import dev.iwilkey.terrafort.particle.Particle;
import dev.iwilkey.terrafort.state.game.interaction.SpatialSelection;
import dev.iwilkey.terrafort.state.game.space.GridSystem;

public final class TerrafortAssetHandler implements Disposable {
	
	private static final HashMap<String, Model> MODEL_MEMORY = new HashMap<>();
	private static final HashMap<String, Texture> TEXTURE_MEMORY = new HashMap<>();
	private final TerrafortEngine engine;
	private final long totalAssets;
	private long loadedAssets = 0L;
	private boolean isFinished = false;
	
	public TerrafortAssetHandler(final TerrafortEngine engine) {
		this.engine = engine;
		totalAssets = VoxelRegister.values().length + TextureRegister.values().length;
	}
	
	public TerrafortEngine getEngine() {
		return engine;
	}

	public void load() {
		System.out.println("[Terrafort Engine] Beginning Terrafort Asset Manager loadtime.");
		
		// Load textures.
		for(TextureRegister texture : TextureRegister.values()) {
			String name = texture.getFileHandle().name();
			if(TEXTURE_MEMORY.containsKey(name)) {
				System.out.println("[Terrafort Engine] You cannot load two Textures of the same name! \"" + name + "\"");
				System.exit(-1);
			}
			TEXTURE_MEMORY.put(name, new Texture(texture.getFileHandle()));
			System.out.println("[Terrafort Engine] Loaded Texture \"" + name + "\".");
			loadedAssets++;
		}
		// Process and load Voxel models.
		for(VoxelRegister voxel : VoxelRegister.values()) {
			String name = voxel.getFileHandle().name();
			if(MODEL_MEMORY.containsKey(name)) {
				System.out.println("[Terrafort Engine] You cannot load two Voxel Models of the same name! \"" + name + "\"");
				System.exit(-1);
			}
			MODEL_MEMORY.put(name, VoxelModelLoader.buildModelFromRaw(name, 
					voxel.getFileHandle().readString(), 
					voxel.getScale(), 
					voxel.getRenderingPrimitive()));
			System.out.println("[Terrafort Engine] Loaded Voxel Model \"" + name + "\".");
			loadedAssets++;
		}
		
		// Load utility models: Space segmentation grid.
		MODEL_MEMORY.put("tf_space_seg", GridSystem.createSegmentationGridModel());
		// Load utility models: Building handler selection.
		MODEL_MEMORY.put("tf_building_handler_selection", SpatialSelection.createBuildingHandlerSelectionModel());
		// Load utility models: Particle.
		MODEL_MEMORY.put("tf_particle", Particle.createParticleModel());
		
		// All assets are loaded.
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
