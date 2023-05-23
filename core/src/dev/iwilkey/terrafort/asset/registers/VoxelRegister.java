package dev.iwilkey.terrafort.asset.registers;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Registers all Voxel models to be loaded and used during the Terrafort Engine runtime.
 * @author iwilkey
 */
public enum VoxelRegister {
	
	CORE("voxel/core.txt", 0.5f, GL20.GL_TRIANGLES),
	CUBE("voxel/cube.txt", 0.05f, GL20.GL_TRIANGLES);
	
	private FileHandle fileHandle;
	private float scale;
	private int renderingPrimitive;
	
	VoxelRegister(String path, float scale, int renderingPrimitive) {
		fileHandle = Gdx.files.internal(path);
		this.scale = scale;
		this.renderingPrimitive = renderingPrimitive;
	}
	
	public FileHandle getFileHandle() {
		return fileHandle;
	}
	
	public float getScale() {
		return scale;
	}
	
	public int getRenderingPrimitive() {
		return renderingPrimitive;
	}
	
}
