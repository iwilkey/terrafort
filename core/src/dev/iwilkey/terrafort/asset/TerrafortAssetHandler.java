package dev.iwilkey.terrafort.asset;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.Terrafort;
import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.asset.registers.Textures;
import dev.iwilkey.terrafort.asset.registers.VoxelModels;

/**
 * The TerrafortAssetHandler class manages the loading and retrieval of game assets for Terrafort.
 * It implements the Disposable interface to handle resource disposal.
 */
public final class TerrafortAssetHandler implements Disposable {

    private static final HashMap<String, Model> MODEL_MEMORY = new HashMap<>();
    private static final HashMap<String, Texture> TEXTURE_MEMORY = new HashMap<>();

    private final TerrafortEngine engine;
    private final long totalAssets;

    private long loadedAssets = 0L;
    private boolean isFinished = false;

    /**
     * Constructs a new TerrafortAssetHandler with the specified TerrafortEngine instance.
     *
     * @param engine the TerrafortEngine instance
     */
    public TerrafortAssetHandler(final TerrafortEngine engine) {
        this.engine = engine;
        totalAssets = VoxelModels.values().length + Textures.values().length;
    }

    /**
     * Retrieves the TerrafortEngine instance associated with this asset handler.
     *
     * @return the TerrafortEngine instance
     */
    public TerrafortEngine getEngine() {
        return engine;
    }

    /**
     * Loads the game assets.
     */
    public void load() {
        Terrafort.log("Beginning Terrafort Asset Manager loadtime");
        // Load textures.
        loadTextures();
        loadVoxelModels();
        loadGeometricModels();
        // All assets are loaded.
        isFinished = true;
        Terrafort.log("Terrafort Asset Manager loadtime successful.");
    }
    
    private void loadTextures() {
    	for (Textures texture : Textures.values()) {
            String name = texture.getFileHandle().name();
            if (TEXTURE_MEMORY.containsKey(name)) {
                Terrafort.fatal("You cannot load two Textures of the same name! \"" + name + "\"");
                System.exit(-1);
            }
            TEXTURE_MEMORY.put(name, new Texture(texture.getFileHandle()));
            Terrafort.log("Loaded Texture \"" + name + "\".");
            loadedAssets++;
        }
    }
    
    private void loadVoxelModels() {
    	// Process and load Voxel models.
        for (VoxelModels voxel : VoxelModels.values()) {
            String name = voxel.getFileHandle().name();
            if (MODEL_MEMORY.containsKey(name))
                Terrafort.fatal("You cannot load two Voxel Models of the same name! \"" + name + "\"");
            MODEL_MEMORY.put(name, VoxelModelLoader.buildModelFromRaw(name,
                    voxel.getFileHandle().readString(),
                    voxel.getScale(),
                    voxel.getRenderingPrimitive()));
            Terrafort.log("Loaded Voxel Model \"" + name + "\".");
            loadedAssets++;
        }
    }
    
    private void loadGeometricModels() {
    	MODEL_MEMORY.put("tf_sphere", GeometricModelLoader.createTexturedSphere(getTexture("crosshair.png")));
    }

    /**
     * Retrieves the total number of assets to be loaded.
     *
     * @return the total number of assets
     */
    public long getTotalNumAssets() {
        return totalAssets;
    }

    /**
     * Retrieves the number of loaded assets.
     *
     * @return the number of loaded assets
     */
    public long getLoadedNumAssets() {
        return loadedAssets;
    }

    /**
     * Calculates the percentage of assets that have been loaded.
     *
     * @return the percentage of assets loaded
     */
    public float getPercentageDone() {
        return ((float) loadedAssets / totalAssets) * 100.0f;
    }

    /**
     * Checks if asset loading is finished.
     *
     * @return true if asset loading is finished, false otherwise
     */
    public boolean isFinished() {
        return isFinished;
    }

    /**
     * Retrieves a loaded model by its name.
     *
     * @param name the name of the model
     * @return the model
     */
    public static Model getModel(String name) {
        if (!MODEL_MEMORY.containsKey(name)) {
            Terrafort.log("There is no loaded Terrafort Voxel Model by the name \"" + name + "\"");
            System.out.println("\tLoaded Voxel Models:");
            for (Map.Entry<String, Model> mod : MODEL_MEMORY.entrySet())
                System.out.println("\t\t-> " + mod.getKey());
            System.exit(-1);
        }
        return MODEL_MEMORY.get(name);
    }

    /**
     * Retrieves a Texture by its name.
     *
     * @param name the name of the Texture
     * @return the Texture
     */
    public static Texture getTexture(String name) {
        if (!TEXTURE_MEMORY.containsKey(name)) {
            Terrafort.log("There is no loaded Terrafort Texture by the name \"" + name + "\"");
            System.out.println("\tLoaded Textures:");
            for (Map.Entry<String, Texture> tex : TEXTURE_MEMORY.entrySet())
                System.out.println("\t\t-> " + tex.getKey());
            System.exit(-1);
        }
        return TEXTURE_MEMORY.get(name);
    }

    /**
     * Disposes of the assets and frees up resources.
     */
    @Override
    public void dispose() {
        for (Map.Entry<String, Model> mod : MODEL_MEMORY.entrySet())
            mod.getValue().dispose();
        for (Map.Entry<String, Texture> tex : TEXTURE_MEMORY.entrySet())
            tex.getValue().dispose();
    }

}
