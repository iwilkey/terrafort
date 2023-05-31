package dev.iwilkey.terrafort.asset.registers;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Registers all Voxel models to be loaded and used during the Terrafort Engine runtime.
 * Each entry in the enum represents a voxel model with its file path, scale, and rendering primitive.
 * 
 * The file path refers to the location of the voxel model data.
 * The scale determines the size of the voxel model.
 * The rendering primitive specifies the rendering mode for the voxel model.
 * 
 * @author iwilkey
 */
public enum VoxelModels {
    
    SPITTER_MK1("voxel/spitter_mk1", 1.0f / 12.0f, GL20.GL_TRIANGLES);
    
    private FileHandle fileHandle;
    private float scale;
    private int renderingPrimitive;
    
    /**
     * Constructs a VoxelRegister entry with the specified file path, scale, and rendering primitive.
     *
     * @param path                the file path of the voxel model
     * @param scale               the scale of the voxel model
     * @param renderingPrimitive  the rendering primitive of the voxel model
     */
    VoxelModels(String path, float scale, int renderingPrimitive) {
        fileHandle = Gdx.files.internal(path);
        this.scale = scale;
        this.renderingPrimitive = renderingPrimitive;
    }
    
    /**
     * Retrieves the file handle of the voxel model.
     *
     * @return the file handle
     */
    public FileHandle getFileHandle() {
        return fileHandle;
    }
    
    /**
     * Retrieves the scale of the voxel model.
     *
     * @return the scale
     */
    public float getScale() {
        return scale;
    }
    
    /**
     * Retrieves the rendering primitive of the voxel model.
     *
     * @return the rendering primitive
     */
    public int getRenderingPrimitive() {
        return renderingPrimitive;
    }
}
