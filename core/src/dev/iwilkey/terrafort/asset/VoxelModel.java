package dev.iwilkey.terrafort.asset;

import com.badlogic.gdx.utils.Array;

public final class VoxelModel {
	
	private final Array<Voxel> voxels;
	private int width;
	private int height;
	private int depth;
	private float centerX;
	private float centerY;
	private float centerZ;
	
	public VoxelModel(final Array<Voxel> voxels) {
		this.voxels = voxels;
		findDimensions();
	}
	
	private void findDimensions() {
		int maxX = Integer.MIN_VALUE;
		int minX = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxZ = Integer.MIN_VALUE;
		int minZ = Integer.MAX_VALUE;
		for(Voxel vox : voxels) {
			int x = vox.getX();
			int y = vox.getY();
			int z = vox.getZ();
			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
			if(z < minZ)
				minZ = z;
			if(z > maxZ)
				maxZ = z;
		}
		width = maxX - minX;
		height = maxY - minY;
		depth = maxZ - minZ;
		centerX = maxX - (width / 2f);
		centerY = maxY - (height / 2f);
		centerZ = maxZ - (depth / 2f);
	}
	
	public Array<Voxel> getVoxels() {
		return voxels;
	}
	
	public boolean hasVoxelAt(int x, int y, int z) {
		for(Voxel vox : voxels) {
			if(vox.getX() == x && vox.getY() == y && vox.getZ() == z) {
				System.out.println("Yes, voxel!");
				return true;
			}
		}
		System.out.println("No Voxel!");
		return false;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public float getCenterX() {
		return centerX;
	}
	
	public float getCenterY() {
		return centerY;
	}
	
	public float getCenterZ() {
		return centerZ;
	}
	
}
