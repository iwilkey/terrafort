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
		findContext();
		findDimensions();
	}
	
	private void findContext() {
		// Sets each Voxel's context. TODO: This is O(n^2). It would be beneficial to find a solution with lower complexity.
		for(int i = 0; i < voxels.size; i++) {
			for(int j = 0; j < voxels.size; j++) {
				if(i != j) {
					final int v1x = voxels.get(i).getX();
					final int v1y = voxels.get(i).getY();
					final int v1z = voxels.get(i).getZ();
					final int v2x = voxels.get(j).getX();
					final int v2y = voxels.get(j).getY();
					final int v2z = voxels.get(j).getZ();
					if(v1x == v2x && v1y == v2y + 1 && v1z == v2z)
						voxels.get(i).setHasTopNeighbor(true);
					if(v1x == v2x && v1y == v2y - 1 && v1z == v2z)
						voxels.get(i).setHasBottomNeighbor(true);
					if(v1x == v2x && v1y == v2y && v1z == v2z + 1)
						voxels.get(i).setHasForwardNeighbor(true);
					if(v1x == v2x && v1y == v2y && v1z == v2z - 1)
						voxels.get(i).setHasBackwardNeighbor(true);
					if(v1x == v2x + 1 && v1y == v2y && v1z == v2z)
						voxels.get(i).setHasRightNeighbor(true);
					if(v1x == v2x - 1 && v1y == v2y && v1z == v2z)
						voxels.get(i).setHasLeftNeighbor(true);
				}
			}
		}
	}

	private void findDimensions() {
		int maxX = Integer.MIN_VALUE;
		int minX = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxZ = Integer.MIN_VALUE;
		int minZ = Integer.MAX_VALUE;
		for(final Voxel vox : voxels) {
			int x = vox.getX();
			int y = vox.getY();
			int z = vox.getZ();
			if(x < minX) minX = x;
			if(x > maxX) maxX = x;
			if(y < minY) minY = y;
			if(y > maxY) maxY = y;
			if(z < minZ) minZ = z;
			if(z > maxZ) maxZ = z;
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
